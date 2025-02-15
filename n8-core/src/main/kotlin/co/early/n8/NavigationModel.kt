package co.early.n8

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.coroutine.launchIO
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.observer.ObservableImp
import co.early.n8.Navigation.EndNode
import co.early.n8.NavigationModel.TabHostTarget.ChangeTabHostTo
import co.early.n8.NavigationModel.TabHostTarget.NoChange
import co.early.n8.NavigationModel.TabHostTarget.TopLevel
import co.early.persista.PerSista
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
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
 *
 * # Generics
 *
 * @param L Class used as Location object, typically a sealed class or an enum
 * @param T Class used to uniquely identify TabHosts, if the app has no TabHost
 * in its navigation, you can specify Unit here. Otherwise you could use a String or Int for a
 * basic implementation, or a sealed class or enum to avoid using magic values.
 *
 * Copyright © 2015-2025 early.co. All rights reserved.
 */
class NavigationModel<L : Any, T : Any>(
    private val stateKType: KType,
    initialNavigation: Navigation<L, T>,
    addHomeLocationToHistory: Boolean = true,
    private val perSista: PerSista,
    private val logger: Logger = Fore.getLogger()
) : Observable by ObservableImp() {

    constructor(
        stateKType: KType,
        homeLocation: L,
        addHomeLocationToHistory: Boolean = true,
        perSista: PerSista,
        logger: Logger = Fore.getLogger(),
    ) : this(
        stateKType = stateKType,
        initialNavigation = backStackOf<L, T>(endNodeOf(homeLocation)),
        addHomeLocationToHistory = addHomeLocationToHistory,
        perSista = perSista,
        logger = logger,
    )

    constructor(
        stateKType: KType,
        homeLocation: L,
        addHomeLocationToHistory: Boolean = true,
        dataDirectory: File,
        logger: Logger = Fore.getLogger(),
    ) : this(
        stateKType = stateKType,
        initialNavigation = backStackOf<L, T>(endNodeOf(homeLocation)),
        addHomeLocationToHistory = addHomeLocationToHistory,
        logger = logger,
        perSista = PerSista(
            dataDirectory = dataDirectory,
            logger = logger,
        ),
    )

    constructor(
        stateKType: KType,
        initialNavigation: Navigation<L, T>,
        addHomeLocationToHistory: Boolean = true,
        dataDirectory: File,
        logger: Logger = Fore.getLogger(),
    ) : this(
        stateKType = stateKType,
        initialNavigation = initialNavigation,
        addHomeLocationToHistory = addHomeLocationToHistory,
        logger = logger,
        perSista = PerSista(
            dataDirectory = dataDirectory,
            logger = logger,
        ),
    )

    var state = NavigationState(
        navigation = initialNavigation,
        willBeAddedToHistory = addHomeLocationToHistory,
    )
        private set

    private var tabHostClassChecked = false

    private sealed class TabHostTarget {
        data object NoChange : TabHostTarget()
        data object TopLevel : TabHostTarget()
        data class ChangeTabHostTo<L: Any, T:Any>(val target: TabHostSpecification<L, T>) : TabHostTarget()
    }

    init {
        try {
            Json.encodeToString(serializer(stateKType), state)
        } catch (e: Exception) {
            require(false) {
                "\nFailed to serialise the specified location type. There are a few potential \n" +
                        "reasons for this, but the quickest solution is probably to compare your \n" +
                        "implementation with the example app that comes with the n8 library. \n\n" +
                        "* check the stateKType that you specified, it needs to look like \n" +
                        "  this: stateKType = typeOf<NavigationState<MyLocationClass, MyTabHostIdClass>>() \n" +
                        "  where MyLocationClass is the class you are using to uniquely identify \n" +
                        "  the locations / pages of your app, and MyTabHostIdClass is the class you \n" +
                        "  are using to uniquely identify your TabHosts (If you have NO TabHosts in your \n" +
                        "  app, you can just put Unit there). Typically those classes will be \n" +
                        "  some kind of enum or sealed data class, but you could also use String or Int. \n\n" +
                        "* you might have forgotten to add the kotlin serialization plugin to gradle, \n" +
                        "  see the sample app gradle build file. \n\n" +
                        "* you could have forgotten to add the @Serializable annotation to mark your \n" +
                        "  Location and TabHost classes serializable \n\n" +
                        "* are you running an obfuscated build? check the sample app for the \n" +
                        "  proguard rules to add (it's the same as when you are using ktor). The \n" +
                        "  actual error we received when trying to serialise the state follows... \n\n" +
                        e.toString()
            }
        }
        load()
    }

    //TODO allow for clearing the navigation memory via this model, and maybe a flag via constructor too

    private fun load() {

        logger.i("load()")

        if (state.loading) {
            return
        }

        state = state.copy(loading = true)
        notifyObservers()

        launchIO {
            perSista.read(state, stateKType) {
                state = it.copy(
                    loading = false,
                    navigation = it.navigation.populateChildParents()
                )
                notifyObservers()
            }
        }
    }

    // TODO add usage comment
    // therefore if tabHostId does not appear in the backStack, it will not be navigated to, also history
    // will be wiped from the point of the TabHost Id forward - alternative is to host the whole thing in an other
    // tabhost because orphan tabs remain as the they are until the who of the TabHost is removed from the back stack
    // TabHostSpecification needs to be specified (in the case that the tabHost doesn't exist, it can be created) but the
    // TabHostId is the only thing which is matched
    fun navigateTo(location: L, addToHistory: Boolean = true, breakTo: BreakToTabHost<L, T> = null) {
        val tabHostTarget: TabHostTarget = breakTo?.let {
            breakTo()?.let {
                ChangeTabHostTo(it)
            } ?: TopLevel
        } ?: NoChange
        navigateTo(location, addToHistory, tabHostTarget)
    }

    private fun navigateTo(
        location: L,
        addToHistory: Boolean = true,
        tabHostTarget: TabHostTarget
    ) {

        logger.d("navigateTo() ${location::class.simpleName} addToHistory:$addToHistory currentAddToHist:${state.willBeAddedToHistory} tabHostTarget:$tabHostTarget")

        val trimmed = if (!state.willBeAddedToHistory) {
            state.navigation.currentItem().applyOneStepBackNavigation()
        } else state.navigation

        val navigated = trimmed?.let { trimmedNav ->

            val itemSwap: Pair<Navigation<L, T>, Navigation<L, T>> = when (tabHostTarget) {
                is ChangeTabHostTo<*, *> -> {

                    @Suppress("UNCHECKED_CAST")
                    val tabHostSpec = tabHostTarget.target as TabHostSpecification<L, T>

                    requireValidTabHostClass(tabHostSpec)

                    trimmedNav.tabHostFinder(tabHostSpec.tabHostId)?.let { tabHost -> // tabHost already exists in navigation graph

                        logger.d("[${tabHostSpec.tabHostId}] Found")

                        tabHost to tabHost.addLocationToCurrentTab(location)

                    }  ?: run { // first time this tabHost has been added

                        logger.w("[${tabHostSpec.tabHostId}] Not Found, adding in place")

                        trimmed.currentItem().requireParent().isBackStack().let { parent ->
                            parent to parent.copy(
                                stack = parent.stack.toMutableList().also {
                                    it.add(tabsOf(tabHostSpec).addLocationToCurrentTab(location))
                                }
                            ).populateChildParents()
                        }
                    }
                }

                NoChange -> {
                    val parent = trimmedNav.currentItem().requireParent().isBackStack()
                    val newParent = parent.addLocation(location)
                    parent to newParent
                }

                TopLevel -> {
                    when (val parentWrapper = trimmedNav.topParent().notEndNode()) {
                        is RestrictedNavigation.NotEndNode.IsBackStack -> {
                            val parent = parentWrapper.value
                            val newParent = parent.addLocation(location)
                            parent to newParent
                        }

                        is RestrictedNavigation.NotEndNode.IsTabHost -> {
                            val parent = parentWrapper.value
                            val newParent = parent.addLocationToCurrentTab(location)
                            parent to newParent
                        }
                    }
                }
            }

            mutateNavigation(
                oldItem = itemSwap.first,
                newItem = itemSwap.second,
                ensureOnHistoryPath = true,
            )
        } ?: backStackOf<L, T>(endNodeOf(location))

        updateState(
            state.copy(
                navigation = navigated,
                willBeAddedToHistory = addToHistory
            )
        )
    }

//    fun navigateBackToTab(
//        tabHostSpec: TabHostSpecification<L, T>,
//        tabIndex: Int = 0,
//        addToHistory: Boolean = true
//    ) {
//
//        // TODO do we need to implement a similar things with the tabbed navigation?
//
//    }


    fun switchTab(
        tabHostSpec: TabHostSpecification<L, T>,
        tabIndex: Int = 0,
        clearToTabRootOverride: Boolean? = null,
    ) {
        logger.d(
            "switchTab() tabHostId:${tabHostSpec.tabHostId} tabIndex:$tabIndex currentAddToHist:${state.willBeAddedToHistory}"
        )

        require(tabHostSpec.homeTabLocations.size > tabIndex) {
            "tabIndex [$tabIndex] is out of bounds for tabs size:${tabHostSpec.homeTabLocations.size}"
        }
        require(tabIndex >= 0) {
            "tabIndex must be positive, $tabIndex is an invalid index"
        }
        requireValidTabHostClass(tabHostSpec)

        val trimmed = if (!state.willBeAddedToHistory) {
            state.navigation.currentItem().applyOneStepBackNavigation()
        } else state.navigation

        val navigated = trimmed?.currentItem()?.requireParent()?.isBackStack()?.let { parent ->

            val tabHost = trimmed.tabHostFinder(tabHostSpec.tabHostId)

            tabHost?.let { // tabHost already exists in navigation graph

                logger.d("[${tabHostSpec.tabHostId}] Found")

                val newSelectedHistory = when (tabHostSpec.backMode) {
                    TabBackMode.Structural -> listOf(tabIndex)
                    TabBackMode.Temporal -> {
                        tabHost.selectedTabHistory.filter { tab ->
                            tab != tabIndex
                        }.toMutableList().also { list ->
                            list.add(tabIndex)
                        }
                    }
                }

                val newTabs = tabHost.tabs.mapIndexed { index, backStack ->
                    if (index == tabIndex && (clearToTabRootOverride == true)
                        || (tabHost.clearToTabRootDefault && (clearToTabRootOverride != false))
                    ) {
                        backStackOf<L, T>(endNodeOf(tabHostSpec.homeTabLocations[tabIndex]))
                    } else {
                        backStack
                    }
                }

                mutateNavigation(
                    oldItem = tabHost,
                    newItem = tabHost.copy(
                        selectedTabHistory = newSelectedHistory,
                        tabs = newTabs
                    ),
                    ensureOnHistoryPath = true,
                )
            } ?: run { // first time this tabHost has been added

                logger.d("[${tabHostSpec.tabHostId}] Not Found, adding")

                val newParent = parent.copy(
                    stack = parent.stack.toMutableList().also {
                        it.add(tabsOf(tabHostSpec, tabIndex))
                    }
                ).populateChildParents()

                mutateNavigation(
                    oldItem = parent,
                    newItem = newParent
                )
            }
        } ?: tabsOf(tabHostSpec, tabIndex)

        updateState(
            state.copy(
                navigation = navigated,
                willBeAddedToHistory = true
            )
        )
    }

    private fun requireValidTabHostClass(tabHostSpec: TabHostSpecification<L, T>) {
        if (!tabHostClassChecked) {
            try {
                Json.encodeToString(
                    serializer(stateKType),
                    NavigationState(backStackOf(tabsOf(tabHostSpec, 0)))
                )
            } catch (e: Exception) {
                require(false) {
                    "\nFailed to serialise the specified tabHost type. There might be something wrong \n" +
                            "with your [TabHostIdClass]. \n\n" +
                            "* check the stateKType that you specified at construction, it needs to look like \n" +
                            "  this: stateKType = typeOf<NavigationState<MyLocationClass, MyTabHostIdClass>>() \n" +
                            "  where MyLocationClass is the class you are using to uniquely identify \n" +
                            "  the locations / pages of your app, and MyTabHostIdClass is the class you \n" +
                            "  are using to uniquely identify your TabHosts. Typically those classes will be \n" +
                            "  some kind of enum or sealed data class, but you could also use String. \n\n" +
                            "* you might have forgotten to add the kotlin serialization plugin to gradle, \n" +
                            "  see the sample app gradle build file. \n\n" +
                            "* you could have forgotten to add the @Serializable annotation to mark your \n" +
                            "  Location and TabHost classes serializable \n\n" +
                            "* are you running an obfuscated build? check the sample app for the \n" +
                            "  proguard rules to add (it's the same as when you are using ktor). The \n" +
                            "  actual error we received when trying to serialise the state follows... \n\n" +
                            e.toString()
                }
            }
            tabHostClassChecked = true
        }
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
    fun navigateBack(times: Int = 1, setData: (L) -> L = { it }): Boolean {
        logger.d("navigateBack() times:$times")

        var backUpSuccessful = true
        var newNavigation = state.navigation.currentItem()
        for (i in 1..times) {
            val backed = newNavigation.applyOneStepBackNavigation()
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
                navigation = setDataOnCurrentLocation(newNavigation, setData),
                willBeAddedToHistory = true
            )
        )

        return backUpSuccessful
    }

    fun navigateBackTo(location: L, tabHostId: T? = null, addToHistory: Boolean = true) {
        logger.d("navigateBackTo() ${location::class.simpleName} addToHistory:$addToHistory")

        val foundLocationNav = tabHostId?.let {
            state.navigation.reverseToLocation(
                location
            ) // TODO reverseToLocation(location, tabsOf(tabHostSpec = TabHostSpecification(T, emptyList())))
        } ?: state.navigation.reverseToLocation(location)

        // important, see note in reverseToLocation()
        state.navigation.populateChildParents()
        foundLocationNav?.populateChildParents()

        if (foundLocationNav != null) { //replace location as it might have different data
            Fore.d("navigateBackTo()... location FOUND in history: ${foundLocationNav.currentLocation()::class.simpleName}")
            val newNavigation = mutateNavigation(
                oldItem = foundLocationNav.currentItem(),
                newItem = endNodeOf(location)
            )
            updateState(
                state.copy(
                    navigation = newNavigation,
                    willBeAddedToHistory = addToHistory,
                )
            )
        } else { // didn't find location so just navigate forward
            Fore.d("navigateBackTo()... location NOT FOUND in history, navigating forward instead")
            navigateTo(location, addToHistory)
        }
    }

    private fun setDataOnCurrentLocation(
        currentItem: EndNode<L, T>,
        setData: (L) -> L = { it },
    ): Navigation<L, T> {
        return mutateNavigation(
            oldItem = currentItem,
            newItem = EndNode(setData(currentItem.location))
        )
    }

    fun reWriteNavigation(
        navigation: Navigation<L, T>,
        addToHistory: Boolean = true
    ) {
        Fore.d("reWriteNavigation() currentLocation: ${navigation.currentLocation()::class.simpleName} willBeAddedToHistory:$addToHistory")
        updateState(
            NavigationState(
                navigation = navigation,
                willBeAddedToHistory = addToHistory,
            )
        )
    }

    override fun toString(): String {
        return toString(diagnostics = false)
    }

    fun toString(diagnostics: Boolean = true): String {
        return state.navigation.toString(diagnostics)
    }

    private fun updateState(newState: NavigationState<L, T>) {
        state = newState
        notifyObservers()
        perSista.write(state, stateKType) {}
    }
}
