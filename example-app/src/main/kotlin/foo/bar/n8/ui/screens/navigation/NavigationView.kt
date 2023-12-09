package foo.bar.n8.ui.screens.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import co.early.n8.HOME_LOCATION
import co.early.n8.Location
import co.early.n8.NavigationState
import foo.bar.clean.ui.R
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.n8.ui.common.components.StateWrapperView
import foo.bar.n8.ui.common.components.ViewTemplate
import foo.bar.n8.ui.common.components.elements.Btn
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.common.components.elements.SH2
import foo.bar.n8.ui.common.components.elements.SH3
import foo.bar.n8.ui.common.components.elements.SW1
import foo.bar.n8.ui.common.components.elements.SW2
import foo.bar.n8.ui.common.components.extraPaddingForHideUiBtn
import kotlinx.serialization.json.Json

@Composable
fun NavigationView(
    viewState: NavigationState,
    perform: (Act) -> Unit = {},
    size: WindowSize = LocalWindowSize.current,
) {
    StateWrapperView(
        state = viewState,
        size = size
    ) {
        ViewTemplate(
            modifier = Modifier
                .padding(
                    top = extraPaddingForHideUiBtn(size),
                    start = SW2(size),
                    end = SW2(size),
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Navigation(
                viewState = viewState,
                size = size,
                updateBackStack = { backstack ->
                    perform(
                        Act.ScreenNavigation.UpdateBackstack(
                            backstack
                        )
                    )
                },
            )
        }
    }
}

@Composable
fun Navigation(
    viewState: NavigationState,
    size: WindowSize = LocalWindowSize.current,
    updateBackStack: (List<Location>) -> Unit = {},
) {

    Fore.i("Settings View")

    var text by rememberSaveable { mutableStateOf(viewState.export()) }

    Spacer(modifier = Modifier.height(SH3(size)))

    TextField(
        value = text,
        onValueChange = {
            text = it
        },
        minLines = 5,
    )

    Spacer(modifier = Modifier.height(SH3(size)))

    Btn(
        spec = BtnSpec(
            label = stringResource(id = R.string.btn_update),
            clicked = { updateBackStack(text.import()) }
        ),
        enabled = true,
        modifier = Modifier.padding(SW1(size)),
    )

    Spacer(modifier = Modifier.height(SH2(size)))
}


private fun NavigationState.export(): String {
    return try {
        Json.encodeToString(NavigationState.serializer(), this)
    } catch (e: Exception) {
        "Parsing exception: $e"
    }
}

private fun String.import(): List<Location> {
    return try {
        Json.decodeFromString(NavigationState.serializer(), this).backStack
    } catch (e: Exception) {
        listOf(HOME_LOCATION)
    }
}
