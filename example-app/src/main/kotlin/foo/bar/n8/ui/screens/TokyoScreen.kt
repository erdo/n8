package foo.bar.n8.ui.screens

import androidx.compose.runtime.Composable
import co.early.n8.NavigationModel
import co.early.n8.compose.N8
import foo.bar.n8.ui.common.BtnSpec
import foo.bar.n8.ui.common.ScreenTemplate
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId

@Composable
fun TokyoScreen(
    n8: NavigationModel<Location, TabHostId> = N8.n8(),
) {

    val btns = listOf(
        BtnSpec(
            label = "Go To Seoul",
            clicked = { n8.navigateTo(Location.Seoul) }
        ),
        BtnSpec(
            label = "Go To New York",
            clicked = { n8.navigateTo(Location.NewYork) }
        ),
        BtnSpec(
            label = "Go Back x3 (or exit)",
            clicked = { n8.navigateBack(times = 3) }
        ),
    )

    ScreenTemplate(
        location = Location.Tokyo,
        buttons = btns,
        stateAsString = n8.toString(diagnostics = false)
    )
}
