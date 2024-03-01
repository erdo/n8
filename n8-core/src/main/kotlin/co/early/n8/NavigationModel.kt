package co.early.n8

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.coroutine.launchIO
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.observer.ObservableImp
import co.early.n8.Navigation.BackStack
import co.early.n8.Navigation.EndNode
import co.early.n8.Navigation.TabHost
import co.early.persista.PerSista
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.reflect.KType

//TODO serialise to json for import amd export. maybe different class

/**
 * # Navigation
 *
 * This model is the source of truth for the navigation state of an app. The navigation state
 * principally describes the locations that were visited by a user to reach their current location.
 *
 * Public functions provide for navigating forward and backwards as the user navigates
 * around the app. In the most basic case this involves pushing new locations on to a backstack
 * when navigating forwards, and popping locations off a backstack when navigating backwards.
 *
 * The navigation state also handles an arbitrary number of nested navigation schemes such as you
 * might use for Tab style UI layouts (with each tab represented as its own backstack). Here public
 * functions enable the user to switch to previously visited tabs (with their own back stacks),
 * or backing out of the tabs entirely.
 *
 * This navigation scheme is built up using 3 Node types:
 *
 * - EndNodes (representing a single location)
 * - BackStacks (a mixed list of EndNodes and TabHosts)
 * - TabHosts (a list of BackStacks)
 *
 * The top level Node is always a BackStack
 *
 * The simplest implementation would therefore be a BackStack containing a number of EndNodes with
 * the last EndNode in the BackStack representing the current location, and the first EndNode
 * representing the home location.
 *
 * Aside from the forward / backwards navigation functions, the navigation state can also be
 * completely rewritten or reordered at will to provide for custom navigation schemes or the
 * handling of deep links etc..
 *
 * # Observable State
 *
 * The state exposed by this model is persisted across rotation or process death with PerSista
 * (PerSista stores data classes as json on the file system). This navigation model is also
 * observable, meaning that any time its navigation state changes (when there is a
 * navigation to a new location) all observers of this model are notified that the state has
 * changed. The observing code (which will typically be UI code) can interrogate the
 * currentLocation (to redraw its UI for example)
 *
 * # Pure Kotlin
 *
 * This is pure kotlin logic, there is no dependency on Android or even Compose, and there is
 * minimal coupling between an app's screen classes and the n8 library because the "Location" class
 * is entirely defined by client code, it just needs to be serializable to play nicely with
 * persistence (a sealed class would be a good candidate - see the example).
 *
 * # Usage
 *
 * It's not necessary to define the navigation graph beforehand, the backstack just keeps track of
 * whatever navigation operations are performed via the public functions, these functions allow you
 * to pass arbitrary data forwards or backwards, reuse previous locations, optionally not save a
 * location to the backstack at all, or arbitrarily rewrite the backstack as you wish. Nested
 * back stacks are supported for youtube style tab implementations
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
 * navigateTo(London)
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
 * Copyright © 2015-2024 early.co. All rights reserved.
 */
