package foo.bar.n8.ui.screens

import androidx.compose.runtime.Composable
import co.early.n8.NavigationModel
import co.early.n8.compose.N8
import foo.bar.n8.ui.common.BtnSpec
import foo.bar.n8.ui.common.ScreenTemplate
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId

@Composable
fun SeoulScreen(
    n8: NavigationModel<Location, TabHostId> = N8.n8(),
) {

    val btns = listOf(
        BtnSpec(
            label = "Go To Dakar",
            clicked = { n8.navigateTo(Location.Dakar) }
        ),
        BtnSpec(
            label = "Go Back to Bangkok (or go to it)",
            clicked = { n8.navigateBackTo(Location.Bangkok) }
        ),
        BtnSpec(
            label = "Go To Milan",
            clicked = { n8.navigateTo(Location.EuropeanLocations.Milan) }
        ),
    )

    ScreenTemplate(
        location = Location.Seoul,
        buttons = btns,
        stateAsString = n8.toString(diagnostics = false)
    )
}
