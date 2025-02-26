@file:Suppress("FunctionName")

package co.early.n8.lowlevel

import co.early.fore.kt.core.delegate.Fore
import co.early.n8.Navigation
import co.early.n8.Navigation.BackStack
import co.early.n8.Navigation.EndNode
import co.early.n8.Navigation.TabHost
import co.early.n8.lowlevel.RestrictedNavigation.NotBackStack
import co.early.n8.TabBackMode
import co.early.n8.endNodeOf

/**
 * The functions listed here are part of n8 library internals, and have been exposed publicly
 * because they can be useful for clients who want to develop their own custom behaviours
 * (advanced usage which most clients aren't going to need.)
 *
 * Unlike the other core functions (which are designed to be impossible to break), these
 * functions are much more easy to misuse, please pay close attention to the warnings and
 * explanations you'll find sprinkled about the source code.
 *
 * And remember: Fore.i(navigation.toString(diagnostics = true)) is your friend!
 */
@RequiresOptIn(message = "warning advanced usage only, please check source comments")
annotation class LowLevelApi

private const val padStep: Int = 4

/**
 * Mutates the navigation graph by replacing the oldItem with the newItem, applying the change
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
 * @ensureOnHistoryPath If ensureOnHistoryPath is true, the newItem will also be placed on the
 * path to the currentItem if it isn't already. Relevant for cases when the oldItem is not on the
 * history path but is still present somewhere in the navigation graph. Replacing oldItem with
 * newItem will mutate the navigation graph, but it will not necessarily change the history path
 * if the oldItem was not on the history path in the first place (which may or may not be what was
 * intended). ensureOnHistoryPath = true ensures that once the mutation is complete, the newItem is
 * accessible by the user on the back stack (or is the actual currentItem)
 *
 * @returns the top item of a new complete mutated navigation graph containing the newItem, with all
 * parent and child references updated
 */
@LowLevelApi
fun <L : Any, T : Any> _mutateNavigation(
    oldItem: Navigation<L, T>,
    newItem: Navigation<L, T>,
    ensureOnHistoryPath: Boolean = false,
): Navigation<L, T> {

    Fore.d("_mutateNavigation() OLD:$oldItem NEW:$newItem ensureOnHistoryPath:$ensureOnHistoryPath")

    val result = oldItem.parent?.let { oldItemParent -> // only the top level item has no parent
        _mutateNavigation(
            oldItem = oldItemParent,
            newItem = when (val oldParent =
                oldItemParent._notEndNode()) { // EndNodes are NOT valid parents

                is RestrictedNavigation.NotEndNode.IsBackStack -> {

                    Fore.d("mutateNavigation()... oldParent is BackStack")

                    oldParent.value.copy(
                        stack = if (ensureOnHistoryPath) {
                            oldParent.value.stack
                                .takeWhile { it !== oldItem }
                                .toMutableList()
                                .also { it.add(newItem) }
                        } else {
                            oldParent.value.stack.map {
                                if (it === oldItem) {
                                    newItem
                                } else it
                            }
                        }
                    )._populateChildParents()
                        .also { newParent ->  // all the entries in the parent stack need to reference their new parent
                            newParent.stack.map {
                                _updateParent(
                                    it,
                                    newParent
                                )
                            }
                        }
                }

                is RestrictedNavigation.NotEndNode.IsTabHost -> {

                    Fore.d("mutateNavigation()... oldParent is Tabhost")

                    val oldTabHostParent = oldParent.value

                    var tabIndex = -1
                    val newTabs = oldTabHostParent.tabs.mapIndexed { index, tab ->
                        if (tab === oldItem) {
                            tabIndex = index
                            newItem._isBackStack()
                        } else tab
                    }

                    require(tabIndex > -1) {
                        "It should be impossible reach here, but if we do it's a bug [2]. If you are NOT directly" +
                                "using @LowLevelApi please file an issue, including the state of the navigation " +
                                "graph just before the crash: 'navigationModel.toString(diagnostics = true)' and " +
                                "the operation performed. If on the other hand you ARE using @LowLevelApi to perform " +
                                "some custom navigation mutations, there is probably an incorrect assumption being " +
                                "made about the structure of the navigation graph, please check carefully the source " +
                                "code comments for the @LowLevelApi functions"
                    }

                    val newHistory = if (ensureOnHistoryPath) {
                        when (oldTabHostParent.tabBackModeDefault) {
                            TabBackMode.Structural -> {
                                listOf(tabIndex)
                            }

                            TabBackMode.Temporal -> {
                                oldTabHostParent.tabHistory.filter { tab ->
                                    tab != tabIndex
                                }.toMutableList().also { list ->
                                    list.add(tabIndex)
                                }
                            }
                        }
                    } else {
                        oldTabHostParent.tabHistory
                    }

                    oldParent.value.copy(
                        tabs = newTabs,
                        tabHistory = newHistory,
                    ).also { newParent ->
                        newParent.tabs.toMutableList().map {
                            _updateParent(
                                it,
                                newParent
                            ) // all the entries in the parent tabs need to reference their new parent
                        }
                    }
                }
            },
            ensureOnHistoryPath = ensureOnHistoryPath
        )
    } ?: newItem._populateChildParents()

    return result
}

