package foo.bar.n8.ui.screens

import androidx.compose.runtime.Composable
import co.early.n8.NavigationModel
import co.early.n8.N8
import foo.bar.n8.ui.common.BtnSpec
import foo.bar.n8.ui.common.ScreenTemplate
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId

@Composable
fun LondonScreen(
    n8: NavigationModel<Location, TabHostId> = N8.n8(),
) {

    val btns = listOf(
        BtnSpec(
            label = "Go To Paris",
            clicked = { n8.navigateTo(Location.EuropeanLocations.Paris) }
        ),
        BtnSpec(
            label = "Go To Milan (no history)",
            clicked = { n8.navigateTo(Location.EuropeanLocations.Milan, false) }
        ),
    )

    ScreenTemplate(
        location = Location.EuropeanLocations.London,
        buttons = btns,
    )
}
