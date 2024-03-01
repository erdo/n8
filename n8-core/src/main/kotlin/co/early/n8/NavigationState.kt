package co.early.n8

import co.early.n8.Navigation.BackStack
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable
data class NavigationState<T>(
    @Serializable
    val navigation: BackStack<T>,
    /**
     * willBeAddedToHistory i.e. current Location "willBeAddedToHistory" on next forward navigation
     */
    @Serializable
    val willBeAddedToHistory: Boolean = true,
    @Transient
    val loading: Boolean = false,
) {

    init {
        require(navigation.directParent == null) {
            "top level navigation parent should be null"
        }
    }

    val currentLocation: T by lazy {
        navigation.currentLocation()
    }
    val canNavigateBack: Boolean by lazy {
        navigation.aDescendantCanNavigateBack() || navigation.specificItemCanNavigateBack()
    }
    val backsToExit: Int by lazy {
        navigation.backsToExit
    }
}

@Serializable
data class Parent<T>(private val nav: Navigation<T>) {
    operator fun invoke(): Navigation<T> {  // TODO can we get rid of explicit invoke()
        return nav
    }
}

@Serializable(with = NavigationSerializer::class)
sealed class Navigation<T> {

    abstract fun currentLocation(): T
    abstract val backsToExit: Int
    abstract fun toString(
        diagnostics: Boolean = false
    ): String

    internal abstract fun render(
        padding: Int = 0,
        builder: StringBuilder,
        incDiagnostics: Boolean,
        current: Boolean = false,
    ): StringBuilder

    internal abstract val directParent: Parent<T>?  // TODO do we still need to wrap this?
    internal abstract val directChild: Navigation<T>?

    /**
     * Will always in fact be an EndNode
     *
     * currentItem is the item at the bottom of the navigation graph, and represents the current location
     * of the user
     */
    internal abstract fun currentItem(): EndNode<T>