/**
 * For a newly created navigation (especially one which has been copied and altered from an
 * original) the children may not realise who their new parent is, this function corrects that
 * information
 */
@LowLevelApi
fun <L : Any, T : Any> Navigation<L, T>._populateChildParents(): Navigation<L, T> {
    return when (this) {
        is BackStack -> _populateChildParents()
        is EndNode -> this
        is TabHost -> _populateChildParents()
    }
}

/**
 * For a new TabHost (including one copied from an old TabHost with minor changes), each of the Tabs
 * will contain a BackStack that does not realise who its new parent is, this function corrects that
 * information
 */
@LowLevelApi
fun <L : Any, T : Any> TabHost<L, T>._populateChildParents(): TabHost<L, T> {
    for (backStack in tabs) {
        backStack._parent = this
        backStack._populateChildParents()
    }
    return this
}

/**
 * For a new BackStack (including one copied from an old BackStack with minor changes), none of the
 * items in the Stack will realise who their new parent is, this function corrects that
 * information
 */
@LowLevelApi
fun <L : Any, T : Any> BackStack<L, T>._populateChildParents(): BackStack<L, T> {
    for (navigation in stack) {
        when (val notBackStack = navigation._notBackStack()) {
            is NotBackStack.IsEndNode -> notBackStack.value._parent = this
            is NotBackStack.IsTabHost -> {
                notBackStack.value._parent = this
                notBackStack.value._populateChildParents()
            }
        }
    }
    return this
}


/**
 * Any navigation graph can only contain unique TabHostIds, no duplicates are allowed
 */
@LowLevelApi
fun <L : Any, T : Any> Navigation<L, T>._ensureUniqueTabHosts(tabHostIds: MutableSet<T> = mutableSetOf()): Navigation<L, T> {
    return when (this) {
        is BackStack -> _ensureUniqueTabHosts(tabHostIds)
        is EndNode -> this
        is TabHost -> _ensureUniqueTabHosts(tabHostIds)
    }
}

/**
 * Any navigation graph can only contain unique TabHostIds, no duplicates are allowed
 */
@LowLevelApi
fun <L : Any, T : Any> TabHost<L, T>._ensureUniqueTabHosts(tabHostIds: MutableSet<T> = mutableSetOf()): TabHost<L, T> {

    if (tabHostIds.contains(tabHostId)) {
        throw RuntimeException(
            "A navigation graph cannot have duplicate tabHostIds, found at least 2 instances of $tabHostId"
        )
    } else {
        for (backStack in tabs) {
            backStack._parent = this
            backStack._ensureUniqueTabHosts(tabHostIds.also { it.add(tabHostId) })
        }
    }
    return this
}

/**
 * Any navigation graph can only contain unique TabHostIds, no duplicates are allowed
 */
@LowLevelApi
fun <L : Any, T : Any> BackStack<L, T>._ensureUniqueTabHosts(tabHostIds: MutableSet<T> = mutableSetOf()): BackStack<L, T> {
    for (navigation in stack) {
        when (val notBackStack = navigation._notBackStack()) {
            is NotBackStack.IsEndNode -> { /* do nothing */
            }

            is NotBackStack.IsTabHost -> {
                notBackStack.value._ensureUniqueTabHosts(tabHostIds)
            }
        }
    }
    return this
}


