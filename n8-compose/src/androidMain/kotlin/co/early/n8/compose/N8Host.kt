package co.early.n8.compose

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import co.early.fore.compose.observeAsState
import co.early.fore.core.delegate.Fore
import co.early.fore.core.observer.threadName
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
    content: @Composable (NavigationState<L, T>) -> Unit,
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

//    BackHandler(enabled = true) {
//        val intercepted = onBack?.invoke(navigationState) == true
//        if (!intercepted) {
//            if (navigationState.canNavigateBack) {
//                navigationModel.navigateBack()
//            } else {
//                this.finish()
//            }
//        }
//    }

    CompositionLocalProvider(LocalN8HostState provides navigationState) {
        content(navigationState)
    }
}
