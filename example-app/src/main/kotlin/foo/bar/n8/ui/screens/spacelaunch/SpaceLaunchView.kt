package foo.bar.n8.ui.screens.spacelaunch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.MinDimBasedDp
import co.early.fore.ui.size.WindowSize
import coil.compose.AsyncImage
import foo.bar.clean.domain.DomainError.NoError
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.clean.ui.R
import foo.bar.n8.ui.common.components.AlertOneButton
import foo.bar.n8.ui.common.components.CircularProgressIndicatorDelayed
import foo.bar.n8.ui.common.components.StateWrapperView
import foo.bar.n8.ui.common.components.ViewTemplate
import foo.bar.n8.ui.common.components.elements.Btn
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.common.components.elements.BtnType
import foo.bar.n8.ui.common.components.elements.SW1
import foo.bar.n8.ui.common.components.elements.SH2
import foo.bar.n8.ui.common.components.elements.SW3
import foo.bar.n8.ui.common.components.elements.Txt
import foo.bar.n8.ui.common.components.extraPaddingForHideUiBtn
import foo.bar.n8.ui.common.mapToUserMessage
import foo.bar.n8.ui.theme.LocalAppColors

@Composable
fun SpaceLaunchView(
    viewState: SpaceLaunchViewState,
    perform: (Act) -> Unit = {},
    size: WindowSize = LocalWindowSize.current,
) {
    StateWrapperView(
        state = viewState.spaceLaunch,
        size = size
    ) {
        ViewTemplate(modifier = Modifier.fillMaxSize()) {
            SpaceLaunchList(
                viewState = viewState,
                refreshLaunches = { perform(Act.ScreenSpaceLaunch.RefreshLaunches) },
                selectLaunch = { id -> perform(Act.ScreenSpaceLaunch.SelectLaunch(id)) },
                clearError = { perform(Act.ScreenSpaceLaunch.ClearErrors) },
                clearData = { perform(Act.ScreenSpaceLaunch.ClearData) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.SpaceLaunchList(
    viewState: SpaceLaunchViewState,
    size: WindowSize = LocalWindowSize.current,
    refreshLaunches: () -> Unit,
    selectLaunch: (String) -> Unit,
    clearError: () -> Unit,
    clearData: () -> Unit,
) {

    Fore.i("SpaceLaunch View: $viewState")

    val iconSize = MinDimBasedDp(20.dp, 50.dp, 100.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = SH2(size))
    ) {

        if (viewState.loading) {
            CircularProgressIndicatorDelayed(
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            if (viewState.hasData) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = extraPaddingForHideUiBtn(size),
                            bottom = extraPaddingForHideUiBtn(size),
                        )
                ) {
                    this.items(
                        items = viewState.spaceLaunch.launches,
                        key = { launch -> launch.id },
                    ) { launch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LocalAppColors.current.paper)
                                .clickable { selectLaunch(launch.id) }
                                .padding(vertical = SH2(size)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = launch.patchThumbImgUrl,
                                modifier = Modifier
                                    .width(iconSize(size))
                                    .height(iconSize(size)),
                                contentDescription = stringResource(
                                    R.string.spacelaunch_launch_image, launch.id
                                ),
                            )
                            Txt(
                                text = "ID:${launch.id}... ${launch.site}",
                                modifier = Modifier
                                    .padding(start = SW3(size))
                            )
                        }
                    }
                }
            } else {
                Txt(
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.spacelaunch_nodata)
                )
            }
        }

        FlowRow(
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.spacelaunch_fetch),
                    clicked = refreshLaunches
                ),
                enabled = viewState.btnFetchEnabled,
                modifier = Modifier.padding(SW1(size)),
            )
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.btn_clear),
                    clicked = clearData
                ),
                enabled = viewState.btnClearEnabled,
                modifier = Modifier.padding(SW1(size)),
            )
        }
    }

    viewState.spaceLaunch.error.takeUnless { it == NoError }?.let {
        AlertOneButton(
            title = stringResource(id = R.string.error),
            text = stringResource(id = it.mapToUserMessage()),
            confirmBtnSpec = BtnSpec(
                label = stringResource(id = R.string.btn_retry),
                clicked = refreshLaunches,
                type = BtnType.Positive
            ),
            onDismissRequest = clearError,
        )
    }
}
