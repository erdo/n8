package foo.bar.n8.ui.screens

import androidx.compose.runtime.Composable
import co.early.n8.NavigationModel
import co.early.n8.N8
import foo.bar.n8.ui.common.BtnSpec
import foo.bar.n8.ui.common.ScreenTemplate
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId

@Composable
fun LagosScreen(
    n8: NavigationModel<Location, TabHostId> = N8.n8(),
) {

    val btns = listOf(
        BtnSpec(
            label = "Go To Mumbai",
            clicked = { n8.navigateTo(Location.Mumbai) }
        ),
        BtnSpec(
            label = "Go Back x1 (or exit)",
            clicked = { n8.navigateBack(times = 1) }
        ),
    )

    ScreenTemplate(
        location = Location.Lagos,
        buttons = btns,
    )
}
