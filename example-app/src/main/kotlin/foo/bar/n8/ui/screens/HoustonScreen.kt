package foo.bar.n8.ui.screens

import androidx.compose.runtime.Composable
import co.early.n8.NavigationModel
import co.early.n8.N8
import foo.bar.n8.ui.common.BtnSpec
import foo.bar.n8.ui.common.ScreenTemplate
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId

@Composable
fun HoustonScreen(
    n8: NavigationModel<Location, TabHostId> = N8.n8(),
) {

    val btns = listOf(
        BtnSpec(
            label = "Go To Sydney",
            clicked = { n8.navigateTo(Location.Sydney) }
        ),
        BtnSpec(
            label = "Go Back to Dakar (or go to it)",
            clicked = { n8.navigateBackTo(Location.Dakar) }
        ),
    )

    ScreenTemplate(
        location = Location.Houston,
        buttons = btns,
    )
}