@LowLevelApi
fun <L : Any, T : Any> _updateParent(item: Navigation<L, T>, newParent: Navigation<L, T>): Navigation<L, T> {
    when (item) {
        is BackStack -> item._parent = newParent
        is EndNode -> item._parent = newParent
        is TabHost -> item._parent = newParent
    }
    return item
}

@LowLevelApi
fun <L : Any, T : Any> Navigation<L, T>._requireParent(): Navigation<L, T> {
    return parent ?: throw RuntimeException(
        "It should be impossible reach here, but if we do it's a bug [3]. If you are NOT directly" +
                "using @LowLevelApi please file an issue, including the state of the navigation " +
                "graph just before the crash: 'navigationModel.toString(diagnostics = true)' and " +
                "the operation performed. If on the other hand you ARE using @LowLevelApi to perform " +
                "some custom navigation mutations, there is probably an incorrect assumption being " +
                "made about the structure of the navigation graph, please check carefully the source " +
                "code comments for the @LowLevelApi functions"
    )
}

@LowLevelApi
fun <L : Any, T : Any> BackStack<L, T>._addLocation(location: L): BackStack<L, T> {
    return copy(
        stack = stack.toMutableList().also { it.add(endNodeOf(location)) }
    )._populateChildParents()
}

@LowLevelApi
fun <L : Any, T : Any> TabHost<L, T>._addLocationToCurrentTab(location: L): TabHost<L, T> {
    return copy(
        tabs = tabs.mapIndexed { index, backStack ->
            if (index == tabHistory.last()) {
                backStack._addLocation(location)
            } else {
                backStack
            }
        }
    )._populateChildParents()
}

