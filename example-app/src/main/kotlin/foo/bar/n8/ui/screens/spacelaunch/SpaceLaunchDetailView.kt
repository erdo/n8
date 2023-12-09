package foo.bar.n8.ui.screens.spacelaunch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import co.early.fore.ui.size.minimumDimension
import coil.compose.AsyncImage
import foo.bar.clean.domain.DomainError.NoError
import foo.bar.clean.ui.R
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.n8.ui.common.components.AlertOneButton
import foo.bar.n8.ui.common.components.CircularProgressIndicatorDelayed
import foo.bar.n8.ui.common.components.StateWrapperView
import foo.bar.n8.ui.common.components.ViewTemplate
import foo.bar.n8.ui.common.components.elements.Btn
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.common.components.elements.BtnType
import foo.bar.n8.ui.common.components.elements.SH3
import foo.bar.n8.ui.common.components.elements.SW1
import foo.bar.n8.ui.common.components.extraPaddingForHideUiBtn
import foo.bar.n8.ui.common.mapToUserMessage

@Composable
fun SpaceLaunchDetailView(
    viewState: SpaceDetailViewState,
    perform: (Act) -> Unit = {},
    size: WindowSize = LocalWindowSize.current,
) {
    StateWrapperView(
        state = viewState,
        size = size
    ) {
        ViewTemplate(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            SpaceLaunchDetail(
                viewState = viewState,
                size = size,
                refreshLaunchDetail = { id -> perform(Act.ScreenSpaceLaunch.RefreshLaunchDetail(id)) },
                bookLaunch = { perform(Act.ScreenSpaceLaunch.MakeBooking) },
                cancelBooking = { perform(Act.ScreenSpaceLaunch.CancelBooking) },
                signIn = { perform(Act.ScreenSpaceLaunch.SignIn) },
                signOut = { perform(Act.ScreenSpaceLaunch.SignOut) },
                clearErrors = { perform(Act.ScreenSpaceLaunch.ClearErrors) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.SpaceLaunchDetail(
    viewState: SpaceDetailViewState,
    size: WindowSize = LocalWindowSize.current,
    refreshLaunchDetail: (String) -> Unit,
    bookLaunch: () -> Unit,
    cancelBooking: () -> Unit,
    signIn: () -> Unit,
    signOut: () -> Unit,
    clearErrors: () -> Unit,
) {

    Fore.i("SpaceLaunchDetail View: $viewState")

    val minimumDimension = size.dpSize.minimumDimension()
    val patchHeight = minimumDimension * 0.70f
    val btnContainerHeight = minimumDimension * 0.3f

    Box(
        modifier = Modifier
            .width(patchHeight)
            .height(patchHeight)
            .padding(
                top = extraPaddingForHideUiBtn(size),
            )
    ) {
        if (viewState.loading) {
            CircularProgressIndicatorDelayed(
                modifier = Modifier
                    .align(Alignment.Center),
            )
        } else {
            AsyncImage(
                model = viewState.spaceDetailState.patchImgUrl,
                modifier = Modifier
                    .fillMaxWidth(),
                contentDescription = stringResource(
                    R.string.spacelaunch_selected_launch, viewState.spaceDetailState.site
                ),
            )
        }
    }

    Spacer(modifier = Modifier.height(SH3(size)))

    Text(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        text = stringResource(id = R.string.spacelaunch_token, viewState.auth.token)
    )

    Spacer(modifier = Modifier.height(SH3(size)))

    Box(
        modifier = Modifier
            .height(btnContainerHeight)
    ) {

        FlowRow(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
        ) {
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.spacelaunch_refresh),
                    clicked = { refreshLaunchDetail(viewState.spaceDetailState.launchId) }
                ),
                enabled = viewState.btnFetchEnabled,
                modifier = Modifier.padding(SW1(size)),
            )
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.spacelaunch_signin),
                    clicked = signIn
                ),
                enabled = viewState.btnSignInEnabled,
                modifier = Modifier.padding(SW1(size)),
            )
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.spacelaunch_signout),
                    clicked = signOut
                ),
                enabled = viewState.btnSignOutEnabled,
                modifier = Modifier.padding(SW1(size)),
            )
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.spacelaunch_book),
                    clicked = bookLaunch
                ),
                enabled = viewState.btnBookEnabled,
                modifier = Modifier.padding(SW1(size)),
            )
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.spacelaunch_cancel_booking),
                    clicked = cancelBooking
                ),
                enabled = viewState.btnCancelEnabled,
                modifier = Modifier.padding(SW1(size)),
            )
        }
    }

    viewState.spaceDetailState.error.takeUnless { it == NoError }?.let {
        AlertOneButton(
            title = stringResource(id = R.string.error),
            text = stringResource(id = it.mapToUserMessage()),
            confirmBtnSpec = BtnSpec(
                label = stringResource(id = R.string.btn_retry),
                clicked = { refreshLaunchDetail(viewState.spaceDetailState.launchId) },
                type = BtnType.Positive
            ),
            onDismissRequest = clearErrors,
        )
    }
}
