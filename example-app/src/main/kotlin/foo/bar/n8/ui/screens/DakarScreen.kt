package foo.bar.n8.ui.screens

import androidx.compose.runtime.Composable
import co.early.n8.NavigationModel
import co.early.n8.compose.N8
import foo.bar.n8.ui.common.BtnSpec
import foo.bar.n8.ui.common.ScreenTemplate
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId

@Composable
fun DakarScreen(
    n8: NavigationModel<Location, TabHostId> = N8.n8(),
) {

    val btns = listOf(
        BtnSpec(
            label = "Go To Bangkok",
            clicked = { n8.navigateTo(Location.Bangkok) }
        ),
        BtnSpec(
            label = "Go To Sydney (no history)",
            clicked = { n8.navigateTo(Location.Sydney, false) }
        ),
    )

    ScreenTemplate(
        location = Location.Dakar,
        buttons = btns,
        stateAsString = n8.toString(diagnostics = false)
    )
}
