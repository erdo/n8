package co.early.n8

import co.early.n8.Navigation.BackStack
import co.early.n8.Navigation.EndNode
import co.early.n8.Navigation.TabHost
import co.early.n8.RestrictedNavigation.NotBackStack

fun <T> backStackOf(
    vararg items: Navigation<T>,
): BackStack<T> {
    return BackStack(
        directParent = null,
        stack = items.toList(),
    ).populateParents()
}

fun <T> tabsOf(
    selectedTabHistory: List<Int> = listOf(0),
    vararg items: BackStack<T>,
): TabHost<T> {
    return TabHost(
        directParent = null,
        selectedTabHistory = selectedTabHistory,
        tabs = items.toList(),
    )
}

fun <T> endNodeOf(
    location: T,
): EndNode<T> {
    return EndNode(location)
}

internal fun <T> TabHost<T>.populateParents(): Navigation<T> {
    for (tab in tabs) {
        val backStack = tab.isBackStack()
        backStack.directParent = Parent(this)
        backStack.populateParents()
    }
    return this
}

internal fun <T> BackStack<T>.populateParents(): BackStack<T> {
    for (navigation in stack) {
        when (val notBackStack = navigation.notBackStack()) {
            is NotBackStack.IsEndNode -> notBackStack.value.directParent = Parent(this)
            is NotBackStack.IsTabHost -> {
                notBackStack.value.directParent = Parent(this)
                notBackStack.value.populateParents()
            }
        }
    }
    return this
}

internal fun <T> Navigation<T>.allChildrenHaveParent(): Boolean {
    return when (this) {
        is BackStack -> {
            stack.firstOrNull { navigation ->
                when (val notBackStack = navigation.notBackStack()) {
                    is NotBackStack.IsEndNode -> notBackStack.value.directParent != null
                    is NotBackStack.IsTabHost -> {
                        notBackStack.value.directParent != null && notBackStack.value.allChildrenHaveParent()
                    }
                }
            } == null
        }

        is TabHost -> {
            selectedTabHistory.firstOrNull { index ->
                val backStack = this.tabs[index]
                backStack.directParent != null && backStack.allChildrenHaveParent()
            } == null
        }

        is EndNode -> directParent != null
    }
}

/**
 * Mutates the navigation item by replacing the oldItem with the newItem, applying the change
 * all the way back up the chain, via parent references, finally returning the complete
 * mutated navigation graph
 *
 * NOTE 1: populateParents() MUST be called on the navigation graph BEFORE calling this
 * function initially (not required for subsequent recursive calls). This will already be the
 * case provided the navigation graph has been constructed using the helper
 * functions: backStackOf() tabHostOf() endNodeOf()
 *
 * NOTE 2: There are some restrictions placed on the mutation. Because of how a navigation
 * graph is structured (the top level item is always a BackStack, a BackStack's direct children
 * can only be EndNodes or TabHosts, a TabHost's direct children can only be BackStacks, it's
 * often not valid to change the item _type_ as part of a mutation operation). When the
 * mutated navigation item is re-integrated into the navigation graph in the same position,
 * its new type can make it invalid. The only type change that is safe
 * is to mutate a TabHost to an EndNode or vice versa. Any other type change will cause this
 * function to immediately throw a runtime exception
 *
 * @oldItem any item in the navigation graph, often (but not necessarily) this will be the item
 * at currentItem() as mutation travels backwards via parent references
 * @newItem the replacement for the oldItem, this may have different values, children or even be of
 * a different type (see above for restrictions)
 *
 * @returns the complete mutated navigation graph containing the newItem
 */
fun <T> mutateNavigation(
    oldItem: Navigation<T>,
    newItem: Navigation<T>,
): Navigation<T> {
    return oldItem.directParent?.invoke()
        ?.let { oldItemParent -> // only the top level item has no parent
            mutateNavigation(
                oldItem = oldItemParent,
                newItem = when (val oldParent =
                    oldItemParent.notEndNode()) { // EndNodes are NOT valid parents
                    is RestrictedNavigation.NotEndNode.IsBackStack -> {
                        oldParent.value.copy(
                            stack = oldParent.value.stack.toMutableList().map {
                                if (it == oldItem) { // TODO do we need to document this behaviour? - if there is more than one match in the back stack is it going to cause issues...
                                    newItem
                                } else {
                                    it
                                }
                            }
                        ).also { newParent ->
                            newParent.stack.toMutableList().map {
                                updateParent(it, Parent(newParent))
                            }
                        }
                    }

                    is RestrictedNavigation.NotEndNode.IsTabHost -> {
                        oldParent.value.copy(
                            tabs = oldParent.value.tabs.replaceAt(
                                oldParent.value.selectedTabHistory.last(),
                                newItem.isBackStack() // TabHosts can only contain BackStacks
                            )
                        ).also { newParent ->
                            newParent.tabs.toMutableList().map {
                                updateParent(it, Parent(newParent))
                            }
                        }
                    }
                }
            )
        } ?: newItem.isBackStack()
}

private fun <T> updateParent(item: Navigation<T>, newParent: Parent<T>): Navigation<T> {
    when (item) {
        is BackStack -> item.directParent = newParent
        is EndNode -> item.directParent = newParent
        is TabHost -> item.directParent = newParent
    }
    return item
}

fun <T> Navigation<T>.requireParent(): Navigation<T> {
    return directParent?.invoke() ?: throw RuntimeException(
        "We were expecting a non null directParent " +
                "here. Please file an issue, indicating the function called and the output of " +
                "toString(diagnostics=true)"
    )
}

fun <T> List<T>.replaceLast(replacement: T): List<T> {
    return replaceAt(size - 1, replacement)
}

fun <T> List<T>.replaceAt(index: Int, replacement: T): List<T> {
    return toMutableList().also {
        it.removeAt(index)
        it.add(index, replacement)
    }
}