internal fun <L : Any, T : Any> Navigation<L, T>.render(
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

private fun <L : Any, T : Any> EndNode<L, T>.render(
    padding: Int,
    builder: StringBuilder,
    incDiagnostics: Boolean,
    current: Boolean
): StringBuilder {
    with(builder) {
        repeat(padding) { append(" ") }
        append("endNodeOf(${location::class.simpleName})")
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

private fun <L : Any, T : Any> TabHost<L, T>.render(
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
        append("tabHistory = listOf(")
        for (index in tabHistory) {
            append("$index,")
        }
        setLength(length - 1)
        append("),")
        append("\n")
        repeat(pad) { append(" ") }
        append("tabHostId = $tabHostId,\n")
        if (incDiagnostics) {
            repeat(pad) { append(" ") }
            append("backMode = $tabBackModeDefault,\n")
            repeat(pad) { append(" ") }
            append("clearToTabRoot = $clearToTabRootDefault,\n")
        }
        tabs.forEachIndexed { index, tab ->
            if (index == tabHistory.last()) {
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

private fun <L : Any, T : Any> BackStack<L, T>.render(
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
@LowLevelApi
fun <L : Any, T : Any> Navigation<L, T>._createItemNavigatedBackCopy(): Navigation<L, T> {
    return when (this) {
        is BackStack -> _createItemNavigatedBackCopy()
        is EndNode -> _createItemNavigatedBackCopy()
        is TabHost -> _createItemNavigatedBackCopy()
    }._populateChildParents()
}


private fun <L : Any, T : Any> EndNode<L, T>._createItemNavigatedBackCopy(): Navigation<L, T> {
    return this
}

private fun <L : Any, T : Any> TabHost<L, T>._createItemNavigatedBackCopy(): Navigation<L, T> {
    return if (specificItemCanNavigateBack()) {
        copy(tabHistory = tabHistory.toMutableList().also { it.removeLast() })
    } else this
}

/**
 * the returned navigation graph will need to have its parents updated
 * once the complete nav graph has been recreated by calling populateParents()
 * on the top level navigation item
 */
private fun <L : Any, T : Any> BackStack<L, T>._createItemNavigatedBackCopy(): Navigation<L, T> {
    return if (specificItemCanNavigateBack()) {
        copy(stack = stack.toMutableList().also { it.removeLast() })
    } else this
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
@LowLevelApi
fun <L : Any, T : Any> Navigation<L, T>._applyOneStepBackNavigation(): Navigation<L, T>? {
    Fore.d("calculateBackStep() type:${this::class.simpleName} navigation:${this}")
    return if (specificItemCanNavigateBack()) {
        Fore.d("calculateBackStep()... item CAN navigate back")
        _mutateNavigation(
            oldItem = this,
            newItem = this._createItemNavigatedBackCopy()
        )
    } else { // try to move up the chain
        Fore.d("calculateBackStep()... item CANNOT navigate back, (need to move up chain to parent) directParent:${parent}")
        parent?._applyOneStepBackNavigation()
    }
}

/**
 * WARNING: populateParents() MUST be called on the navigation graph AFTER calling
 * this function as changing parent references is part of the reversal algorithm
 *
 * NOTE: populateParents() MUST be called on the navigation graph BEFORE calling this
 * function initially (not required for subsequent recursive calls)
 *
 * @location location to be searched for
 *
 * @nav search will be conducted from the currentLocation of this nav graph, and up via
 * parent relationships, and in the same manner as would a user continually
 * navigating back from the current item until they exit the app
 *
 * @returns a mutated navigation graph containing the searched for location in current position
 * or null if the location is not found
 */
@LowLevelApi
fun <L : Any, T : Any> Navigation<L, T>._reverseToLocation(locationToFind: L): Navigation<L, T>? {
    Fore.d("reverseToLocation() locationToFind:${locationToFind::class.simpleName} nav:$this")
    return if (currentLocation()::class.simpleName == locationToFind::class.simpleName) {
        Fore.d("reverseToLocation()... << MATCHED ${currentLocation()::class.simpleName} >>")
        this
    } else {
        currentItem()._applyOneStepBackNavigation()?._reverseToLocation(locationToFind)
    }
}

/**
 * Finds a TabHost if it exists in the navigation graph, even if it is NOT on the back path
 * of the user. No mutation happens here, a reference to the item is just returned if found, or
 * null if not found
 *
 * NOTE: unlike most functions, this starts at the Home/Exit location and works
 * downwards through the navigation graph, so this must be initially called on the Home Navigation
 * item (which will always be a BackStack or a TabHost). If you don't call this function from the
 * home location, it will throw an exception
 *
 * If a BackStack is encountered during a search, each item of that backStack will be checked for
 * further processing starting at stack[0]
 *
 * If a TabHost is encountered during a search (assuming there is no TabHostId match) each tab of
 * that TabHost is checked in turn for further processing, starting with tab 0
 *
 * If there is a TabHost match, that TabHost is returned
 *
 * If the search completes with no TabHostId match, null is returned
 *
 * @returns a reference to the TabHost as it sits within the navigation graph, or null if the
 * TabHostId is not present anywhere in the navigation graph
 */
@LowLevelApi
fun <L : Any, T : Any> Navigation<L, T>._tabHostFinder(
    tabHostIdToFind: T,
    skipParentCheck: Boolean = false
): TabHost<L, T>? {

    Fore.d("tabHostFinder() tabHostIdToFind:${tabHostIdToFind::class.simpleName} nav:$this")

    if (!skipParentCheck) {
        require(parent == null) {
            "This function is intended to be called on the home / top level navigation item, for" +
                    "which the parent will be null. Try: nav.topParent().tabHostFinder()"
        }
    }

    return when (val tabOrBackStack = _notEndNode()) {
        is RestrictedNavigation.NotEndNode.IsBackStack -> {
            tabOrBackStack.value.stack.firstNotNullOfOrNull {
                if (it is EndNode) {
                    Fore.d("tabHostFinder() skip $this")
                    null
                } else {
                    it._tabHostFinder(tabHostIdToFind, true)
                }
            }
        }

        is RestrictedNavigation.NotEndNode.IsTabHost -> {
            if (tabOrBackStack.value.tabHostId == tabHostIdToFind) {
                tabOrBackStack.value
            } else {
                tabOrBackStack.value.tabs.firstNotNullOfOrNull {
                    it._tabHostFinder(tabHostIdToFind, true)
                }
            }
        }
    }
}
