package foo.bar.n8.ui.screens

import androidx.compose.runtime.Composable
import co.early.n8.N8
import co.early.n8.NavigationModel
import foo.bar.n8.ui.common.BtnSpec
import foo.bar.n8.ui.common.ScreenTemplate
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId

@Composable
fun ShanghaiScreen(
    n8: NavigationModel<Location, TabHostId> = N8.n8(),
) {

    val btns = listOf(
        BtnSpec(
            label = "Go To Damascus",
            clicked = { n8.navigateTo(Location.Damascus) }
        ),
        BtnSpec(
            label = "Go To Stockholm",
            clicked = { n8.navigateTo(Location.EuropeanLocations.Stockholm) }
        ),
    )

    ScreenTemplate(
        location = Location.Shanghai,
        buttons = btns,
    )
}
