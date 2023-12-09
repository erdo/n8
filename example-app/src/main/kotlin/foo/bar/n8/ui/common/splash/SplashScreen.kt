package foo.bar.n8.ui.common.splash

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import co.early.fore.compose.observeAsState
import co.early.fore.kt.core.delegate.Fore
import foo.bar.clean.domain.DomainError
import foo.bar.clean.domain.features.ReadableState
import foo.bar.clean.domain.features.init.InitModel
import foo.bar.clean.domain.features.init.InitState
import foo.bar.clean.domain.features.init.Step
import foo.bar.clean.ui.R
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.clean.ui.actionhandlers.screens.ActionHandlerSplashScreen
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

/**
 * Splash screen, looks similar to android's default Splash screen, but we add a determinate
 * LinearProgress bar after a second to keep the user interested.
 */
@Composable
fun Activity.SplashScreen(
    initStateProvider: ReadableState<InitState> = (koinInject() as InitModel),
    actionHandler: ActionHandlerSplashScreen = koinInject(),
    content: @Composable () -> Unit,
) {

    val initModelState by initStateProvider.observeAsState { initStateProvider.state }

    initModelState.step.let { step ->

        Fore.d("initialisatino step:$step")

        when (step) {
            is Step.Loading -> SplashLoading(step.progress)
            is Step.Ready -> {
                if (step.nagBeforeStart) {
                    SplashError(
                        title = stringResource(id = R.string.upgrade),
                        error = DomainError.UpgradeNag,
                        dismissed = { actionHandler.handle(Act.ScreenSplash.ClearUpgradeNag) },
                    )
                } else {
                    var show by rememberSaveable { mutableStateOf(false) }

                    LaunchedEffect(true) {
                        delay(10)
                        show = true
                    }

                    AnimatedVisibility(
                        visible = show,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        content()
                    }
                }
            }

            is Step.Error -> SplashError(
                title = stringResource(id = R.string.hmm),
                error = step.domainError,
                dismissed = {
                    if (step.domainError != DomainError.UpgradeForce) {
                        actionHandler.handle(Act.ScreenSplash.Retry)
                    }
                },
            )

            Step.Uninitialised -> {
                /** no op, the init model should be kicked off by the application class **/
            }
        }
    }
}
