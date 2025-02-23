@file:OptIn(LowLevelApi::class)

package co.early.n8

import co.early.n8.RestrictedNavigation.NotBackStack.IsEndNode
import co.early.n8.RestrictedNavigation.NotBackStack.IsTabHost
import co.early.n8.lowlevel.LowLevelApi
import co.early.n8.lowlevel.render
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable
data class NavigationState<L : Any, T : Any>(
    @Serializable
    val navigation: Navigation<L, T>,  // TODO should we verify and enforce that each tabHost is unique? if not things will not go well
    /**
     * willBeAddedToHistory i.e. current Location Will Be Added To History on next forward navigation
     */
    @Serializable
    val willBeAddedToHistory: Boolean = true,
    @Transient
    val loading: Boolean = false,
) {

    init {
        require(navigation.parent == null) {
            "top level navigation parent should be null"
        }
    }

    val currentLocation: L by lazy {
        navigation.currentLocation()
    }
    val canNavigateBack: Boolean by lazy {
        navigation.aDescendantCanNavigateBack() || navigation.specificItemCanNavigateBack()
    }
    val backsToExit: Int by lazy {
        navigation.backsToExit
    }
    val hostedBy: List<TabHostLocation<T>> by lazy {
        navigation.currentItem().hostedBy()
    }
}

@Serializable(with = NavigationSerializer::class)
sealed class Navigation<L : Any, T : Any> {

    abstract fun currentLocation(): L

    abstract val backsToExit: Int
    abstract fun toString(
        diagnostics: Boolean = false
    ): String

    /**
     * Indicates which TabHosts this location is hosted in, or an empty list if there is no TabHost
     * parent in any of the navigation graph as the user would navigate back. If the location is
     * hosted in multiple nested TabHosts, the tabHost closest to the exit as the user navigates
     * back is listed first, inner tabHosts are listed after
     * NB: this typically is called on the currentItem: navigation.currentItem().hostedBy()
     */
    abstract fun hostedBy(): List<TabHostLocation<T>>

    /**
     * All the items contained in the stack of a BackStack have that BackStack as their parent.
     *
     * All the Tabs (BackStacks) of a TabHost have that TabHost as their parent.
     *
     * The Only navigation item with a null parent is the top level item
     *
     * Note, this is a _structural_ parent, the parent is NOT the item the user will navigate to
     * when pressing back.
     */
     abstract val parent: Navigation<L, T>?

    /**
     * The child of a BackStack is the last item in the stack
     *
     * The child of a TabHost is the BackStack indexed by the last item in the tabHistory
     *
     * The child of an EndNode is always null
     */
    abstract val child: Navigation<L, T>?

    /**
     * currentItem is the item at the bottom of the navigation graph, and contains the current location
     * of the user
     */
    abstract fun currentItem(): EndNode<L, T>

    /**
     * This obviously depends on all the parents having already been set correctly
     */
    fun topItem(): Navigation<L, T> {
        return parent?.topItem() ?: this
    }

    /**
     * Note, this means will this individual navigation item navigate back if asked to. i.e.
     * irrespective of its parent's ability to navigate back, and in deference to its children's
     * ability to move back
     *
     * 1. "Irrespective of parent" means: if this specific item CAN NOT to move back it will
     * indicate FALSE. Even if its parent may still be able to. (For example, if this item is
     * an EndNode, it will reply false here as no EndNodes can themselves move back. But this
     * EndNode might also be the last item inside a backstack of size 3, meaning that the parent
     * backstack could itself navigate back (and that parent's itemCanNavigateBack() would return
     * true). Irrespective of that possibility, this item will still return false here)
     *
     * 2. "In deference to its children" means: Even if this item CAN to move back in theory, it's
     * child items may also have the ability to move back (and in practice, that would happen first)
     * so in that case, this item would return FALSE. For example, if this item is a BackStack with
     * a stack size of 3, it will STILL reply false if its current item, at index 2, is a TabHost
     * which has room to move back due to the fact that it has a tabHistory of size 2 say
     */
     abstract fun specificItemCanNavigateBack(): Boolean

