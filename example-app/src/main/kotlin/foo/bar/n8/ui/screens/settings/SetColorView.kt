package foo.bar.n8.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import foo.bar.clean.ui.R
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.n8.ui.common.components.ViewTemplate
import foo.bar.n8.ui.common.components.elements.Btn
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.common.components.elements.BtnType
import foo.bar.n8.ui.common.components.elements.SW1
import foo.bar.n8.ui.theme.LocalAppColors

@Composable
fun SetColorView(
    perform: (Act) -> Unit = {},
    size: WindowSize = LocalWindowSize.current,
) {
    ViewTemplate(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
    ) {
        SetColor(
            size = size,
            setColor = { color -> perform(Act.ScreenSettings.SetColorAndBack(color)) },
        )
    }
}

@Composable
fun ColumnScope.SetColor(
    size: WindowSize = LocalWindowSize.current,
    setColor: (ULong?) -> Unit = {},
) {

    Fore.i("SetColor View")

    val red = Color.Red
    val green = Color.Green
    val orange = lerp(Color.Yellow, Color.Red, fraction = 0.5f)

    Btn(
        spec = BtnSpec(
            label = stringResource(id = R.string.settings_red),
            type = BtnType.CustomColour(red),
            clicked = { setColor(red.value) }
        ),
        modifier = Modifier.padding(SW1(size)),
    )

    Btn(
        spec = BtnSpec(
            label = stringResource(id = R.string.settings_green),
            type = BtnType.CustomColour(green),
            clicked = { setColor(green.value) }
        ),
        modifier = Modifier.padding(SW1(size)),
    )

    Btn(
        spec = BtnSpec(
            label = stringResource(id = R.string.settings_orange),
            type = BtnType.CustomColour(orange),
            clicked = { setColor(orange.value) }
        ),
        modifier = Modifier.padding(SW1(size)),
    )

    Btn(
        spec = BtnSpec(
            label = stringResource(id = R.string.settings_none),
            type = BtnType.CustomColour(LocalAppColors.current.paperOn),
            clicked = { setColor(null) }
        ),
        modifier = Modifier.padding(SW1(size)),
    )
}
