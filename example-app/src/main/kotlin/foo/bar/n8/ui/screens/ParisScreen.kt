package foo.bar.n8.ui.screens

import androidx.compose.runtime.Composable
import co.early.n8.NavigationModel
import co.early.n8.N8
import foo.bar.n8.ui.common.BtnSpec
import foo.bar.n8.ui.common.ScreenTemplate
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId

@Composable
fun ParisScreen(
    n8: NavigationModel<Location, TabHostId> = N8.n8(),
) {

    val btns = listOf(
        BtnSpec(
            label = "Go To Bangkok",
            clicked = { n8.navigateTo(Location.Bangkok) }
        ),
        BtnSpec(
            label = "Go To Dakar",
            clicked = { n8.navigateTo(Location.Dakar) }
        ),
        BtnSpec(
            label = "Go To London",
            clicked = { n8.navigateTo(Location.EuropeanLocations.London) }
        ),
    )

    ScreenTemplate(
        location = Location.EuropeanLocations.Paris,
        buttons = btns,
        stateAsString = n8.toString(diagnostics = false)
    )
}
