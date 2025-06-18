package co.early.n8.compose

import android.app.Activity
import android.os.Build
import android.window.BackEvent
import android.window.OnBackAnimationCallback
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.early.fore.compose.observeAsState
import co.early.n8.N8
import co.early.n8.NavigationModel
import co.early.n8.NavigationState

val LocalN8HostState = compositionLocalOf<NavigationState<*, *>> {
        error("To access LocalN8Host, your compose code must be wrapped in a N8Host<L, T>{} " +
                "block, we'd suggest somewhere high up in the UI tree/hierarchy, just inside " +
                "setContent{}") }

/**
 * Top level navigation container for the app, anything wrapped inside this element will receive the
 * current state for rendering, and the peekBack state + backProgress for predictive back animation
 */
@Composable @Suppress("FunctionNaming")
fun <L : Any, T : Any> Activity.N8Host(
    navigationModel: NavigationModel<L, T> = N8.n8(),
    onBackCheck: (suspend (NavigationState<L, T>) -> Boolean)? = null, // true = handled/blocked/intercepted
    content: @Composable (NavigationState<L, T>, NavigationState<L, T>?, Float) -> Unit, // current state, peek back state, back progress
) {

    val navigationState by navigationModel.observeAsState { navigationModel.state }
    val canNavigateBack = navigationState.canNavigateBack
    var systemBackParams by remember { mutableStateOf(SystemBackParams()) }
    val legacyBackDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val backDispatcher = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { onBackInvokedDispatcher } else null

    DisposableEffect(canNavigateBack) {
        val systemCallback = when {
            // android 14, API 34+ full predictive back animation
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                object : OnBackAnimationCallback {
                    override fun onBackStarted(backEvent: BackEvent) {
                        systemBackParams = SystemBackParams()
                    }

                    override fun onBackProgressed(backEvent: BackEvent) {
                        systemBackParams = SystemBackParams(
                            backProgress = backEvent.progress.coerceIn(0f, 1f),
                        )
                    }

                    override fun onBackCancelled() {
                        systemBackParams = SystemBackParams()
                    }

                    override fun onBackInvoked() {
                        systemBackParams = systemBackParams.copy(backEvent = true)
                    }
                }.also { animationCallBack ->
                    backDispatcher?.registerOnBackInvokedCallback(
                        OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                        animationCallBack
                    )
                }
            }
            // android 13, API 33 - gesture back but no animation
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                OnBackInvokedCallback {
                    systemBackParams = SystemBackParams(backEvent = true)
                }.also { gestureCallback ->
                    backDispatcher?.registerOnBackInvokedCallback(
                        OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                        gestureCallback
                    )
                }
            }
            // android 12 / API 32 and below - just simple system back press
            else -> {
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        systemBackParams = SystemBackParams(backEvent = true)
                    }
                }.also { simpleCallback ->
                    legacyBackDispatcher?.addCallback(simpleCallback)
                }
            }
        }

        onDispose {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    onBackInvokedDispatcher.unregisterOnBackInvokedCallback(
                        systemCallback as OnBackAnimationCallback
                    )
                }

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    onBackInvokedDispatcher.unregisterOnBackInvokedCallback(
                        systemCallback as OnBackInvokedCallback
                    )
                }

                else -> (systemCallback as? OnBackPressedCallback?)?.remove()

            }
        }
    }

    LaunchedEffect(systemBackParams.backEvent) {
        if (systemBackParams.backEvent) {
            val intercepted = onBackCheck?.invoke(navigationState) == true

            if (!intercepted) {
                if (canNavigateBack) {
                    navigationModel.navigateBack()
                } else {
                    this@N8Host.finish()
                }
            }
            systemBackParams = SystemBackParams()
        }
    }

    val peekBackNavState = if (systemBackParams.backProgress != 0f) {
            navigationState.peekBack?.let { peekBack ->
                navigationState.copy(
                    navigation = peekBack,
                    comingFrom = null // we don't want to see any custom transition animation stuff in the peek back preview
                )
            }
        } else null

    CompositionLocalProvider(LocalN8HostState provides navigationState) {
        content(navigationState, peekBackNavState, systemBackParams.backProgress)
    }
}

private data class SystemBackParams(
    val backEvent: Boolean = false,
    val backProgress: Float = 0f,
)
