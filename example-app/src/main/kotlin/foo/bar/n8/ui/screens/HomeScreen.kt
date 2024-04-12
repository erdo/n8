package foo.bar.n8.ui.screens

import androidx.compose.runtime.Composable
import co.early.n8.NavigationModel
import co.early.n8.N8
import foo.bar.n8.ui.common.BtnSpec
import foo.bar.n8.ui.common.ScreenTemplate
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId
import foo.bar.n8.ui.navigation.tabHostSpecMain

@Composable
fun HomeScreen(
    n8: NavigationModel<Location, TabHostId> = N8.n8(),
) {

    val btns = listOf(
        BtnSpec(
            label = "Go To Tabs",
            clicked = { n8.switchTab(tabHostSpecMain, 0) }
        ),
    )

    ScreenTemplate(
        location = Location.Home,
        buttons = btns,
        stateAsString = n8.toString(diagnostics = false)
    )
}
