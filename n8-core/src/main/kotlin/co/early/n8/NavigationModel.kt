package co.early.n8

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.coroutine.launchIO
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.observer.ObservableImp
import co.early.persista.PerSista
import java.io.File

/**
 * This is implemented as an observable model that exposes its state. That state is persisted across
 * rotation or process death with PerSista (PerSista stores data classes as json on the file system)
 *
 * This state is the source of truth that defines the navigation backstack for an app, any time it
 * changes (when there is a navigation to a new location) all observers of the model are notified
 * that the state has changed. The top of the back stack at any point in time (the last element
 * in the list) is the current location.
 *
 * Public functions provide for pushing locations on to the backstack (navigating forward) and
 * popping locations off the backstack (navigating backwards) as the user navigates around the app,
 * the backstack can also be completely rewritten or reordered at will to provide for custom
 * navigation schemes or the handling of deep links.
 *
 * This is pure kotlin logic, there is no dependency on Android or even Compose, and there is
 * minimal coupling between an app screen classes and the n8 library because the Location class is
 * entirely defined by the client, it just needs to be serializable to play nicely with persistence
 * (a sealed class would be a good candidate - see the example).
 *
 * It's not necessary to define the navigation graph beforehand, the backstack just keeps track of
 * whatever navigation operations are performed via the public functions, these functions allow you
 * to pass arbitrary data forwards or backwards, reuse previous locations, optionally not save a
 * location to the backstack at all, or arbitrarily rewrite the backstack as you wish. Nested
 * backstacks are also supported for youtube style tab implementations
 *
 * Example usage below, but see the UnitTests for the definitive guide to the behaviour of the
 * public functions
 *
 *
 * Example Locations:
 *
 * sealed class Location {
 *
 *     data object NewYork : Location()
 *
 *     data object Tokyo : Location()
 *
 *     data class Sydney(val withSunCreamFactor: Int? = null) : Location()
 *
 *     data object SunCreamSelector: Location()
 *
 *     sealed class EuropeanLocations : Location() {
 *
 *         data object London : EuropeanLocations()
 *
 *         data object Paris : EuropeanLocations()
 *     }
 * }
 *
 * instantiate:
 * NavigationModel(homeLocation = London)
 *
 *
 * A. Regular forward navigation
 *
 * navigateTo(Paris)
 * navigateTo(NewYork)
 *
 * backstack: London > Paris > NewYork
 * currentLocation: NewYork
 *
 *
 * B. Regular back navigation
 *
 * navigateTo(Paris)
 * navigateTo(NewYork)
 * popBackStack()
 *
 * backstack: London > Paris
 * currentLocation: Paris
 *
 *
 * C. Back navigation multiple steps
 *
 * navigateTo(Paris)
 * navigateTo(NewYork)
 * popBackStack(times = 2)
 *
 * backstack: London
 * currentLocation: London
 *
 *
 * D. Visiting locations more than once
 *
 * navigateTo(Paris)
 * navigateTo(NewYork)
 * navigateTo(Paris)
 * navigateTo(Tokyo)
 *
 * backstack: London > Paris > NewYork > Paris > Tokyo
 * currentLocation: Tokyo
 *
 *
 * E. Recycling previous locations
 *
 * navigateTo(Paris) (OR just use navigateBackTo(Paris) each time, see example F.)
 * navigateTo(NewYork)
 * navigateBackTo(Paris)
 * navigateTo(Tokyo)
 *
 * backstack: London > Paris > Tokyo
 * currentLocation: Tokyo
 *
 *
 * F. Recycling previous locations when they were never visited in the first place
 *
 * navigateTo(Sydney)
 * navigateTo(NewYork)
 * navigateBackTo(Paris)
 * navigateTo(Tokyo)
 *
 * backstack: London > Sydney > NewYork > Paris > Tokyo
 * currentLocation: Tokyo
 *
 *
 * G. Visiting a location that you don't want added to the back stack
 *
 * navigateTo(Sydney)
 * navigateTo(NewYork)
 * navigateTo(Paris, addToHistory = false)
 * navigateTo(Tokyo)
 *
 * backstack: London > Sydney > NewYork > Tokyo
 * currentLocation: Tokyo
 *
 *
 * H. Passing data forward to a new Location
 *
 * navigateTo(Paris)
 * navigateTo(NewYork)
 * navigateTo(Sydney(withSunCreamFactor = 50))
 *
 * backstack: London > Paris > NewYork > Sydney(50)
 * currentLocation: Sydney(50)
 *
 *
 * I. Returning data from the current Location to a Location further back in the stack
 *
 * navigateTo(Paris)
 * navigateTo(NewYork)
 * navigateTo(Sydney)
 * navigateTo(SunCreamSelector)
 * popBackStack { it -> // the new current location after popping backstack i.e. Sydney
 *    when(it){
 *      Sydney -> it.copy(withSunCreamFactor = 30)
 *      else -> it
 *    }
 * }
 *
 * backstack: London > Paris > NewYork > Sydney(30)
 * currentLocation: Sydney(30)
 *
 *
 * J. Arbitrarily rewriting the entire backstack
 *
 * updateBackStack(
 *    listOf(
 *      Paris,
 *      London,
 *      Sydney(50),
 *      Tokyo,
 *      London
 *    )
 * )
 *
 * backstack: Paris > London > Sydney(50) > Tokyo > London
 * currentLocation: London
 *
 *
 * Copyright © 2015-2023 early.co. All rights reserved.
 */
