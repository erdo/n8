package co.early.n8

import co.early.fore.kt.core.delegate.Fore
import co.early.n8.Navigation.BackStack
import co.early.n8.Navigation.EndNode
import co.early.n8.Navigation.TabHost
import co.early.n8.RestrictedNavigation.NotBackStack

private const val padStep: Int = 4

/**
 * this lets us create a backStack 'that doesn't need a tabHost, without having to specify
 * explicitly the tabHost class Type (it just defaults it to Int)
 */
fun <L> backStackNoTabsOf(
    vararg items: Navigation<L, Unit>,
): BackStack<L, Unit> {
    return backStackOf(*items)
}

fun <L, T> backStackOf(
    vararg items: Navigation<L, T>,
): BackStack<L, T> {
    return BackStack(
        stack = items.toList(),
    ).populateChildParents()
}

fun <L, T> tabsOf(
    selectedTabHistory: List<Int> = listOf(0),
    tabHostId: T,
    vararg tabs: BackStack<L, T>,
): TabHost<L, T> {
    return TabHost(
        selectedTabHistory = selectedTabHistory,
        tabHostId = tabHostId,
        tabs = tabs.toList(),
    ).populateChildParents()
}

internal fun <L, T> tabsOf(
    tabHostSpec: TabHostSpecification<L, T>,
    initialTab: Int = 0
): TabHost<L, T> {
    return TabHost(
        selectedTabHistory = listOf(initialTab),
        tabHostId = tabHostSpec.tabHostId,
        tabs = tabHostSpec.homeTabLocations.map {
            backStackOf(endNodeOf(it))
        },
    )
}

fun <L, T> endNodeOf(
    location: L,
): EndNode<L, T> {
    return EndNode(location)
}

/**
 * For a newly created navigation (especially one which has been copied and altered from an
 * original) the children may not realise who their new parent is, this function corrects that
 * information
 */
internal fun <L, T> Navigation<L, T>.populateChildParents(): Navigation<L, T> {
    return when (this) {
        is BackStack -> populateChildParents()
        is EndNode -> this
        is TabHost -> populateChildParents()
    }
}

/**
 * For a new TabHost (including one copied from an old TabHost with minor changes), each of the Tabs
 * will contain a BackStack that does not realise who its new parent is, this function corrects that
 * information
 */
internal fun <L, T> TabHost<L, T>.populateChildParents(): TabHost<L, T> {
    for (backStack in tabs) {
        backStack.parent = this
        backStack.populateChildParents()
    }
    return this
}

/**
 * For a new BackStack (including one copied from an old BackStack with minor changes), none of the
 * items in the Stack will realise who its new parent is, this function corrects that
 * information
 */
