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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WidthBasedTextUnit
import co.early.fore.ui.size.WindowSize
import foo.bar.n8.R
import foo.bar.n8.ui.common.elements.TextSpec
import foo.bar.n8.ui.common.elements.Txt

@Composable
fun StateWrapperView(
    stateAsString: String,
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

    val label = stringResource(id = if (displayed) R.string.show_state else R.string.hide_state)
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