    /**
     * This obviously depends on all the parents having already been set correctly,
     * the top parent is always of BackStack type
     */
    internal fun topParent(): BackStack<T> {
        return directParent?.invoke()?.topParent() ?: this.isBackStack()
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

    /**
     * a copy of the navigation item which has had the back operation applied to it, for an
     * end node, this will be the navigation item with no changes (a back operation is not
     * valid for this type of navigation item)
     */
    internal abstract fun createNavigatedBackCopy(): Navigation<T>

    @Serializable
    data class EndNode<T> internal constructor(
        @Serializable
        val location: T,
        @Transient
        override var directParent: Parent<T>? = null,
    ) : Navigation<T>() {

        override val directChild: Navigation<T>?
            get() = null

        override val backsToExit: Int
            get() = 0

        override fun toString(): String {
            return this::class.simpleName ?: "null"
        }

        override fun toString(diagnostics: Boolean): String {
            return "\n" + render(
                builder = StringBuilder(),
                incDiagnostics = diagnostics,
                current = diagnostics
            ).toString()
        }

        override fun render(
            padding: Int,
            builder: StringBuilder,
            incDiagnostics: Boolean,
            current: Boolean
        ): StringBuilder {
            with(builder) {
                repeat(padding) { append(" ") }
                append("endNodeOf(${location!!::class.simpleName})")
                if (incDiagnostics) {
                    append(" [p=${directParent?.invoke()} c=${directChild}]")
                    if (current) {
                        append("     <--- Current Item")
                    }
                }
                return this
            }
        }

        override fun currentLocation(): T {
            return location
        }

        override fun currentItem(): EndNode<T> {
            return this
        }

        override fun specificItemCanNavigateBack(): Boolean {
            return false
        }

        override fun aDescendantCanNavigateBack(): Boolean {
            return false
        }

        override fun createNavigatedBackCopy(): Navigation<T> {
            return this
        }
    }

    @Serializable
    data class TabHost<T> internal constructor(
        @Transient
        override var directParent: Parent<T>? = null,
        @Serializable
        val selectedTabHistory: List<Int> = listOf(0),
       // @Serializable(with = TabsFunctionSerializer::class)
        @Serializable
        val tabs: List<out BackStack<T>>,
    ) : Navigation<T>() {
        init {
            require(tabs.size > 1) { "require at least 2 tabs" }
            require(selectedTabHistory.size >= 0) { "there must be at least one index in selectedTabs" }
            require(
                selectedTabHistory.firstOrNull { tabIndex ->
                    tabIndex > tabs.size - 1 || tabIndex < 0
                } == null
            ) { "one or more selectedTab indexes are out of range for ${tabs.size} tabs" }
        }

        override val directChild: Navigation<T>?
            get() = tabs[selectedTabHistory.last()]

        override val backsToExit: Int
            get() = selectedTabHistory.size + (directChild?.backsToExit ?: 0)

        override fun toString(): String {
            return "${this::class.simpleName}(${tabs.size})"
        }

        override fun toString(diagnostics: Boolean): String {
            return "\n" + render(
                builder = StringBuilder(),
                incDiagnostics = diagnostics,
                current = diagnostics
            ).toString()
        }

        override fun render(
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
                    append("[p=${directParent?.invoke()} c=${directChild} tabs=${tabs.size}]")
                }
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
                        tab.render(pad, builder, incDiagnostics)
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

        override fun currentLocation(): T {
            return currentItem().currentLocation()
        }

        override fun currentItem(): EndNode<T> {
            return directChild!!.currentItem()
        }

        override fun specificItemCanNavigateBack(): Boolean {
            return aDescendantCanNavigateBack().not() && (selectedTabHistory.size > 1)
        }

        override fun aDescendantCanNavigateBack(): Boolean {
            return directChild!!.let {
                it.specificItemCanNavigateBack() || it.aDescendantCanNavigateBack()
            }
        }

        override fun createNavigatedBackCopy(): Navigation<T> {
            return if (specificItemCanNavigateBack()) {
                val newHistory = selectedTabHistory.toMutableList()
                newHistory.removeLast()
                this.copy(selectedTabHistory = newHistory) // TODO .populateParents()?
            } else this
        }
    }

    @Serializable
    data class BackStack<T> internal constructor(
        @Transient
        override var directParent: Parent<T>? = null,
        @Serializable
        val stack: List<Navigation<T>>,
    ) : Navigation<T>() {
        init {
            require(stack.isNotEmpty()) { "stack cannot be empty" }
            require(
                stack.firstOrNull { stackItem ->
                    stackItem is BackStack
                } == null
            ) { "BackStacks can NOT have direct children which are also BackStacks" }
        }

        override val directChild: Navigation<T>?
            get() = stack.last()

        override val backsToExit: Int
            get() = stack.size + (directChild?.backsToExit ?: 0)

        override fun toString(): String {
            return "${this::class.simpleName}(${stack.size})"
        }

        override fun toString(diagnostics: Boolean): String {
            return "\n" + render(
                builder = StringBuilder(),
                incDiagnostics = diagnostics,
                current = diagnostics
            ).toString()
        }

        override fun render(
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
                    append("[p=${directParent?.invoke()} c=${directChild} stackSize=${stack.size}]")
                }
                append("\n")
                val lastIndex = stack.lastIndex
                stack.forEachIndexed { index, stackItem ->
                    if (index == lastIndex) {
                        stackItem.render(pad, builder, incDiagnostics, current)
                    } else {
                        stackItem.render(pad, builder, incDiagnostics)
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

        override fun currentLocation(): T {
            return currentItem().currentLocation()
        }

        override fun currentItem(): EndNode<T> {
            return directChild!!.currentItem()
        }

        override fun specificItemCanNavigateBack(): Boolean {
            return aDescendantCanNavigateBack().not() && (stack.size > 1)
        }

        override fun aDescendantCanNavigateBack(): Boolean {
            return directChild!!.let {
                it.specificItemCanNavigateBack() || it.aDescendantCanNavigateBack()
            }
        }

        /**
         * the returned navigation graph will need to have its parents updated
         * once the complete nav graph has been recreated by calling populateParents()
         * on the top level navigation item
         */
        override fun createNavigatedBackCopy(): Navigation<T> {
            return if (specificItemCanNavigateBack()) {
                val newStack = stack.toMutableList()
                newStack.removeLast()
                this.copy(
                    stack = newStack
                ).populateParents()
            } else this
        }
    }
}

internal class RestrictedNavigation {
    internal sealed class NotBackStack<T> {
        internal data class IsEndNode<T>(val value: Navigation.EndNode<T>) : NotBackStack<T>()
        internal data class IsTabHost<T>(val value: Navigation.TabHost<T>) : NotBackStack<T>()
    }

    internal sealed class NotEndNode<T> {
        internal data class IsBackStack<T>(val value: Navigation.BackStack<T>) :
            NotEndNode<T>()

        internal data class IsTabHost<T>(val value: Navigation.TabHost<T>) : NotEndNode<T>()
    }
}

internal fun <T> Navigation<T>.isBackStack(): BackStack<T> {
    return when (this) {
        is BackStack -> this
        is Navigation.EndNode, is Navigation.TabHost -> throw RuntimeException(errorMsg)
    }
}

internal fun <T> Navigation<T>.notBackStack(): RestrictedNavigation.NotBackStack<T> {
    return when (this) {
        is BackStack -> throw RuntimeException(errorMsg)
        is Navigation.EndNode -> RestrictedNavigation.NotBackStack.IsEndNode(this)
        is Navigation.TabHost -> RestrictedNavigation.NotBackStack.IsTabHost(this)
    }
}

internal fun <T> Navigation<T>.isEndNode(): Navigation.EndNode<T> {
    return when (this) {
        is Navigation.EndNode -> this
        is BackStack, is Navigation.TabHost -> throw RuntimeException(errorMsg)
    }
}

internal fun <T> Navigation<T>.notEndNode(): RestrictedNavigation.NotEndNode<T> {
    return when (this) {
        is BackStack -> RestrictedNavigation.NotEndNode.IsBackStack(this)
        is Navigation.EndNode -> throw RuntimeException(errorMsg)
        is Navigation.TabHost -> RestrictedNavigation.NotEndNode.IsTabHost(this)
    }
}

private const val padStep: Int = 4
private const val errorMsg =
    "It should be impossible reach here, but if we do it's a bug. Please file " +
            "an issue, indicating the function called and the output of toString(diagnostics=true)"
