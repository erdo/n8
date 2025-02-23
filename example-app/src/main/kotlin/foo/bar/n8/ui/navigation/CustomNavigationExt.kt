@file:OptIn(LowLevelApi::class)

package foo.bar.n8.ui.navigation

import co.early.n8.Navigation
import co.early.n8.lowlevel.LowLevelApi
import co.early.n8.lowlevel._mutateNavigation

/**
 * trim the navigation graph from the top, so that the backsToExit value is no more than maxPathSize (effectively
 * what we're saying here is only remember x number of locations max)
 */
fun limitBackPath(maxPathSize: Int, nav: Navigation<Location, TabHostId>): Navigation<Location, TabHostId> {

    require(maxPathSize > 1) {
        "maxPathSize must be 2 or more"
    }

    return if (nav.topItem().backsToExit > maxPathSize) {
        when (nav) {
            is Navigation.BackStack -> {
                if (nav.stack.size == 1) {
                    limitBackPath(maxPathSize, nav.child)
                } else {
                    val newBackStack = nav.copy(
                        stack = nav.stack.takeLast(nav.stack.size - 1)
                    )
                    limitBackPath(
                        maxPathSize, _mutateNavigation(
                            oldItem = nav,
                            newItem = newBackStack
                        )
                    )
                }
            }
            is Navigation.TabHost -> {
                if (nav.tabHistory.size == 1) {
                    limitBackPath(maxPathSize, nav.child)
                } else {
                    val trimmed = if (nav.tabs[nav.tabHistory.first()].stack.size == 1){
                        limitBackPath(maxPathSize, _mutateNavigation(
                            oldItem = nav,
                            newItem = nav.copy(
                                tabHistory = nav.tabHistory.drop(1)
                            )
                        ))
                    } else {
                        limitBackPath(maxPathSize, nav.tabs[nav.tabHistory.first()])
                    }
                    if (trimmed.topItem().backsToExit > maxPathSize) {
                        limitBackPath(maxPathSize, trimmed)
                    } else {
                        trimmed
                    }
                }
            }
            is Navigation.EndNode -> nav
        }
    } else {
        nav
    }
}
