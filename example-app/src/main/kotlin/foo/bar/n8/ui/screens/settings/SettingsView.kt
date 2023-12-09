package foo.bar.n8.ui.screens.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.MinDimBasedDp
import co.early.fore.ui.size.WindowSize
import foo.bar.clean.domain.features.settings.DarkMode
import foo.bar.clean.ui.R
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.n8.ui.common.components.StateWrapperView
import foo.bar.n8.ui.common.components.ViewTemplate
import foo.bar.n8.ui.common.components.elements.Btn
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.common.components.elements.SH2
import foo.bar.n8.ui.common.components.elements.SH4
import foo.bar.n8.ui.common.components.elements.SW1
import foo.bar.n8.ui.common.components.elements.SW3
import foo.bar.n8.ui.common.components.elements.Txt
import foo.bar.n8.ui.common.components.extraPaddingForHideUiBtn
import foo.bar.n8.ui.theme.LocalAppColors

@Composable
fun SettingsView(
    viewState: SettingsViewState,
    perform: (Act) -> Unit = {},
    size: WindowSize = LocalWindowSize.current,
) {

    StateWrapperView(
        state = viewState,
        size = size
    ) {
        ViewTemplate(
            modifier = Modifier
                .padding(top = extraPaddingForHideUiBtn(size))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Settings(
                viewState = viewState,
                size = size,
                setDarkMode = { darkMode -> perform(Act.ScreenSettings.SetDarkMode(darkMode)) },
                chooseColorBtnClicked = { perform(Act.ScreenSettings.ToSetColorScreen) },
            )
        }
    }
}

@Composable
fun Settings(
    viewState: SettingsViewState,
    size: WindowSize = LocalWindowSize.current,
    setDarkMode: (DarkMode) -> Unit = {},
    chooseColorBtnClicked: () -> Unit = {},
) {

    Fore.i("Settings View")

    val darkMode = viewState.settingsState.darkMode
    val selectedColor = viewState.color.toColor() ?: LocalAppColors.current.paperOn
    val colorSwatchSize = MinDimBasedDp(20.dp, 100.dp, 300.dp)

    Spacer(modifier = Modifier.height(SH2(size)))
    Txt(text = stringResource(id = R.string.settings_override_system))
    Switch(
        enabled = true,
        checked = darkMode != DarkMode.System,
        onCheckedChange = { overrideSystem ->
            setDarkMode(
                if (overrideSystem) {
                    DarkMode.Dark
                } else DarkMode.System
            )
        },
    )
    Txt(
        modifier = Modifier.padding(start = SW3(size)),
        text = stringResource(id = R.string.settings_dark_mode)
    )
    Switch(
        modifier = Modifier.padding(start = SW3(size)),
        enabled = darkMode != DarkMode.System,
        checked = darkMode == DarkMode.Dark,
        onCheckedChange = { dark ->
            setDarkMode(
                if (dark) {
                    DarkMode.Dark
                } else DarkMode.Light
            )
        },
    )
    Spacer(modifier = Modifier.height(SH4(size)))
    Txt(text = stringResource(id = R.string.settings_choose_color))
    Spacer(modifier = Modifier.height(SH2(size)))
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(
            modifier = Modifier.size(colorSwatchSize(size)),
            onDraw = {
                drawCircle(color = selectedColor)
            }
        )
        Spacer(modifier = Modifier.height(SH2(size)))
        Btn(
            spec = BtnSpec(
                label = stringResource(id = R.string.settings_color),
                clicked = chooseColorBtnClicked
            ),
            enabled = true,
            modifier = Modifier.padding(SW1(size)),
        )
    }
}

private fun ULong?.toColor(): Color? {
    return this?.let { Color(value = this) }
}
