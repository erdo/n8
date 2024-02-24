package foo.bar.n8.ui.screens

import androidx.compose.runtime.Composable
import co.early.n8.NavigationModel
import co.early.n8.compose.N8
import foo.bar.n8.ui.common.BtnSpec
import foo.bar.n8.ui.common.ScreenTemplate
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId

@Composable
fun SydneyScreen(
    n8: NavigationModel<Location, TabHostId> = N8.n8(),
) {

    val btns = listOf(
        BtnSpec(
            label = "Go To LA",
            clicked = { n8.navigateTo(Location.LA) }
        ),
        BtnSpec(
            label = "Go To Houston (no history)",
            clicked = { n8.navigateTo(Location.Houston, false) }
        ),
    )

    ScreenTemplate(
        location = Location.Sydney,
        buttons = btns,
        stateAsString = n8.toString(diagnostics = false)
    )
}
