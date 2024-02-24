package foo.bar.n8.ui.screens

import androidx.compose.runtime.Composable
import co.early.n8.NavigationModel
import co.early.n8.compose.N8
import foo.bar.n8.ui.common.BtnSpec
import foo.bar.n8.ui.common.ScreenTemplate
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId

@Composable
fun DamascusScreen(
    n8: NavigationModel<Location, TabHostId> = N8.n8(),
) {

    val btns = listOf(
        BtnSpec(
            label = "Go To Krakow",
            clicked = { n8.navigateTo(Location.EuropeanLocations.Krakow) }
        ),
        BtnSpec(
            label = "Go Back to Lagos (or to it)",
            clicked = { n8.navigateBackTo(Location.Lagos) }
        ),
    )

    ScreenTemplate(
        location = Location.Damascus,
        buttons = btns,
        stateAsString = n8.toString(diagnostics = false)
    )
}