class NavigationModel<T: Any>(
    homeLocation: T,
    addHomeLocationToHistory: Boolean = true,
    dataDirectory: File,
    private val logger: Logger = Fore.getLogger(),
    private val perSista: PerSista = PerSista(
        dataDirectory = dataDirectory,
        logger = logger,
    )
) : Observable by ObservableImp() {

    var state = NavigationState(
        backStack = listOf(homeLocation),
        currentLocationWillBeAddedToHistoryOnNextNavigation = addHomeLocationToHistory,
    )
        private set

    fun load() {

        logger.i("load()")

        if (state.loading) {
            return
        }

        state = state.copy(loading = true)
        notifyObservers()

        launchIO {
            perSista.read(state) {
                state = it.copy(
                    loading = false,
                )
                notifyObservers()
            }
        }
    }

    fun navigateTo(location: T, addToHistory: Boolean = true) {
        logger.i("navigateTo() ${location.javaClass.simpleName} addToHistory:$addToHistory")
        val stack = state.backStack.toMutableList()
        if (!state.currentLocationWillBeAddedToHistoryOnNextNavigation) {
            stack.removeLast()
        }
        stack.add(location)
        updateState(
            state.copy(
                backStack = stack,
                currentLocationWillBeAddedToHistoryOnNextNavigation = addToHistory
            )
        )
    }

    fun navigateBackTo(location: T, addToHistory: Boolean = true) {
        logger.i("navigateBackTo() ${location.javaClass.simpleName} addToHistory:$addToHistory")
        val indexInBackStack = state.backStack.indexOfLast {
            it.javaClass.canonicalName == location.javaClass.canonicalName
        }

        val newBackStack = if (indexInBackStack >= 0) {
            state.backStack.subList(0, indexInBackStack)
        } else {
            logger.i(" >>> ${location.javaClass.simpleName} not found in back stack, adding new location: ${location.javaClass.simpleName}")
            state.backStack
        }.toMutableList()

        newBackStack.add(location)
        updateState(
            state.copy(
                backStack = newBackStack,
                currentLocationWillBeAddedToHistoryOnNextNavigation = addToHistory
            )
        )
    }

    /**
     * @setData - use this to pass data to locations further back in the backstack. Once the
     * backStack has been popped the required number of times, setData{} will be run with the
     * new current location passed in as a parameter. This gives the caller an opportunity to
     * set data on the new location before it is set at the new top of the backstack
     *
     * returns false if we cannot go back any further (i.e. we are already at the home location)
     */
    fun popBackStack(times: Int = 1, setData: (T) -> T = { it }): Boolean {
        logger.i("popBackStack() times:$times")
        if (state.backStack.size == 1) {
            return false
        }

        val stack = state.backStack.toMutableList()
        if (state.backStack.size > times) {
            for (count in 1..times) {
                stack.removeLast()
            }
        } else {
            stack.clear()
            stack.add(state.backStack.first())
        }
        val locationWithData = setData(stack.last())
        stack.removeLast()
        stack.add(locationWithData)
        updateState(
            state.copy(
                backStack = stack,
                currentLocationWillBeAddedToHistoryOnNextNavigation = true
            )
        )
        return true
    }

    /**
     * @newBackStack - [0] represents the home location, [size-1] is the current location
     */
    fun updateBackStack(newBackStack: List<T>, currentLocationAddToHistory: Boolean = true) {
        logger.i("updateBackStack()")
        require(newBackStack.isNotEmpty()) {
            logger.e("newBackStack is empty")
            "newBack stack cannot be empty, it needs to contain at least one location"
        }
        updateState(
            state.copy(
                backStack = newBackStack,
                currentLocationWillBeAddedToHistoryOnNextNavigation = currentLocationAddToHistory
            )
        )
    }

    override fun toString(): String = toString(" > ")

    fun toString(breadCrumbIndicator: String): String {
        return buildString {
            state.backStack.forEach {
                append(it.toString())
                append(breadCrumbIndicator)
            }
            deleteRange(length - breadCrumbIndicator.length, length - 1)
        }
    }

    private fun updateState(newState: NavigationState<T>) {
        state = newState
        perSista.write(state) {
            notifyObservers()
        }
    }
}
