package foo.bar.n8.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.AspectBasedComposable
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.MinDimBasedTextUnit
import co.early.fore.ui.size.WindowSize
import foo.bar.n8.App
import foo.bar.n8.R
import foo.bar.n8.feature.ViewStateFlagModel
import foo.bar.n8.feature.ViewStateFlagModelState
import foo.bar.n8.feature.toState
import foo.bar.n8.ui.common.elements.TextSpec
import foo.bar.n8.ui.common.elements.Txt

@Composable
fun StateWrapperView(
    stateAsString: String,
    stateFlagModel: ViewStateFlagModel = App[ViewStateFlagModel::class],
    size: WindowSize = LocalWindowSize.current,
    content: @Composable () -> Unit,
) {

    val showStateFlag by stateFlagModel.toState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AspectBasedComposable(
            port = {
                StateWrapperPortrait(
                    stateAsString = stateAsString,
                    showStateFlag = showStateFlag,
                    content = content,
                    toggleCallback = { stateFlagModel.toggle() }
                )
            },
            land = {
                StateWrapperLandscape(
                    stateAsString = stateAsString,
                    content = content
                )
            }
        )(size)
    }
}

@Composable
private fun BoxScope.StateWrapperPortrait(
    stateAsString: String,
    showStateFlag: ViewStateFlagModelState,
    content: @Composable () -> Unit,
    toggleCallback: () -> Unit,
    size: WindowSize = LocalWindowSize.current,
) {
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.Center),
        visible = !showStateFlag.showState,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
    ) {
        content()
    }
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.Center),
        visible = showStateFlag.showState,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        StateView(stateAsString)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        StateToggle(
            hideButtonVisible = showStateFlag.showState,
            size = size,
            toggleDisplayCallback = toggleCallback,
        )
    }
}

@Composable
private fun StateWrapperLandscape(
    stateAsString: String,
    content: @Composable () -> Unit,
    size: WindowSize = LocalWindowSize.current,
) {
    Row {
        Box(
            modifier = Modifier
                .width(size.dpSize.width / 3),
            contentAlignment = Alignment.TopCenter
        ) {
            content()
        }
        StateView(stateAsString)
    }
}

@Composable
fun StateView(
    stateAsString: String,
    size: WindowSize = LocalWindowSize.current,
) {
    val stateFontSize = MinDimBasedTextUnit(
        xs = 12.sp,
        m = 20.sp,
        l = 35.sp
    )

    val scrollState = rememberScrollState()

    Fore.e("rememberScrollState(): ${scrollState.value}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = stateAsString,
            style = TextStyle(fontSize = stateFontSize(size))
        )
    }
}

@Composable
fun BoxScope.StateToggle(
    hideButtonVisible: Boolean,
    size: WindowSize = LocalWindowSize.current,
    toggleDisplayCallback: () -> Unit,
) {

    Fore.i("StateToggle")

    val label =
        stringResource(id = if (hideButtonVisible) R.string.hide_state else R.string.show_state)
    val alignment = if (size.isRound) Alignment.TopCenter else Alignment.TopEnd

    OutlinedButton(
        modifier = Modifier.align(alignment),
        colors = ButtonDefaults.textButtonColors(),
        onClick = toggleDisplayCallback,
        border = BorderStroke(
            width = 1.dp,
            color = ButtonDefaults.textButtonColors().contentColor,
        ),
        shape = ButtonDefaults.textShape,
        enabled = true,
    ) {
        Txt(
            text = label,
            textSpec = TextSpec.P2(size)
        )
    }
}
