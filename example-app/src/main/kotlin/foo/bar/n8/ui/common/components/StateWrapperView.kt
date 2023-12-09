package foo.bar.n8.ui.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.HeightBasedDp
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WidthBasedTextUnit
import co.early.fore.ui.size.WindowSize
import foo.bar.clean.ui.R
import foo.bar.n8.ui.common.components.elements.Btn
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.common.components.elements.BtnType
import foo.bar.n8.ui.common.prettyPrint

val extraPaddingForHideUiBtn = HeightBasedDp(5.dp, 50.dp, 80.dp)

@Composable
fun StateWrapperView(
    state: Any,
    uiIsVisibleDefault: Boolean = true,
    size: WindowSize = LocalWindowSize.current,
    content: @Composable () -> Unit,
) {

    val show = remember { mutableStateOf(uiIsVisibleDefault) }

    AnimatedVisibility(
        visible = show.value,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
    ) {
        content()
    }

    val stateFontSize = WidthBasedTextUnit(
        xs = 12.sp,
        m = 20.sp,
        l = 35.sp
    )

    val stateAsString = state.prettyPrint()

    AnimatedVisibility(
        visible = !show.value,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stateAsString,
                style = TextStyle(fontSize = stateFontSize(size))
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        DisplayToggleView(
            displayed = show.value,
            size = size,
            toggleDisplayCallback = { show.value = !show.value },
        )
    }
}

@Composable
fun BoxScope.DisplayToggleView(
    displayed: Boolean,
    size: WindowSize = LocalWindowSize.current,
    toggleDisplayCallback: () -> Unit,
) {

    Fore.i("DisplayToggleView")

    val label = stringResource(id = if (displayed) R.string.hide_ui else R.string.show_ui)
    val alignment = if (size.isRound) Alignment.TopCenter else Alignment.TopEnd

    Btn(
        modifier = Modifier.align(alignment),
        spec = BtnSpec(
            label = label,
            clicked = toggleDisplayCallback,
            type = BtnType.Neutral
        )
    )
}
