@file:OptIn(LowLevelApi::class)

package co.early.n8

import co.early.fore.core.observer.Observable
import co.early.fore.core.coroutine.launchIO
import co.early.fore.core.delegate.Fore
import co.early.fore.core.logging.Logger
import co.early.fore.core.observer.ObservableImp
import co.early.n8.Navigation.EndNode
import co.early.n8.NavigationModel.TabHostTarget.ChangeTabHostTo
import co.early.n8.NavigationModel.TabHostTarget.NoChange
import co.early.n8.NavigationModel.TabHostTarget.TopLevel
import co.early.n8.lowlevel.LowLevelApi
import co.early.n8.lowlevel.RestrictedNavigation
import co.early.n8.lowlevel._addLocation
import co.early.n8.lowlevel._addLocationToCurrentTab
import co.early.n8.lowlevel._applyOneStepBackNavigation
import co.early.n8.lowlevel._ensureUniqueTabHosts
import co.early.n8.lowlevel._isBackStack
import co.early.n8.lowlevel._mutateNavigation
import co.early.n8.lowlevel._notEndNode
import co.early.n8.lowlevel._populateChildParents
import co.early.n8.lowlevel._requireParent
import co.early.n8.lowlevel._reverseToLocation
import co.early.n8.lowlevel._tabHostFinder
import co.early.persista.PerSista
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okio.Path
import kotlin.reflect.KType

