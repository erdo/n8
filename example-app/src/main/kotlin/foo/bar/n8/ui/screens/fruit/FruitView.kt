package foo.bar.n8.ui.screens.fruit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import co.early.fore.compose.observeAsState
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.AspectBasedValue
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WidthBasedValue
import co.early.fore.ui.size.WindowSize
import co.early.fore.ui.size.minimumDimension
import foo.bar.clean.domain.features.fruit.FruitState
import foo.bar.clean.domain.features.settings.DarkMode
import foo.bar.clean.domain.features.settings.SettingsModel
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.n8.ui.common.components.StateWrapperView
import foo.bar.n8.ui.common.components.ViewTemplate
import org.koin.compose.koinInject

@Composable
fun FruitView(
    fruitState: FruitState,
    perform: (Act) -> Unit = {},
    size: WindowSize = LocalWindowSize.current,
) {
    StateWrapperView(
        state = fruitState,
        size = size
    ) {
        ViewTemplate(horizontalAlignment = Alignment.Start) {
            Fruit(
                fruitState = fruitState,
                size = size,
                fetchFruit = { perform(Act.ScreenFruit.Fetch) },
                fetchFruitForceFail = { perform(Act.ScreenFruit.FetchSimulateFail) },
            )
        }
    }
}

@Composable
fun Fruit(
    fruitState: FruitState,
    size: WindowSize = LocalWindowSize.current,
    fetchFruit: () -> Unit = {},
    fetchFruitForceFail: () -> Unit = {},
    settingsModel: SettingsModel = koinInject(),
) {

    Fore.i("Fruit View")

    val minimumDimension = size.dpSize.minimumDimension()
    val borderThickness = minimumDimension * 0.10f
    val boxHeight = minimumDimension * 0.50f
    val numberFontSize = (minimumDimension / 5f).value.sp
    val buttonFontSize = (minimumDimension / 8f).value.sp
    val buttonSize = max(borderThickness * 3, 50.dp)
    val color = WidthBasedValue(
        xs = Color.Red,
        s = Color.Green,
        m = Color.Blue,
        l = Color.Magenta,
        xl = Color.Gray
    )
    val shape = AspectBasedValue(
        port = CircleShape,
        land = CircleShape,
        squarish = RectangleShape
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(boxHeight)
    ) {

        val darkMode = settingsModel.observeAsState { settingsModel.state.darkMode }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "override system settings")
            Switch(
                enabled = true,
                checked = darkMode.value != DarkMode.System,
                onCheckedChange = { overrideSystem ->
                    settingsModel.setDarkMode(
                        if (overrideSystem) {
                            DarkMode.Dark
                        } else DarkMode.System
                    )
                },
            )

            Text(text = "dark mode on")
            Switch(
                enabled = darkMode.value != DarkMode.System,
                checked = darkMode.value == DarkMode.Dark,
                onCheckedChange = { dark ->
                    settingsModel.setDarkMode(
                        if (dark) {
                            DarkMode.Dark
                        } else DarkMode.Light
                    )
                },
            )
        }
    }
}
