package com.kmpfoo.android.ui.tabhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.early.n8.NavigationModel
import co.early.n8.NavigationState
import co.early.n8.TabHostLocation
import com.kmpfoo.android.OG
import com.kmpfoo.ui.navigation.Location
import com.kmpfoo.ui.navigation.TabHostId

private val navModel by lazy {
    OG[NavigationModel::class] as NavigationModel<Location, TabHostId>
}

@Composable
fun RootTabHostView(
    navigationState: NavigationState<Location, TabHostId>,
    wrappingTabHosts: List<TabHostLocation<TabHostId>>,
    depth: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    val tabHostLocation = wrappingTabHosts[depth]
    val tabHost = navigationState.locateTabHost(tabHostLocation.tabHostId)

    requireNotNull(tabHost){
        "this should never be null, it means the tabHost does not exist in the navigation graph yet, check your code"
    }

    val nestedContent = if (wrappingTabHosts.size > depth + 1) {
        { RootTabHostView(navigationState, wrappingTabHosts, depth + 1, modifier, content) }
    } else {
        content
    }

    when (tabHostLocation.tabHostId){
        TabHostId.EuropeTabHost -> {
            TabHostEurope(tabHost.tabHistory.last(), navModel, modifier, nestedContent)
        }
        TabHostId.GlobalTabHost -> {
            TabHostGlobal(tabHost.tabHistory.last(), navModel, modifier, nestedContent)
        }
    }
}
