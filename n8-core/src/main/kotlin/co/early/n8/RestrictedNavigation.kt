package co.early.n8

import co.early.n8.RestrictedNavigation.NotBackStack
import co.early.n8.RestrictedNavigation.NotEndNode

/**
 * a Navigation can be one of 3 types (BackStack, EndNode, TabHost), but we can often further
 * restrict this to two or only one possibility, when we do this we can help the compiler help us
 * during library development, and that's what these classes are for
 */
internal class RestrictedNavigation {
    internal sealed class NotBackStack<L : Any, T : Any> {
        internal data class IsEndNode<L : Any, T : Any>(val value: Navigation.EndNode<L, T>) : NotBackStack<L, T>()
        internal data class IsTabHost<L : Any, T : Any>(val value: Navigation.TabHost<L, T>) : NotBackStack<L, T>()
    }

    internal sealed class NotEndNode<L : Any, T : Any> {
        internal data class IsBackStack<L : Any, T : Any>(val value: Navigation.BackStack<L, T>) : NotEndNode<L, T>()
        internal data class IsTabHost<L : Any, T : Any>(val value: Navigation.TabHost<L, T>) : NotEndNode<L, T>()
    }
}

internal fun <L : Any, T : Any> Navigation<L, T>.isBackStack(): Navigation.BackStack<L, T> {
    return when (this) {
        is Navigation.BackStack<L, T> -> this
        is Navigation.EndNode, is Navigation.TabHost -> throw RuntimeException(errorMsg)
    }
}

internal fun <L : Any, T : Any> Navigation<L, T>.isEndNode(): Navigation.EndNode<L, T> {
    return when (this) {
        is Navigation.EndNode -> this
        is Navigation.BackStack, is Navigation.TabHost -> throw RuntimeException(errorMsg)
    }
}

internal fun <L : Any, T : Any> Navigation<L, T>.isTabHost(): Navigation.TabHost<L, T> {
    return when (this) {
        is Navigation.TabHost<L, T> -> this
        is Navigation.EndNode, is Navigation.BackStack -> throw RuntimeException(errorMsg)
    }
}

internal fun <L : Any, T : Any> Navigation<L, T>.notBackStack(): NotBackStack<L, T> {
    return when (this) {
        is Navigation.BackStack<L, T> -> throw RuntimeException(errorMsg)
        is Navigation.EndNode -> NotBackStack.IsEndNode(this)
        is Navigation.TabHost -> NotBackStack.IsTabHost(this)
    }
}

internal fun <L : Any, T : Any> Navigation<L, T>.notEndNode(): NotEndNode<L, T> {
    return when (this) {
        is Navigation.BackStack -> NotEndNode.IsBackStack(this)
        is Navigation.EndNode -> throw RuntimeException(errorMsg)
        is Navigation.TabHost -> NotEndNode.IsTabHost(this)
    }
}

private const val errorMsg =
    "It should be impossible reach here, but if we do it's a bug [1]. If you are NOT directly" +
            "using @LowLevelApi please file an issue, including the state of the navigation " +
            "graph just before the crash: 'navigationModel.toString(diagnostics = true)' and " +
            "the operation performed. If on the other hand you ARE using @LowLevelApi to perform " +
            "some custom navigation mutations, there is probably an incorrect assumption being " +
            "made about the structure of the navigation graph, please check carefully the source " +
            "code comments for the @LowLevelApi functions"
