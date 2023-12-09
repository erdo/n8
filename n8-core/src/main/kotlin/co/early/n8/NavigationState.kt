package co.early.n8

import kotlinx.serialization.Serializable

@Serializable
data class NavigationState<T>(
    /**
     * backStack is the full history, plus the current location in the last position
     */
    @Serializable
    val backStack: List<T>,
    @Serializable
    val currentLocationWillBeAddedToHistoryOnNextNavigation: Boolean = true,
    @Transient
    val loading: Boolean = false,
) {

    init {
        require(backStack.isNotEmpty()) { "The backstack needs at least one location item that serves as the homepage" }
    }

    fun currentPage(): T = backStack.last()
    fun canNavigateBack(): Boolean = backStack.size > 1
}