    /**
     * True if any children between this item and the currentLocation return true for
     * specificItemCanNavigateBack()
     */
    abstract fun aDescendantCanNavigateBack(): Boolean

    // Note @ExposedCopyVisibility is deliberate, we want clients to use endNodeOf() or copy()
    // to create these, because it's easier and cleaner. We could instead leave the constructor
    // public, but that would just provide another less pretty way to do the same thing
    @Serializable @ExposedCopyVisibility
    data class EndNode<L : Any, T : Any> internal constructor (
        @Serializable
        val location: L
    ) : Navigation<L, T>() {


        @Transient @LowLevelApi
        var _parent: Navigation<L, T>? = null

        override val parent: Navigation<L, T>?
            get() = _parent


        override val child: Navigation<L, T>?
            get() = null

        override val backsToExit: Int
            get() = 0

        override fun hostedBy(): List<TabHostLocation<T>> {
            return parent?.hostedBy() ?: emptyList()
        }

        override fun toString(): String {
            return "${this::class.simpleName}[${location::class.simpleName}]"
        }

        override fun toString(diagnostics: Boolean): String {
            return "\n" + render(
                builder = StringBuilder(),
                incDiagnostics = diagnostics,
                current = true
            ).toString()
        }

        override fun currentLocation(): L {
            return location
        }

        override fun currentItem(): EndNode<L, T> {
            return this
        }

        override fun specificItemCanNavigateBack(): Boolean {
            return false
        }

        override fun aDescendantCanNavigateBack(): Boolean {
            return false
        }
    }

    // Note @ExposedCopyVisibility is deliberate, we want clients to use tabsOf() or copy()
    // to create these, because it's easier and cleaner. We could instead leave the constructor
    // public, but that would just provide another less pretty way to do the same thing
    @Serializable @ExposedCopyVisibility
    data class TabHost<L : Any, T : Any> internal constructor (
        @Serializable
        val tabHistory: List<Int>,
        @Serializable
        val tabHostId: T,
        @Serializable
        val tabs: List<BackStack<L, T>>,
        @Serializable
        val clearToTabRootDefault: Boolean = false,
        @Serializable
        val tabBackModeDefault: TabBackMode = TabBackMode.Temporal
    ) : Navigation<L, T>() {

        init {
            require(tabs.isNotEmpty()) { "require at least 1 tab" }
            require(tabHistory.size >= 0) { "there must be at least one index in selectedTabs" }
            require(
                tabHistory.firstOrNull { tabIndex ->
                    tabIndex > tabs.size - 1 || tabIndex < 0
                } == null
            ) { "one or more selectedTab indexes are out of range for ${tabs.size} tabs" }
        }

        @Transient @LowLevelApi
        var _parent: Navigation<L, T>? = null

        override val parent: Navigation<L, T>?
            get() = _parent

        override val child: Navigation<L, T>
            get() = tabs[tabHistory.last()]

        override val backsToExit: Int
            get() = tabHistory.sumOf { tabs[it].backsToExit }

        override fun hostedBy(): List<TabHostLocation<T>> {
            return parent?.hostedBy() ?: emptyList()
        }

        override fun toString(): String {
            return "${this::class.simpleName}(${tabHostId} tabs:${tabs.size} hist:${tabHistory})"
        }

        override fun toString(diagnostics: Boolean): String {
            return "\n" + render(
                builder = StringBuilder(),
                incDiagnostics = diagnostics,
                current = true
            ).toString()
        }

        override fun currentLocation(): L {
            return currentItem().currentLocation()
        }

        override fun currentItem(): EndNode<L, T> {
            return child.currentItem()
        }

        override fun specificItemCanNavigateBack(): Boolean {
            return aDescendantCanNavigateBack().not() && (tabHistory.size > 1)
        }

        override fun aDescendantCanNavigateBack(): Boolean {
            return child.let {
                it.specificItemCanNavigateBack() || it.aDescendantCanNavigateBack()
            }
        }
    }