internal fun <L, T> BackStack<L, T>.populateChildParents(): BackStack<L, T> {
    for (navigation in stack) {
        when (val notBackStack = navigation.notBackStack()) {
            is NotBackStack.IsEndNode -> notBackStack.value.parent = this
            is NotBackStack.IsTabHost -> {
                notBackStack.value.parent = this
                notBackStack.value.populateChildParents()
            }
        }
    }
    return this
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
 * at currentItem() as mutation travels backwards via parent references. The parent value must be
 * valid as it will be adjusted to add the newItem as its child
 * @newItem the replacement for the oldItem, this may have different values, children or even be of
 * a different type (see above for restrictions). The parent value of the newItem will be ignored
 * and a new value set as part of the mutation
 *
 * @returns a new complete mutated navigation graph containing the newItem, with all parent and child
 * references updated
 */
fun <L, T> mutateNavigation(
    oldItem: Navigation<L, T>,
    newItem: Navigation<L, T>,
): Navigation<L, T> {

    Fore.d("mutateNavigation() OLD:$oldItem NEW:$newItem")

    val result = oldItem.parent?.let { oldItemParent -> // only the top level item has no parent
        val mutated = mutateNavigation(
            oldItem = oldItemParent,
            newItem = when (val oldParent =
                oldItemParent.notEndNode()) { // EndNodes are NOT valid parents
                is RestrictedNavigation.NotEndNode.IsBackStack -> {

                    Fore.d("mutateNavigation()... newItem is BackStack")

                    oldParent.value.copy(
                        stack = oldParent.value.stack.toMutableList().map {
                            Fore.d("mutateNavigation()... FIND stack item $it")
                            if (it === oldItem) {
                                Fore.d("mutateNavigation()... MATCH")
                                newItem// swap the item
                            } else {
                                it
                            }
                        }
                    ).also { newParent ->
                        newParent.stack.toMutableList().map {
                            updateParent(
                                it,
                                newParent
                            ) // all the entries in the parent stack need to reference their new parent
                        }
                    }
                }

                is RestrictedNavigation.NotEndNode.IsTabHost -> {

                    Fore.d("mutateNavigation()... newItem is Tabhost")

                    oldParent.value.copy(
                        tabs = oldParent.value.tabs.toMutableList().map {
                            Fore.d("mutateNavigation()... FIND tab item $it")
                            if (it === oldItem) {
                                Fore.d("mutateNavigation()... MATCH")
                                newItem.isBackStack() // swap the item, TabHosts can only contain BackStacks
                            } else {
                                it
                            }
                        }
                    ).also { newParent ->
                        newParent.tabs.toMutableList().map {
                            updateParent(
                                it,
                                newParent
                            ) // all the entries in the parent tabs need to reference their new parent
                        }
                    }
                }
            }
        )
        mutated
    } ?: newItem.populateChildParents()

    return result
}

private fun <L, T> updateParent(
    item: Navigation<L, T>,
    newParent: Navigation<L, T>
): Navigation<L, T> {
    when (item) {
        is BackStack -> item.parent = newParent
        is EndNode -> item.parent = newParent
        is TabHost -> item.parent = newParent
    }
    return item
}

fun <L, T> Navigation<L, T>.requireParent(): Navigation<L, T> {
    return parent ?: throw RuntimeException(
        "We were expecting a non null directParent " +
                "here. Please file an issue, indicating the function called and the output of " +
                "toString(diagnostics=true)"
    )
}

fun <L> List<L>.replaceLast(replacement: L): List<L> {
    return replaceAt(size - 1, replacement)
}

fun <L> List<L>.replaceAt(index: Int, replacement: L): List<L> {
    return toMutableList().also {
        it.removeAt(index)
        it.add(index, replacement)
    }
}

fun <L, T> BackStack<L, T>.addLocation(location: L): BackStack<L, T> {
    return copy(
        stack = stack.toMutableList().also { it.add(endNodeOf(location)) }
    ).populateChildParents()
}

fun <L, T> TabHost<L, T>.addLocationToCurrentTab(location: L): TabHost<L, T> {
    return copy(
        tabs = tabs.mapIndexed { index, backStack ->
            if (index == selectedTabHistory.last()) {
                backStack.addLocation(location)
            } else {
                backStack
            }
        }
    ).populateChildParents()
}

fun <T> List<TabHostLocation<T>>.showSelected(tabHostId: T, index: Int): Boolean {
    return firstOrNull { it.tabHostId == tabHostId }?.tabIndex == index
}

suspend fun <L : Any, T : Any> NavigationModel<L, T>.exportState(): String {
    Fore.d("exportState() ${this.state}")
    TODO()
}

suspend fun <L : Any, T : Any> NavigationModel<L, T>.importState(
    serializedState: String,
    addToHistory: Boolean = true
) {
    Fore.d("importState() addToHistory:$addToHistory")
    TODO()
//    val state = serializedState
//    this.reWriteNavigation(state, addToHistory = addToHistory)
}

internal fun <L, T> Navigation<L, T>.render(
    padding: Int = 0,
    builder: StringBuilder,
    incDiagnostics: Boolean,
    current: Boolean = false,
): StringBuilder {
    return when (this) {
        is BackStack -> render(padding, builder, incDiagnostics, current)
        is EndNode -> render(padding, builder, incDiagnostics, current)
        is TabHost -> render(padding, builder, incDiagnostics, current)
    }
}

private fun <L, T> EndNode<L, T>.render(
    padding: Int,
    builder: StringBuilder,
    incDiagnostics: Boolean,
    current: Boolean
): StringBuilder {
    with(builder) {
        repeat(padding) { append(" ") }
        append("endNodeOf(${location!!::class.simpleName})")
        if (incDiagnostics) {
            append(" [parent=${parent} child=${child}]")
            if (current) {
                append("     <--- Current Item")
            }
        } else {
            if (current) {
                append(" <---")
            }
        }
        return this
    }
}

private fun <L, T> TabHost<L, T>.render(
    padding: Int,
    builder: StringBuilder,
    incDiagnostics: Boolean,
    current: Boolean
): StringBuilder {
    var pad = padding
    with(builder) {
        repeat(pad) { append(" ") }
        pad += padStep
        append("tabsOf( ")
        if (incDiagnostics) {
            append("[tabs=${tabs.size} parent=${parent} child=${child}]")
        }
        append("\n")
        repeat(pad) { append(" ") }
        append("tabHostId = $tabHostId")
        append("\n")
        repeat(pad) { append(" ") }
        append("selectedTabHistory = listOf(")
        for (index in selectedTabHistory) {
            append("$index,")
        }
        setLength(length - 1)
        append("),\n")
        tabs.forEachIndexed { index, tab ->
            if (index == selectedTabHistory.last()) {
                tab.render(pad, builder, incDiagnostics, current)
            } else {
                tab.render(pad, builder, incDiagnostics, false)
            }
            append(",\n")
        }
        setLength(length - 2)
        append("\n")
        pad -= padStep
        repeat(pad) { append(" ") }
        append(")")
        return this
    }
}

private fun <L, T> BackStack<L, T>.render(
    padding: Int,
    builder: StringBuilder,
    incDiagnostics: Boolean,
    current: Boolean
): StringBuilder {
    var pad = padding
    with(builder) {
        repeat(pad) { append(" ") }
        pad += padStep
        append("backStackOf( ")
        if (incDiagnostics) {
            append("[stackSize=${stack.size} parent=${parent} child=${child}]")
        }
        append("\n")
        val lastIndex = stack.lastIndex
        stack.forEachIndexed { index, stackItem ->
            if (index == lastIndex) {
                stackItem.render(pad, builder, incDiagnostics, current)
            } else {
                stackItem.render(pad, builder, incDiagnostics, false)
            }
            append(",\n")
        }
        setLength(length - 2)
        append("\n")
        pad -= padStep
        repeat(pad) { append(" ") }
        append(")")
        return this
    }
}

/**
 * a copy of the navigation item which has had the back operation applied to it, for an
 * end node, this will be the navigation item with no changes (a back operation is not
 * valid for this type of navigation item)
 */
internal fun <L, T> Navigation<L, T>.createNavigatedBackCopy(): Navigation<L, T> {
    return when (this) {
        is BackStack -> createNavigatedBackCopy()
        is EndNode -> createNavigatedBackCopy()
        is TabHost -> createNavigatedBackCopy()
    }.populateChildParents()
}


private fun <L, T> EndNode<L, T>.createNavigatedBackCopy(): Navigation<L, T> {
    return this
}

private fun <L, T> TabHost<L, T>.createNavigatedBackCopy(): Navigation<L, T> {
    return if (specificItemCanNavigateBack()) {
        copy(selectedTabHistory = selectedTabHistory.toMutableList().also { it.removeLast() })
    } else this
}

/**
 * the returned navigation graph will need to have its parents updated
 * once the complete nav graph has been recreated by calling populateParents()
 * on the top level navigation item
 */
private fun <L, T> BackStack<L, T>.createNavigatedBackCopy(): Navigation<L, T> {
    return if (specificItemCanNavigateBack()) {
        copy(stack = stack.toMutableList().also { it.removeLast() })
    } else this
}

/**
 * Find a TabHost match anywhere in the navigation graph. If no match is found null is returned
 * Recursively works it's way down the navigation graph from exit to current
 * @param navigation start by sending the top most navigation item here
 */
internal fun <L, T> Navigation<L, T>.findTabHost(tabHostId: T): TabHost<L, T>? {
    return when (this) {
        is BackStack -> {
            stack.firstNotNullOfOrNull {
                it.findTabHost(tabHostId)
            }
        }

        is EndNode -> null
        is TabHost -> {
            if (this.tabHostId == tabHostId) {
                this
            } else {
                tabs.firstNotNullOfOrNull {
                    it.findTabHost(tabHostId)
                }
            }
        }
    }
}


/**
 * NOTE: populateParents() MUST be called on the navigation graph BEFORE calling this
 * function initially (not required for subsequent recursive calls)
 *
 * @navigation opportunities for navigating back will be looked for from
 * this point in the navigation graph, and up via parents (i.e. ignoring children), therefore
 * clients will typically start by sending currentItem() here
 *
 * @returns the complete new navigation graph after the back operation has been
 * performed or null if it was not possible to navigate further back in the graph
 */
internal fun <L, T> Navigation<L, T>.calculateBackStep(): Navigation<L, T>? {
    Fore.d("calculateBackStep() type:${this::class.simpleName} navigation:${this}")
    return if (specificItemCanNavigateBack()) {
        Fore.d("calculateBackStep()... item CAN navigate back")
        mutateNavigation(
            oldItem = this,
            newItem = this.createNavigatedBackCopy()
        )
    } else { // try to move up the chain
        Fore.d("calculateBackStep()... item CANNOT navigate back, (need to move up chain to parent) directParent:${parent}")
        parent?.calculateBackStep()
    }
}