/**
 * # Navigation
 *
 * This model is the source of truth for the navigation state of an app. The navigation state's
 * most important component is the navigation graph. The navigation graph is built up of nodes
 * (or navigation items) that describe the locations that were visited by a user to reach their
 * current location.
 *
 * This navigation graph is built up using 3 Node types:
 *
 * - EndNodes (representing a single location)
 * - BackStacks (a list which mostly contain EndNodes, but can also contain TabHosts)
 * - TabHosts (a list of BackStacks)
 *
 * We consider the "top level item" to be the root item in the navigation graph, it's the first
 * item to be created in a navigation graph and often hosts the "home" location that the user
 * encountered when starting their app session (and would return to if they kept pressing back).
 * The top level item is also the only navigation item that does not have a parent, and it is
 * always a TabHost or a BackStack
 *
 * We consider the "current" location to be where the user is currently, it is at the opposite
 * end of the navigation graph to the top level item. A navigation graph containing only one
 * location would mean that the current location and the home location are the same
 *
 * backStackOf( <--- top level item
 *     endNodeOf(Paris), <--- home location
 *     endNodeOf(London),
 *     endNodeOf(Rome), <--- current location
 * )
 *
 * This represents a very simple navigation graph, a single BackStack containing 3
 * EndNodes. Paris is the home location, Rome is the current location
 *
 * The 'back path" is the route from the current location to the home location that the user
 * would travel on by continually pressing back until they exit the app. In the example above
 * the navigation graph and the back path are the same, but when tab hosts are included there
 * will often be locations in the navigation graph that are not on the back path and cannot
 * be accessed by the user just by navigating backwards
 *
 * backStackOf( <--- top level item
 *     endNodeOf(NewYork), <--- home location
 *     endNodeOf(Tokyo),
 *     tabsOf(
 *         tabHistory = listOf(0),
 *         tabHostId = "European Cities",
 *         backStackOf(
 *             endNodeOf(London)
 *             endNodeOf(Manchester) <--- current location
 *         ),
 *         backStackOf(
 *             endNodeOf(Paris)
 *         ),
 *         backStackOf(
 *             endNodeOf(Rome)
 *         )
 *     )
 * )
 *
 * In the above case, the back path comprises: Manchester, London, Tokyo, New York. The locations Paris
 * and Rome exist in the navigation graph, but they are not on the back path.
 *
 * Public functions provide for navigating forward and backwards as the user navigates
 * around the app, and also for switching tabs and jumping between completely different
 * tab hosts which may or may not be nested
 *
 * Aside from regular navigation functions, the navigation state can also be completely
 * rewritten or reordered at will to provide for custom navigation schemes or the
 * handling of deep links etc.
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
 * # Basic Usage
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
 * navigateBack()
 *
 * backstack: London > Paris
 * currentLocation: Paris
 *
 *
 * C. Back navigation multiple steps
 *
 * navigateTo(Paris)
 * navigateTo(NewYork)
 * navigateBack(times = 2)
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
 * navigateBack { it -> // the new current location after popping backstack i.e. Sydney
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
    private val initialNavigation: Navigation<L, T>,
    private val initialWillBeAddedToHistoryFlag: Boolean = true,
    private val perSista: PerSista,
    clearPreviousNavGraph: Boolean = false,
    private val logger: Logger = Fore.getLogger(),
) : Observable by ObservableImp() {

    // a lot of these constructors are for iOS target benefit which doesn't like default parameters in constructors

    constructor(
        stateKType: KType,
        initialNavigation: Navigation<L, T>,
        perSista: PerSista,
    ) : this(
        stateKType = stateKType,
        initialNavigation = initialNavigation,
        initialWillBeAddedToHistoryFlag = true,
        perSista = perSista,
        clearPreviousNavGraph = false,
        logger = Fore.getLogger(),
    )

    constructor(
        stateKType: KType,
        homeLocation: L,
        initialWillBeAddedToHistoryFlag: Boolean = true,
        perSista: PerSista,
        clearPreviousNavGraph: Boolean = false,
        logger: Logger = Fore.getLogger(),
    ) : this(
        stateKType = stateKType,
        initialNavigation = backStackOf<L, T>(endNodeOf(homeLocation)),
        initialWillBeAddedToHistoryFlag = initialWillBeAddedToHistoryFlag,
        perSista = perSista,
        clearPreviousNavGraph = clearPreviousNavGraph,
        logger = logger,
    )

    constructor(
        stateKType: KType,
        homeLocation: L,
        perSista: PerSista,
    ) : this(
        stateKType = stateKType,
        initialNavigation = backStackOf<L, T>(endNodeOf(homeLocation)),
        initialWillBeAddedToHistoryFlag = true,
        perSista = perSista,
        clearPreviousNavGraph = false,
        logger = Fore.getLogger(),
    )

    constructor(
        stateKType: KType,
        homeLocation: L,
        initialWillBeAddedToHistoryFlag: Boolean = true,
        dataPath: Path,
        clearPreviousNavGraph: Boolean = false,
        logger: Logger = Fore.getLogger(),
    ) : this(
        stateKType = stateKType,
        initialNavigation = backStackOf<L, T>(endNodeOf(homeLocation)),
        initialWillBeAddedToHistoryFlag = initialWillBeAddedToHistoryFlag,
        perSista = PerSista(
            dataPath = dataPath,
            logger = logger,
        ),
        clearPreviousNavGraph = clearPreviousNavGraph,
        logger = logger,
    )

    constructor(
        stateKType: KType,
        homeLocation: L,
        dataPath: Path,
    ) : this(
        stateKType = stateKType,
        initialNavigation = backStackOf<L, T>(endNodeOf(homeLocation)),
        initialWillBeAddedToHistoryFlag = true,
        perSista = PerSista(
            dataPath = dataPath,
            logger = Fore.getLogger(),
        ),
        clearPreviousNavGraph = false,
        logger = Fore.getLogger(),
    )

    constructor(
        stateKType: KType,
        initialNavigation: Navigation<L, T>,
        initialWillBeAddedToHistoryFlag: Boolean = true,
        dataPath: Path,
        clearPreviousNavGraph: Boolean = false,
        logger: Logger = Fore.getLogger(),
    ) : this(
        stateKType = stateKType,
        initialNavigation = initialNavigation,
        initialWillBeAddedToHistoryFlag = initialWillBeAddedToHistoryFlag,
        perSista = PerSista(
            dataPath = dataPath,
            logger = logger,
        ),
        clearPreviousNavGraph = clearPreviousNavGraph,
        logger = logger,
    )

    constructor(
        stateKType: KType,
        initialNavigation: Navigation<L, T>,
        dataPath: Path,
    ) : this(
        stateKType = stateKType,
        initialNavigation = initialNavigation,
        initialWillBeAddedToHistoryFlag = true,
        perSista = PerSista(
            dataPath = dataPath,
            logger = Fore.getLogger(),
        ),
        clearPreviousNavGraph = false,
        logger = Fore.getLogger(),
    )

    var state = NavigationState(
        navigation = initialNavigation,
        willBeAddedToHistory = initialWillBeAddedToHistoryFlag,
    )
        private set

    private var tabHostClassChecked = false

    private sealed class TabHostTarget {
        data object NoChange : TabHostTarget()
        data object TopLevel : TabHostTarget()
        data class ChangeTabHostTo<L : Any, T : Any>(val target: TabHostSpecification<L, T>) : TabHostTarget()
    }

    fun interface NavigationInterceptor<L : Any, T : Any> {
        fun intercept(old: NavigationState<L, T>, new: NavigationState<L, T>): NavigationState<L, T>
    }

    private val navigationInterceptors = mutableListOf<Pair<String, NavigationInterceptor<L, T>>>()

    init {
        try {
            Json.encodeToString(serializer(stateKType), state)
        } catch (e: Exception) {
            throw Exception(
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
            )
        }
        if (clearPreviousNavGraph) {
            updateState(state)
        } else {
            load()
        }
    }

    private fun load() {

        logger.d("load()")

        if (state.initialLoading) {
            return
        }
        state = state.copy(initialLoading = true)
        notifyObservers()

        launchIO {
            perSista.read(state, stateKType) {
                state = it.copy(
                    initialLoading = false,
                    navigation = it.navigation._populateChildParents()
                )
                notifyObservers()
            }
        }
    }

    /**
     * @addToHistory refers to the screen about to be navigated _to_ when it eventually is navigated away
     * from
     * Usage:
     * navigateTo(A) // navigate to location A in current tab (or in the current backStack if there is no
     * tab host for the current location)
     * navigateTo(A) { TabsAbc } // find tab host TabsABC, and navigate to location A from there. If TabsABC
     * does not exist in the navigation graph, create it at the current location first. Note that this can be
     * a destructive operation, if the tab host identified is further back in the navigation graph, in order
     * for it to be made current, the navigation items further forward from that point may be removed from
     * the navigation graph
     * navigateTo(A) { null } // jump out of any tab host(s) and continue at the top level host whether that
     * is a backStack or a tabHost
     */
    fun navigateTo(location: L, addToHistory: Boolean = true, breakTo: BreakToTabHost<L, T> = null) {
        val tabHostTarget: TabHostTarget = breakTo?.let {
            breakTo()?.let {
                ChangeTabHostTo(it)
            } ?: TopLevel
        } ?: NoChange
        navigateTo(location, addToHistory, tabHostTarget)
    }

    /** for kmp swift benefit (where default values don't work) **/
    fun navigateTo(location: L, addToHistory: Boolean) {
        navigateTo(location, addToHistory, null)
    }

    /** for kmp swift benefit (where default values don't work) **/
    fun navigateTo(location: L) {
        navigateTo(location, true, null)
    }

    private fun navigateTo(
        location: L,
        addToHistory: Boolean = true,
        tabHostTarget: TabHostTarget
    ) {

        logger.d("navigateTo() ${location::class.simpleName} addToHistory:$addToHistory currentAddToHist:${state.willBeAddedToHistory} tabHostTarget:$tabHostTarget")

        val trimmed = if (!state.willBeAddedToHistory) {
            state.navigation.currentItem()._applyOneStepBackNavigation()
        } else state.navigation

        val navigated = trimmed?.let { trimmedNav ->

            val itemSwap: Pair<Navigation<L, T>, Navigation<L, T>> = when (tabHostTarget) {
                is ChangeTabHostTo<*, *> -> {

                    @Suppress("UNCHECKED_CAST")
                    val tabHostSpec = tabHostTarget.target as TabHostSpecification<L, T>

                    requireValidTabHostClass(tabHostSpec)

                    trimmedNav._tabHostFinder(tabHostSpec.tabHostId)?.let { tabHost -> // tabHost already exists

                        logger.d("[${tabHostSpec.tabHostId}] Found")

                        tabHost to tabHost._addLocationToCurrentTab(location)

                    } ?: run { // first time this tabHost has been added

                        logger.w("[${tabHostSpec.tabHostId}] Not Found, adding in place")

                        trimmed.currentItem()._requireParent()._isBackStack().let { parent ->
                            parent to parent.copy(
                                stack = parent.stack.toMutableList().also {
                                    it.add(tabsOf(tabHostSpec)._addLocationToCurrentTab(location))
                                }
                            )._populateChildParents()
                        }
                    }
                }

                NoChange -> {
                    val parent = trimmedNav.currentItem()._requireParent()._isBackStack()
                    val newParent = parent._addLocation(location)
                    parent to newParent
                }

                TopLevel -> {
                    when (val parentWrapper = trimmedNav.topItem()._notEndNode()) {
                        is RestrictedNavigation.NotEndNode.IsBackStack -> {
                            val parent = parentWrapper.value
                            val newParent = parent._addLocation(location)
                            parent to newParent
                        }

                        is RestrictedNavigation.NotEndNode.IsTabHost -> {
                            val parent = parentWrapper.value
                            val newParent = parent._addLocationToCurrentTab(location)
                            parent to newParent
                        }
                    }
                }
            }

            _mutateNavigation(
                oldItem = itemSwap.first,
                newItem = itemSwap.second,
                ensureOnHistoryPath = true,
            )
        } ?: backStackOf<L, T>(endNodeOf(location))

        updateState(
            state.copy(
                navigation = navigated,
                willBeAddedToHistory = addToHistory,
                comingFrom = state.currentLocation,
            )
        )
    }

    /**
     * If the tabHostSpec is not specified, the nearest hosting tabHost will be used i.e. the value we get from
     * hostedBy.last() If hostedBy returns an empty List, then the navigation operation will fail (the function
     * will return false).
     *
     * Depending on how you structure your client code, consider always specifying the tabHostSpec - in that case
     * if the tabHost does not already exist in the navigation graph, n8 can create it in place. This style is also
     * clearer in the case that the navigation graph has multiple nested TabHosts (n8 always switches on the deepest
     * tabHost in this case)
     *
     * Be aware that a runtime exception will be thrown if you specify a tabIndex which is out of bounds for the tabHost
     */
    fun switchTab(
        tabHostSpec: TabHostSpecification<L, T>? = null,
        tabIndex: Int? = null,
        clearToTabRootOverride: Boolean? = null,
    ): Boolean {
        logger.d("switchTab() tabId:${tabHostSpec?.tabHostId} index:$tabIndex currentAddToHist:${state.willBeAddedToHistory}")

        require(tabHostSpec != null || tabIndex != null) {
            "tabHostSpec or tabIndex must be specified, they can't both be null"
        }
        require(tabIndex == null || tabIndex >= 0) {
            "tabIndex must be positive, $tabIndex is an invalid index"
        }

        val trimmed = if (!state.willBeAddedToHistory) {
            state.navigation.currentItem()._applyOneStepBackNavigation()
        } else state.navigation

        val navigated = trimmed?.currentItem()?._requireParent()?._isBackStack()?.let { parent ->

            val tabHostId = tabHostSpec?.let {
                requireValidTabHostClass(tabHostSpec)
                it.tabHostId
            } ?: run {
                trimmed.currentItem().hostedBy().lastOrNull()?.tabHostId
            }

            logger.d("looking for tabHostId:$tabHostId")

            val tabHost = tabHostId?.let { trimmed._tabHostFinder(it) }

            tabHost?.let { // tabHost already exists in navigation graph

                logger.d("[${tabHost.tabHostId}] Found, tabIndex specified: $tabIndex")

                tabIndex?.let {

                    require(tabHost.tabs.size > tabIndex) {
                        "tabIndex [$tabIndex] is out of bounds for [${tabHost.tabHostId}] tabs size:${tabHost.tabs.size}"
                    }

                    val newTabHistory = when (tabHost.tabBackModeDefault) {
                        TabBackMode.Structural -> listOf(tabIndex)
                        TabBackMode.Temporal -> {
                            tabHost.tabHistory.filter { tab ->
                                tab != (tabIndex)
                            }.toMutableList().also { list ->
                                list.add(tabIndex)
                            }
                        }
                    }
                    val newTabs = tabHost.tabs.mapIndexed { index, backStack ->
                        if (index == tabIndex && ((clearToTabRootOverride == true)
                            || (tabHost.clearToTabRootDefault && (clearToTabRootOverride != false)))
                        ) {
                            backStack.copy(stack = listOf(backStack.stack.first()))
                        } else {
                            backStack
                        }
                    }

                    _mutateNavigation(
                        oldItem = tabHost,
                        newItem = tabHost.copy(
                            tabHistory = newTabHistory,
                            tabs = newTabs
                        ),
                        ensureOnHistoryPath = true,
                    )
                }?: run {// make no changes, but ensure we are on the history path
                    _mutateNavigation(
                        oldItem = tabHost,
                        newItem = tabHost,
                        ensureOnHistoryPath = true,
                    )
                }
            } ?: run { // first time this tabHost has been added

                if (tabHostSpec == null){
                    logger.w("No tabHostSpec specified, and no suitable TabHost found, cannot create in place!")
                    return false
                } else {

                    logger.d("[${tabHostId}] NOT FOUND in navigation graph! creating in place")

                    val newParent = parent.copy(
                        stack = parent.stack.toMutableList().also {
                            it.add(tabsOf(tabHostSpec, tabIndex))
                        }
                    )._populateChildParents()

                    _mutateNavigation(
                        oldItem = parent,
                        newItem = newParent
                    )
                }
            }
        } ?: run {// trimmed to nothing, create TabHost in place if possible
            if (tabHostSpec == null){
                logger.w("Trimmed to nothing and no tabHostSpec specified, cannot create in place!")
                return false
            } else {
                tabsOf(tabHostSpec, tabIndex)
            }
        }

        updateState(
            state.copy(
                navigation = navigated,
                willBeAddedToHistory = true,
                comingFrom = state.currentLocation,
            )
        )
        return true
    }

    /** for kmp swift benefit (where default values don't work) **/
    fun switchTab(
        tabHostSpec: TabHostSpecification<L, T>,
    ): Boolean {
        return switchTab(tabHostSpec, null, null)
    }

    /** for kmp swift benefit (where default values don't work) **/
    fun switchTab(
        tabIndex: Int,
    ): Boolean {
        return switchTab(null, tabIndex, null)
    }

    /** for kmp swift benefit (where default values don't work) **/
    fun switchTab(
        tabHostSpec: TabHostSpecification<L, T>,
        tabIndex: Int,
    ): Boolean {
        return switchTab(tabHostSpec, tabIndex, null)
    }

    private fun requireValidTabHostClass(tabHostSpec: TabHostSpecification<L, T>) {
        if (!tabHostClassChecked) {
            try {
                Json.encodeToString(
                    serializer(stateKType),
                    NavigationState(backStackOf(tabsOf(tabHostSpec)))
                )
            } catch (e: Exception) {
                throw Exception(
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
                )
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
     * @addToHistory - set this to false in the unlikely event that you want to navigate back to a
     * location, and ALSO don't want that location added to history when the user next navigates
     * away from that location. Defaults to true which is the usual behaviour
     *
     * returns false if we were not able to go back the requested number of times (i.e. we reached
     * the home location first, the state will be updated with the home location as current)
     */
    fun navigateBack(times: Int = 1, addToHistory: Boolean = true, setData: (L) -> L = { it }): Boolean {
        logger.d("navigateBack() times:$times")

        require(times > 0) {
            "times must be larger than 0"
        }

        var backUpSuccessful = true
        var newNavigation = state.navigation.currentItem()
        for (i in 1..times) {
            val backed = newNavigation._applyOneStepBackNavigation()
            if (backed != null) {
                newNavigation = backed.currentItem()
            } else {
                logger.d("navigateBack()... no more room to back up")
                backUpSuccessful = false
                break
            }
        }

        if (backUpSuccessful || times > 1) {
            updateState(
                state.copy(
                    navigation = setDataOnCurrentLocation(newNavigation, setData),
                    willBeAddedToHistory = addToHistory,
                    comingFrom = state.currentLocation,
                )
            )
        }

        return backUpSuccessful
    }

    /** for kmp swift benefit (where default values don't work) **/
    fun navigateBack(times: Int, addToHistory: Boolean): Boolean {
        return navigateBack(times, addToHistory, { it })
    }

    /** for kmp swift benefit (where default values don't work) **/
    fun navigateBack(times: Int): Boolean {
        return navigateBack(times, true, { it })
    }

    /** for kmp swift benefit (where default values don't work) **/
    fun navigateBack(): Boolean {
        return navigateBack(1, true, { it })
    }

    /**
     * @addToHistory refers to the screen about to be navigated _to_ when it eventually is navigated away
     * from
     * Note: there is no need for a setData{} parameter here, you set the data on the Location itself before
     * passing it in. If you don't want to prevent this behaviour and use whatever data the destination
     * already has set @passingData to false
     *
     * By default if the location is not found it will be created in place. Note that if you are navigating back
     * to a location that you expect to find in a different tab or even tabHost, you might not want to create
     * it inside your current Tab, to prevent this behaviour set @createInPlaceIfNotFound to false. Another option
     * is to first switch to the Tab or TabHost you expect to find your location in and search from there (in
     * which case if the location is not found, it will be created in place at the correct tab or tabHost)
     *
     * @returns false if location was not found AND was not created
     */
    fun navigateBackTo(location: L, addToHistory: Boolean = true, passingData: Boolean = true, createInPlaceIfNotFound: Boolean = true): Boolean {
        logger.d("navigateBackTo() location:$location addToHistory:$addToHistory")

        val trimmed = if (!state.willBeAddedToHistory) {
            state.navigation.currentItem()._applyOneStepBackNavigation()
        } else state.navigation

        trimmed?.let {
            it._reverseToLocation(location)?.let { foundLocationNav ->

                foundLocationNav._populateChildParents() // see _reverseToLocation() docs

                logger.d("navigateBackTo()... location FOUND in history: ${foundLocationNav.currentLocation()::class.simpleName}")

                val newNavigation = _mutateNavigation(
                    oldItem = foundLocationNav.currentItem(),
                    newItem = if (passingData) { endNodeOf(location) } else foundLocationNav.currentItem()
                )
                updateState(
                    state.copy(
                        navigation = newNavigation,
                        willBeAddedToHistory = addToHistory,
                        comingFrom = state.currentLocation,
                    )
                )

                return true
            } ?: run { // didn't find location so just navigate forward
                state.navigation._populateChildParents() // see _reverseToLocation() docs
                logger.d("navigateBackTo()... location NOT FOUND in history, createInPlaceIfNotFound:$createInPlaceIfNotFound")
                if (createInPlaceIfNotFound) {
                    navigateTo(location, addToHistory)
                }
            }
        } ?: run { // trimmed has removed all remaining nav

            logger.d("trimmed to nothing")

            if (createInPlaceIfNotFound) {
                updateState(
                    NavigationState(
                        navigation = backStackOf(endNodeOf(location)),
                        willBeAddedToHistory = addToHistory,
                        comingFrom = state.currentLocation,
                    )
                )
            }
        }
        return createInPlaceIfNotFound
    }

    /** for kmp swift benefit (where default values don't work) **/
    fun navigateBackTo(location: L): Boolean {
        return navigateBackTo(location, true, true)
    }

    /**
     * Sometimes you might not know which specific Location you want to navigate back to (e.g. Audio settings,
     * Video Settings or Account Settings), you just know you want to navigate back to a specific tabHost
     * (e.g. TabHostId.Settings). With this function you will land on whatever location is current on the tabHost
     * requested (providing that tabHost exists on the back path)
     *
     * @addToHistory refers to the screen about to be navigated _to_ when it eventually is navigated away
     * from
     *
     * By default if the tabHost is not found it will be created in place. Note that if you are navigating back
     * to a tabHost that you expect to find nested in a different tab or tabHost, you might not want to create
     * it inside your current Tab, to prevent this behaviour set @createInPlaceIfNotFound to false. Another option
     * is to first switch to the Tab or TabHost you expect to find your tabHost hosted within and search from there (in
     * which case if the location is not found, it will be created in place at the correct tab or tabHost)
     *
     * @returns false if location was not found AND was not created
     */
    fun navigateBackTo(tabHostSpec: TabHostSpecification<L, T>, addToHistory: Boolean = true, createInPlaceIfNotFound: Boolean = true, setData: (L) -> L = { it }) : Boolean {
        logger.d("navigateBackTo() tabHostId:${tabHostSpec.tabHostId} addToHistory:$addToHistory")

        requireValidTabHostClass(tabHostSpec)

        if (state.hostedBy.find { it.tabHostId == tabHostSpec.tabHostId } != null) {

            logger.w(
                "Already hosted by this TabHost! hostedBy: " +
                        state.hostedBy.map { it.tabHostId }.joinToString(" > ")
            )

            updateState(
                state.copy(
                    navigation = setDataOnCurrentLocation(state.navigation.currentItem(), setData),
                    willBeAddedToHistory = addToHistory,
                    comingFrom = state.currentLocation,
                )
            )
            return true
        }

        val trimmed = if (!state.willBeAddedToHistory) {
            state.navigation.currentItem()._applyOneStepBackNavigation()
        } else state.navigation

        trimmed?.let {
            it._tabHostFinder(tabHostSpec.tabHostId)?.let { foundTabHostNav ->

                logger.d("navigateBackTo()... tabHost ${foundTabHostNav.tabHostId} FOUND in nav graph")

                val newNavigation = _mutateNavigation(
                    oldItem = foundTabHostNav,
                    newItem = foundTabHostNav,
                    ensureOnHistoryPath = true,
                )
                updateState(
                    state.copy(
                        navigation = setDataOnCurrentLocation(newNavigation.currentItem(), setData),
                        willBeAddedToHistory = addToHistory,
                        comingFrom = state.currentLocation,
                    )
                )

                return true
            } ?: run {
                logger.d("tabHost ${tabHostSpec.tabHostId} NOT FOUND in navigation graph, createInPlaceIfNotFound:$createInPlaceIfNotFound")

                if (createInPlaceIfNotFound) {
                    trimmed.currentItem()._requireParent()._isBackStack().let { parent ->

                        val newParent = parent.copy(
                            stack = parent.stack.toMutableList().also { parentStack ->
                                parentStack.add(tabsOf(tabHostSpec))
                            }
                        )._populateChildParents()

                        val newNavigation = _mutateNavigation(
                            oldItem = parent,
                            newItem = newParent
                        )

                        updateState(
                            state.copy(
                                navigation = setDataOnCurrentLocation(newNavigation.currentItem(), setData),
                                comingFrom = state.currentLocation,
                                willBeAddedToHistory = addToHistory
                            )
                        )
                    }
                }
            }
        } ?: run { // trimmed has removed all remaining nav

            logger.d("trimmed to nothing, creating as home item createInPlaceIfNotFound:$createInPlaceIfNotFound")

            if (createInPlaceIfNotFound) {
                updateState(
                    NavigationState(
                        navigation = setDataOnCurrentLocation(tabsOf(tabHostSpec).currentItem(), setData),
                        willBeAddedToHistory = addToHistory,
                        comingFrom = state.currentLocation,
                    )
                )
            }
        }
        return createInPlaceIfNotFound
    }


    /** for kmp swift benefit (where default values don't work) **/
    fun navigateBackTo(tabHostSpec: TabHostSpecification<L, T>, setData: (L) -> L = { it }): Boolean {
        return navigateBackTo(tabHostSpec, true, true, setData)
    }

    /** for kmp swift benefit (where default values don't work) **/
    fun navigateBackTo(tabHostSpec: TabHostSpecification<L, T>): Boolean {
        return navigateBackTo(tabHostSpec, true, true, { it })
    }

    /**
     * This is intended to be called right before a forward navigation operation,
     * and it relates to the current location, before the navigation operation is
     * performed. As such it doesn't cause an observer notification to fire, and
     * n8 doesn't persist the willBeAddedToHistory value you set (it's intended
     * to be remembered momentarily while the next navigation operation is
     * processed only)
     */
    fun overrideWillBeAddedToHistory(willBeAddedToHistory: Boolean) {
        state = state.copy(willBeAddedToHistory = willBeAddedToHistory)
    }

    private fun setDataOnCurrentLocation(
        currentItem: EndNode<L, T>,
        setData: (L) -> L = { it },
    ): Navigation<L, T> {
        return _mutateNavigation(
            oldItem = currentItem,
            newItem = EndNode(setData(currentItem.location))
        )
    }

    fun clearNavigationGraph() {
        updateState(
            NavigationState(
                navigation = initialNavigation._populateChildParents(),
                willBeAddedToHistory = initialWillBeAddedToHistoryFlag,
                comingFrom = null,
            )
        )
    }

    fun reWriteNavigation(
        navigation: Navigation<L, T>,
        addToHistory: Boolean = true, //applies to the "current" location of the new navigation graph only
    ) {
        logger.d("reWriteNavigation() currentLocation: ${navigation.currentLocation()::class.simpleName} willBeAddedToHistory:$addToHistory")

        navigation._ensureUniqueTabHosts()

        updateState(
            NavigationState(
                navigation = navigation,
                willBeAddedToHistory = addToHistory,
                comingFrom = state.currentLocation,
            )
        )
    }

    /** for kmp swift benefit (where default values don't work) **/
    fun reWriteNavigation(navigation: Navigation<L, T>) {
        reWriteNavigation(navigation, true)
    }

    override fun toString(): String {
        return toString(diagnostics = false)
    }

    fun toString(diagnostics: Boolean = true): String {
        return state.navigation.toString(diagnostics)
    }

    private fun updateState(newState: NavigationState<L, T>) {
        state = checkInterceptors(newState)
        notifyObservers()
        perSista.write(state, stateKType) {}
    }

    private fun checkInterceptors(newState: NavigationState<L, T>): NavigationState<L, T> {
        var modifiedState = newState
        for ((_, interceptor) in navigationInterceptors) {
            modifiedState = interceptor.intercept(state, modifiedState)
        }
        return modifiedState
    }

    fun installInterceptor(name: String, interceptor: NavigationInterceptor<L, T>): NavigationModel<L, T> {
        uninstallInterceptor(name)
        navigationInterceptors.add(name to interceptor)
        return this
    }

    fun uninstallInterceptor(name: String) {
        navigationInterceptors.removeAll { it.first == name }
    }

    suspend fun import(serializedNav: String, setAsState: Boolean = true): NavigationState<L, T> {
        @Suppress("UNCHECKED_CAST")
        val deSerializedState = (Json.decodeFromString(serializer(stateKType), serializedNav) as NavigationState<L, T>)
        val newState = deSerializedState.copy(
            navigation = deSerializedState.navigation._populateChildParents()
        )
        newState.navigation._ensureUniqueTabHosts()
        if (setAsState) {
            updateState(newState.copy(comingFrom = state.currentLocation))
        }
        return newState
    }

    /**
     * Exports the given navigation, if either navigationGraph or willBeAddedToHistory are not set
     * the current values from the NavigationModel will be used
     */
    suspend fun export(navigationGraph: Navigation<L, T>? = null, willBeAddedToHistory: Boolean? = null): String {
        return Json.encodeToString(
            serializer(stateKType),
            NavigationState(
                navigation = navigationGraph ?: state.navigation,
                willBeAddedToHistory = willBeAddedToHistory ?: state.willBeAddedToHistory
            )
        )
    }
}