    // Note @ExposedCopyVisibility is deliberate, we want clients to use backStackOf() or copy()
    // to create these, because it's easier and cleaner. We could instead leave the constructor
    // public, but that would just provide another less pretty way to do the same thing
    @Serializable @ExposedCopyVisibility
    data class BackStack<L : Any, T : Any> internal constructor(
        @Serializable
        val stack: List<Navigation<L, T>>, // TODO should we define this as NotBackStack?
    ) : Navigation<L, T>() {

        init {
            require(stack.isNotEmpty()) { "stack cannot be empty" }
            require(
                stack.firstOrNull { stackItem ->
                    stackItem is BackStack
                } == null
            ) { "BackStacks can NOT have direct children which are also BackStacks" }
        }

        @Transient @LowLevelApi
        var _parent: Navigation<L, T>? = null

        override val parent: Navigation<L, T>?
            get() = _parent

        override val child: Navigation<L, T>
            get() = stack.last()

        override val backsToExit: Int
            get(): Int {
                return stack.sumOf {
                    when (val notBackStack = it.notBackStack()) {
                        is IsEndNode -> 1
                        is IsTabHost -> notBackStack.value.backsToExit
                    }
                }
            }

        override fun hostedBy(): List<TabHostLocation<T>> {
            return when (val tabHost = parent) {
                is TabHost<L, T> -> {
                    tabHost.hostedBy().toMutableList().also {
                        it.add(
                            TabHostLocation(tabHost.tabHostId, tabHost.tabHistory.last())
                        )
                    }
                }

                else -> emptyList() // no parent i.e. we are at the top level BackStack
            }
        }

        override fun toString(): String {
            return "${this::class.simpleName}(${stack.size})"
        }

        override fun toString(diagnostics: Boolean): String {
            return "\n" + render(
                builder = StringBuilder(),
                incDiagnostics = diagnostics,
                current = true
            ).toString()
        }

        override fun currentLocation(): L {
            return currentItem().currentLocation()
        }

        override fun currentItem(): EndNode<L, T> {
            return child.currentItem()
        }

        override fun specificItemCanNavigateBack(): Boolean {
            return aDescendantCanNavigateBack().not() && (stack.size > 1)
        }

        override fun aDescendantCanNavigateBack(): Boolean {
            return child.let {
                it.specificItemCanNavigateBack() || it.aDescendantCanNavigateBack()
            }
        }
    }
}

/**
 * if BreakToTabHost itself is null, continue in current TabHost if any
 *
 * if the object returned from the BreakToTabHost function is null, break out to the top level
 * Navigation item (which may be a BackStack or a TabHost)
 *
 * if the object returned from the BreakToTabHost function is a TabHost which exists somewhere in the
 * navigation graph, jump to that TabHost to continue
 *
 * if the object returned from the BreakToTabHost function is a TabHost which does not exist in the
 * current navigation graph, log a warning and create the TabHost at the current location
 */
typealias BreakToTabHost<L, T> = (() -> TabHostSpecification<L, T>?)?

data class TabHostLocation<T>(
    val tabHostId: T,
    val tabIndex: Int
)

data class TabHostSpecification<L, T>(
    val tabHostId: T,
    val homeTabLocations: List<L>,
    val clearToTabRoot: Boolean = false,
    val backMode: TabBackMode = TabBackMode.Temporal,
    val initialTab: Int = 0,
) {
    init {
        require(homeTabLocations.isNotEmpty()) {
            "homeTabLocations must contain at least one tab"
        }
        require(initialTab < homeTabLocations.size) {
            "initialTab must be less than homeTabLocations.size"
        }
        require(initialTab > -1) {
            "initialTab must be 0 or greater"
        }
    }
}

@Serializable
sealed class TabBackMode {
    @Serializable
    data object Structural : TabBackMode()

    @Serializable
    data object Temporal : TabBackMode()

}