class NavigationModel<T>(
    homeLocation: T?,
    initialNavigation: BackStack<T>? = null,
    private val locationKType: KType,
    addHomeLocationToHistory: Boolean = true,
    dataDirectory: File,
    private val logger: Logger = Fore.getLogger(),
    private val perSista: PerSista = PerSista(
        dataDirectory = dataDirectory,
        logger = logger,
    ),
) : Observable by ObservableImp() {

    var state = NavigationState(
        navigation = homeLocation?.let { backStackOf(endNodeOf(it)) } ?: initialNavigation!!,
        willBeAddedToHistory = addHomeLocationToHistory,
    )
        private set

    init {
        require(homeLocation == null || initialNavigation == null) {
            "Either homeLocation OR initialNavigation must be null"
        }
        require(homeLocation != null || initialNavigation != null) {
            "Either homeLocation OR initialNavigation must be specified"
        }
//        require(locationKType){ //TODO how to check this and fail early
//            "locationKType = typeOf<NavigationState<Location>>(),"
//        }
        load()
    }

    //TODO allow for clearing the navigation memory via this model, and maybe a flag via constructor too

    fun load() {

        logger.i("load()")

        if (state.loading) {
            return
        }

        state = state.copy(loading = true)
        notifyObservers()

        launchIO {
            perSista.read(state, locationKType) {
                state = it.copy(
                    loading = false,
                    navigation = it.navigation.populateParents()
                )
                notifyObservers()
            }
        }
    }

    fun navigateTo(location: T, addToHistory: Boolean = true) {
        logger.d("navigateTo() ${location!!::class.simpleName} addToHistory:$addToHistory currentAddToHist:${state.willBeAddedToHistory}")

        val trimmed = if (!state.willBeAddedToHistory) {
            calculateBackStep(state.navigation.currentItem())
        } else state.navigation

        val navigated = trimmed?.currentItem()?.requireParent()?.isBackStack()?.let { parent ->
            val newParent = parent.copy(
                stack = parent.stack.toMutableList().also { it.add(endNodeOf(location)) }
            ).populateParents()

            mutateNavigation(
                oldItem = parent,
                newItem = newParent
            ).isBackStack()
        } ?: backStackOf<T>(endNodeOf(location))

        updateState(
            state.copy(
                navigation = navigated,
                willBeAddedToHistory = addToHistory
            )
        )
    }

    /**
     * @setData - use this to pass data to locations further back in the graph. Once the
     * back operation has been applied the required number of times, setData{} will be run with the
     * new current location passed in as a parameter. This gives the caller an opportunity to
     * set data on the new location before it is set as the new currentLocation of the navigation
     * graph
     *
     * returns false if we were not able to go back the requested number of times (i.e. we reached
     * the home location item first)
     */
    fun navigateBack(times: Int = 1, setData: (T) -> T = { it }): Boolean {
        logger.d("navigateBack() times:$times")

        var backUpSuccessful = true
        var newNavigation = state.navigation.currentItem()
        for (i in 1..times) {
            val backed = calculateBackStep(newNavigation)
            if (backed != null) {
                newNavigation = backed.currentItem()
            } else {
                logger.d("navigateBack()... no more room to back up")
                backUpSuccessful = false
                break
            }
        }

        updateState(
            state.copy(
                navigation = setDataOnCurrentLocation(newNavigation, setData).isBackStack(),
                willBeAddedToHistory = true
            )
        )

        return backUpSuccessful
    }

    fun navigateBackTo(location: T, tabHostId: String? = null, addToHistory: Boolean = true) {
        logger.d("navigateBackTo() ${location!!::class.simpleName} addToHistory:$addToHistory")

        val foundLocationNav = tabHostId?.let {
            reverseToLocation(location, tabsOf()) //TODO
        } ?: reverseToLocation(location, state.navigation)

        if (foundLocationNav != null) { //replace location as it might have different data
            Fore.d("navigateBackTo()... location found in history: ${foundLocationNav.currentLocation()!!::class.simpleName}")
            val newNavigation = mutateNavigation(
                oldItem = foundLocationNav.currentItem(),
                newItem = endNodeOf(location)
            ).isBackStack()
            updateState(
                state.copy(
                    navigation = newNavigation,
                    willBeAddedToHistory = addToHistory,
                )
            )
        } else { // didn't find location so just navigate forward
            Fore.d("navigateBackTo()... location not found in history, navigating forward instead")
            navigateTo(location, addToHistory)
        }
    }

    /**
     * NOTE: populateParents() MUST be called on the navigation graph BEFORE calling this
     * function initially (not required for subsequent recursive calls)
     *
     * @location location to be searched for
     *
     * @nav search will be conducted from the currentLocation of this nav graph, and up via
     * parent relationships, and in the same manner as would a user continually
     * navigating back from the current item until they exit the app
     *
     * @returns a mutated navigation graph containing the location in current position or null if
     * the location is not found
     */
    private fun reverseToLocation(locationToFind: T, nav: Navigation<T>): Navigation<T>? {
        Fore.d("reverseToLocation() locationToFind:${locationToFind!!::class.simpleName} nav:${nav}")
        return if (nav.currentLocation()!!::class.simpleName == locationToFind!!::class.simpleName) {
            Fore.d("reverseToLocation()... MATCHED ${nav.currentLocation()!!::class.simpleName}")
            nav
        } else {
            calculateBackStep(nav.currentItem())?.let {
                reverseToLocation(locationToFind, it)
            }
        }
    }

    /**
     * NOTE: populateParents() MUST be called on the navigation graph BEFORE calling this
     * function initially (not required for subsequent recursive calls)
     *
     * @location location to be searched for
     *
     * @tabHost the search will visit only the tabHost identified, starting by going backwards
     * along the selectedTabHistory as usual, but then including any other tab backStacks not
     * checked in the previous step, in order, from tab [0] to tab [size-1]
     *
     * @returns a mutated navigation graph containing the location in current position or null if
     * the location is not found
     */
    private fun reverseToLocation(location: T, tabHost: TabHost<T>): EndNode<T>? {
        TODO()
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
    private fun calculateBackStep(navigation: Navigation<T>): Navigation<T>? {
        Fore.d("calculateBackStep() type:${navigation::class.simpleName} navigation:${navigation}")
        return if (navigation.specificItemCanNavigateBack()) {
            Fore.d("calculateBackStep()... item CAN navigate back")
            mutateNavigation(
                oldItem = navigation,
                newItem = navigation.createNavigatedBackCopy()
            )
        } else { // try to move up the chain
            Fore.d("calculateBackStep()... item CANNOT navigate back, (need to move up chain to parent) directParent:${navigation.directParent}")
            navigation.directParent?.invoke()?.let {
                calculateBackStep(it)
            }
        }
    }

    private fun setDataOnCurrentLocation(
        currentItem: EndNode<T>,
        setData: (T) -> T = { it },
    ): Navigation<T> {
        return mutateNavigation(
            oldItem = currentItem,
            newItem = EndNode(setData(currentItem.location))
        )
    }

    fun reWriteNavigation(
        navigation: BackStack<T>,
        willBeAddedToHistory: Boolean = true
    ) {
        Fore.d("reWriteNavigation() currentLocation: ${navigation.currentLocation()!!::class.simpleName} willBeAddedToHistory:$willBeAddedToHistory")
        updateState(
            NavigationState(
                navigation = navigation,
                willBeAddedToHistory = willBeAddedToHistory,
            )
        )
    }

    override fun toString(): String {
        return toString(diagnostics = false)
    }

    fun toString(diagnostics: Boolean = true): String {
        return state.navigation.toString(diagnostics)
    }

    private fun updateState(newState: NavigationState<T>) {
        state = newState
        notifyObservers()
        perSista.write(state, locationKType) {}
    }
}
