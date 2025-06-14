package co.early.n8.compose

import android.app.Activity
import android.os.Build
import android.window.BackEvent
import android.window.OnBackAnimationCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.early.fore.compose.observeAsState
import co.early.fore.core.delegate.Fore
import co.early.n8.N8
import co.early.n8.NavigationModel
import co.early.n8.NavigationState

val LocalN8HostState = compositionLocalOf<NavigationState<*, *>> {
        error("To access LocalN8Host, your compose code must be wrapped in a N8Host<L, T>{} " +
                "block, we'd suggest somewhere high up in the UI tree/hierarchy, just inside " +
                "setContent{}") }

/**
 * Top level navigation container for the app, anything wrapped inside this element will receive the
 * current page for rendering
 */
@Composable @Suppress("FunctionNaming")
fun <L : Any, T : Any> Activity.N8Host(
    navigationModel: NavigationModel<L, T> = N8.n8(),
    onBack: (suspend (NavigationState<L, T>) -> Boolean)? = null, // true = handled/blocked/intercepted
    content: @Composable (NavigationState<L, T>, Float) -> Unit,
) {

    val navigationState by navigationModel.observeAsState { navigationModel.state }

    val backEvent = remember { mutableStateOf(false) }

    if (onBack == null){
        BackHandler(navigationState.canNavigateBack) {
            navigationModel.navigateBack()
        }
    } else {
        BackHandler(enabled = true) {
            backEvent.value = true
        }
    }

    LaunchedEffect(backEvent.value) {
        if (backEvent.value) {
            val intercepted = onBack?.invoke(navigationState) == true

            if (!intercepted) {
                if (navigationState.canNavigateBack) {
                    navigationModel.navigateBack()
                } else {
                    this@N8Host.finish()
                }
            }
            backEvent.value = false
        }
    }

    var backProgress by remember { mutableFloatStateOf(0f) }

    PredictiveBack(navigationState, { navigationModel.navigateBack() }) { progress ->
        backProgress = progress
    }

    CompositionLocalProvider(LocalN8HostState provides navigationState) {
        content(navigationState, backProgress)
    }
}


@Composable
fun <L : Any, T : Any> Activity.PredictiveBack(
    navigationState: NavigationState<L, T>,
    navigateBack: () -> Unit = {},
    onBackProgress: (Float) -> Unit = {}
) {

    val canPop = navigationState.canNavigateBack

    DisposableEffect(canPop) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

            val callback = object : OnBackAnimationCallback {
                override fun onBackStarted(backEvent: BackEvent) {
                    onBackProgress(0f)
                }

                override fun onBackProgressed(backEvent: BackEvent) {
                    onBackProgress(backEvent.progress)
                }

                override fun onBackCancelled() {
                    onBackProgress(0f)
                }

                override fun onBackInvoked() {
                    onBackProgress(0f)
                    navigateBack()
                }
            }

            if (canPop) {
                onBackInvokedDispatcher.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    callback
                )
            }

            onDispose {
                onBackInvokedDispatcher.unregisterOnBackInvokedCallback(callback)
            }
        } else {
            onDispose { }
        }
    }
}
