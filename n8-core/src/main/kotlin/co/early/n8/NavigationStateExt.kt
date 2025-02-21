@file:OptIn(LowLevelApi::class)

package co.early.n8

import co.early.n8.Navigation.BackStack
import co.early.n8.Navigation.EndNode
import co.early.n8.Navigation.TabHost
import co.early.n8.lowlevel.LowLevelApi
import co.early.n8.lowlevel._populateChildParents

fun <L : Any, T : Any> backStackOf(
    vararg items: Navigation<L, T>,
): BackStack<L, T> {
    return BackStack(
        stack = items.toList(),
    )._populateChildParents()
}

/**
 * this lets us create a backStack 'that doesn't need a tabHost, without having to specify
 * explicitly the tabHost class Type (it just defaults it to Int)
 */
fun <L : Any> backStackNoTabsOf(
    vararg items: Navigation<L, Unit>,
): BackStack<L, Unit> {
    return backStackOf(*items)
}

fun <L : Any, T : Any> tabsOf(
    selectedTabHistory: List<Int> = listOf(0),
    tabHostId: T,
    vararg tabs: BackStack<L, T>,
): TabHost<L, T> {
    return TabHost(
        selectedTabHistory = selectedTabHistory,
        tabHostId = tabHostId,
        tabs = tabs.toList(),
    )._populateChildParents()
}

internal fun <L : Any, T : Any> tabsOf(
    tabHostSpec: TabHostSpecification<L, T>,
    initialTabOverride: Int? = null,
): TabHost<L, T> {
    return TabHost(
        selectedTabHistory = listOf(
            initialTabOverride ?: tabHostSpec.initialTab
        ),
        tabHostId = tabHostSpec.tabHostId,
        tabs = tabHostSpec.homeTabLocations.map {
            backStackOf(endNodeOf(it))
        },
        clearToTabRootDefault = tabHostSpec.clearToTabRoot,
        tabBackModeDefault = tabHostSpec.backMode,
    )._populateChildParents()
}

fun <L : Any, T : Any> endNodeOf(
    location: L,
): EndNode<L, T> {
    return EndNode(location)
}

fun <T> List<TabHostLocation<T>>.showSelected(tabHostId: T, index: Int): Boolean { //todo review this naming
    return firstOrNull { it.tabHostId == tabHostId }?.tabIndex == index
}
