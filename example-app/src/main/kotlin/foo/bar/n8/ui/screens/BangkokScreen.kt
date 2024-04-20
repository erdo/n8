package foo.bar.n8.ui.screens

import androidx.compose.runtime.Composable
import co.early.n8.N8
import co.early.n8.NavigationModel
import foo.bar.n8.ui.common.BtnSpec
import foo.bar.n8.ui.common.ScreenTemplate
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId

@Composable
fun BangkokScreen(
    n8: NavigationModel<Location, TabHostId> = N8.n8(),
) {

    val btns = listOf(
        BtnSpec(
            label = "Go To Shanghai",
            clicked = { n8.navigateTo(Location.Shanghai) }
        ),
        BtnSpec(
            label = "Go To Stockholm",
            clicked = { n8.navigateTo(Location.EuropeanLocations.Stockholm) }
        ),
        BtnSpec(
            label = "Go To NewYork (no history)",
            clicked = { n8.navigateTo(Location.EuropeanLocations.Stockholm, addToHistory = false) }
        ),
    )

    ScreenTemplate(
        location = Location.Bangkok,
        buttons = btns,
    )
}
