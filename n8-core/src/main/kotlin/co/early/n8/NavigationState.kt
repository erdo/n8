package co.early.n8

import co.early.fore.kt.core.delegate.Fore
import co.early.n8.RestrictedNavigation.NotBackStack.IsEndNode
import co.early.n8.RestrictedNavigation.NotBackStack.IsTabHost
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable
data class NavigationState<L, T>(
    @Serializable
    val navigation: Navigation<L, T>,
    /**
     * willBeAddedToHistory i.e. current Location "willBeAddedToHistory" on next forward navigation
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
sealed class Navigation<L, T> {

    abstract fun currentLocation(): L
    abstract val backsToExit: Int
    abstract fun toString(
        diagnostics: Boolean = false
    ): String


    // TODO some of these recursive functions work up the tree, some work down, is it worth highlighting this
    // or can we make it consistent?

    /**
     * Indicates which TabHosts this location is hosted in, or an empty list if there is no TabHost
     * parent in any of the navigation graph as the user would navigate back. If the location is
     * hosted in multiple nested TabHosts, the tabHost closest to the exit as the user navigates
     * back is listed first, inner tabHosts are listed after
     */
    internal abstract fun hostedBy(): List<TabHostLocation<T>>
    /**
     * All the items contained in the stack of a BackStack have that BackStack as their parent.
     *
     * All the Tabs (BackStacks) of a TabHost have that TabHost as their parent.
     *
     * Note, this is a _structural_ parent, the parent is NOT the item the user will navigate to
     * when pressing back.
     */
    internal abstract val parent: Navigation<L, T>?
    /**
     * The child of a BackStack is the last item in the stack (often but not always, this is also
     * the currentItem)
     *
     * The child of a TabHost is the last item in the stack (often but not always, this is also
     * the currentItem)
     */
    internal abstract val child: Navigation<L, T>?

    /**
     * currentItem is the item at the bottom of the navigation graph, and represents the current location
     * of the user
     */
    internal abstract fun currentItem(): EndNode<L, T>

    /**
     * This obviously depends on all the parents having already been set correctly
     */
    internal fun topParent(): Navigation<L, T> {
        return parent?.topParent() ?: this
    }

    /**
     * Note, this means will this individual navigation item navigate back if asked to. i.e.
     * irrespective of its parent's ability to navigate back, and in deference to its children's
     * ability to move back
     *
     * 1. "Irrespective of parent" means: if this specific item CAN NOT to move back it will
     * indicate FALSE. Even if its parent may still be able to. (For example, if this item is
     * an EndNode, it will reply false here as no EndNodes can themselves move back. But it might
     * be the last item inside a backstack of size 3, meaning that the parent backstack could
     * itself navigate back (and that parent's itemCanNavigateBack() would return true).
     * Irrespective of that possibility, this item will still return false here)
     *
     * 2. "In deference to its children" means: Even if this item CAN to move back in theory, it's
     * child items may also have the ability to move back (and in practice, that would happen first)
     * so that case, this item would return FALSE. For example, if this item is a BackStack with
     * a stack size of 3, it will STILL reply false if its current item, at index 2, is a TabHost
     * which has room to move back due to the fact that it has a selectedTabHistory of size 2 say
     */
    internal abstract fun specificItemCanNavigateBack(): Boolean

    /**
     * True if any children between this item and the currentLocation return true for
     * specificItemCanNavigateBack()
     */
    internal abstract fun aDescendantCanNavigateBack(): Boolean

    @Serializable
    data class EndNode<L, T> internal constructor(
        @Serializable
        val location: L
    ) : Navigation<L, T>() {

        @Transient
        override var parent: Navigation<L, T>? = null

        override val child: Navigation<L, T>?
            get() = null

        override val backsToExit: Int
            get() = 0

        override fun toString(): String {
            return this::class.simpleName ?: "null"
        }

        override fun hostedBy(): List<TabHostLocation<T>> {

            Fore.e("hostedBy()")

            return parent?.hostedBy() ?: emptyList()
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

    @Serializable
    data class TabHost<L, T> internal constructor(
        @Serializable
        val selectedTabHistory: List<Int>,
        @Serializable
        val tabHostId: T,
        @Serializable
        val tabs: List<BackStack<L, T>>,
    ) : Navigation<L, T>() {

        @Transient
        override var parent: Navigation<L, T>? = null

        init {
            require(tabs.isNotEmpty()) { "require at least 1 tab" }
            require(selectedTabHistory.size >= 0) { "there must be at least one index in selectedTabs" }
            require(
                selectedTabHistory.firstOrNull { tabIndex ->
                    tabIndex > tabs.size - 1 || tabIndex < 0
                } == null
            ) { "one or more selectedTab indexes are out of range for ${tabs.size} tabs" }
        }

        override val child: Navigation<L, T>
            get() = tabs[selectedTabHistory.last()]

        override val backsToExit: Int
            get(): Int {
                var backs = 0
                selectedTabHistory.forEach {
                    backs += tabs[it].backsToExit
                }
                return backs
//TODO why does reduce not work
//                return selectedTabHistory.reduce { backsToExitAccumulator, tabIndex ->
//                    Fore.e("backsToExit for tabIndex $tabIndex: ${tabs[tabIndex].backsToExit}")
//                    backsToExitAccumulator + tabs[tabIndex].backsToExit
//                }
            }

        override fun toString(): String {
            return "${this::class.simpleName}(${tabHostId} tabs:${tabs.size} hist:${selectedTabHistory})"
        }

        override fun hostedBy(): List<TabHostLocation<T>> {
            return parent?.hostedBy() ?: emptyList()
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
            return aDescendantCanNavigateBack().not() && (selectedTabHistory.size > 1)
        }

        override fun aDescendantCanNavigateBack(): Boolean {
            return child.let {
                it.specificItemCanNavigateBack() || it.aDescendantCanNavigateBack()
            }
        }
    }

    @Serializable
    data class BackStack<L, T> internal constructor(
        @Serializable
        val stack: List<Navigation<L, T>>, // TODO should we define this as NotBackStack?
    ) : Navigation<L, T>() {

        @Transient
        override var parent: Navigation<L, T>? = null

        init {
            require(stack.isNotEmpty()) { "stack cannot be empty" }
            require(
                stack.firstOrNull { stackItem ->
                    stackItem is BackStack
                } == null
            ) { "BackStacks can NOT have direct children which are also BackStacks" }
        }

        override val child: Navigation<L, T>
            get() = stack.last()

        override val backsToExit: Int
            get(): Int {
                var backs = 0  // TODO why does reduce not work
                stack.forEach {
                    backs += when (val notBackStack = it.notBackStack()) {
                        is IsEndNode -> 1
                        is IsTabHost -> notBackStack.value.backsToExit
                    }
                }
                return backs
            }

        override fun toString(): String {
            return "${this::class.simpleName}(${stack.size})"
        }

        override fun hostedBy(): List<TabHostLocation<T>> {
            return when (val tabHost = parent) {
                is TabHost<L, T> -> {
                    tabHost.hostedBy().toMutableList().also {
                        it.add(
                            TabHostLocation(tabHost.tabHostId, tabHost.selectedTabHistory.last())
                        )
                    }
                }

                else -> emptyList() // no parent i.e. we are at the top level BackStack
            }
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
 * if BreakTab itself is null, continue in current TabHost if any
 *
 * if the object passed into the BreakTab function is null, break out to the top level Navigation
 * item (which may be a BackStack or a TabHost)
 *
 * if the object passed into the BreakTab function is a known TabHost, break out to that TabHost
 *
 * if the object passed into the BreakTab function is not a known TabHost, log a warning and leave
 * the navigation state as it is // TODO review this behaviour
 */
typealias BreakToTabHost<T> = (() -> T?)?

data class TabHostLocation<T>(
    val tabHostId: T,
    val tabIndex: Int
)

data class TabHostSpecification<L, T>(
    val tabHostId: T,
    val homeTabLocations: List<L>,
    val backMode: TabBackMode = TabBackMode.Temporal,
) {
    init {
        require(homeTabLocations.isNotEmpty()) {
            "homeTabLocations must contain at least one tab"
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
