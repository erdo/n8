package co.early.n8

import MockObserver
import co.early.fore.core.delegate.Fore
import co.early.fore.core.delegate.TestDelegateDefault
import co.early.n8.LinearTestData.Location
import co.early.n8.LinearTestData.Location.EuropeanLocations.London
import co.early.n8.LinearTestData.Location.EuropeanLocations.Paris
import co.early.n8.LinearTestData.Location.NewYork
import co.early.n8.LinearTestData.Location.Tokyo
import co.early.persista.PerSista
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.reflect.typeOf
import kotlin.test.BeforeTest
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.test.assertTrue

class NavigationModelTest {

    private val dataPath: Path = "test".toPath()
    private val mockObserver = MockObserver()

    @BeforeTest
    fun setup() {
        Fore.setDelegate(TestDelegateDefault())
        okio.FileSystem.SYSTEM.deleteRecursively(dataPath)
    }

    @Test
    fun `instantiate with directory and homeLocation`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataPath = dataPath,
        )

        // act
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(London, navigationModel.state.currentLocation)
        assertEquals(null, navigationModel.state.comingFrom)
        assertEquals(false, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `instantiate with persista and homeLocation`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            perSista = PerSista(
                dataPath = dataPath,
            ),
        )

        // act
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(London, navigationModel.state.currentLocation)
        assertEquals(null, navigationModel.state.comingFrom)
        assertEquals(false, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `instantiate with directory and initialNavigationState`() {

        // arrange
        val navigationModel = NavigationModel(
            initialNavigation = backStackNoTabsOf(endNodeOf(Tokyo), endNodeOf(NewYork)),
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataPath = dataPath,
        )

        // act
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(NewYork, navigationModel.state.currentLocation)
        assertEquals(null, navigationModel.state.comingFrom)
        assertEquals(true, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `instantiate with persista and initialNavigationState`() {

        // arrange
        val navigationModel = NavigationModel(
            initialNavigation = backStackNoTabsOf(endNodeOf(Tokyo), endNodeOf(NewYork)),
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            perSista = PerSista(
                dataPath = dataPath,
            ),
        )

        // act
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(NewYork, navigationModel.state.currentLocation)
        assertEquals(null, navigationModel.state.comingFrom)
        assertEquals(true, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `instantiate with persista and tabHost initialNavigationState`() {

        // arrange
        val navigationModel = NavigationModel(
            initialNavigation = tabsOf(
                tabHistory = listOf(0, 1),
                tabHostId = "MyTabs",
                backStackOf(endNodeOf(Tokyo), endNodeOf(NewYork)),
                backStackOf(endNodeOf(London), endNodeOf(Paris)),
            ),
            stateKType = typeOf<NavigationState<Location, String>>(),
            perSista = PerSista(
                dataPath = dataPath,
            ),
        )

        // act
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(Paris, navigationModel.state.currentLocation)
        assertEquals(null, navigationModel.state.comingFrom)
        assertEquals(true, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `when instantiating with the wrong Location KType - exception is thrown`() {

        // arrange
        var exception: Exception? = null

        // act
        try {
            NavigationModel<Location, Unit>(
                homeLocation = London,
                stateKType = typeOf<NavigationState<String, Unit>>(),
                dataPath = dataPath,
            )
        } catch (e: Exception) {
            Fore.e(e.message ?: "exception with no message")
            exception = e
        }

        // assert
        assertNotEquals(null, exception)
    }

    @Test
    fun `when adding a tabHost - with the wrong TabHost KType specified in the constructor - exception is thrown`() {

        // arrange
        var exception: Exception? = null
        val navigationModel = NavigationModel<Location, Int>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, String>>(),
            dataPath = dataPath,
        )

        // act
        try {
            navigationModel.switchTab(
                TabHostSpecification(
                    tabHostId = 1,
                    homeTabLocations = listOf(Paris, NewYork),
                )
            )
        } catch (e: Exception) {
            Fore.e(e.message ?: "exception with no message")
            exception = e
        }

        // assert
        assertNotEquals(null, exception)
    }

    /**
     * NB all we are checking here is that observers are called AT LEAST once
     *
     * We don't really want tie our tests (OR any observers in production code)
     * to an expected number of times they are notified. (This would be
     * testing an implementation detail and make the tests unnecessarily brittle)
     *
     * The contract says nothing about how many times the observers will get called,
     * only that they will be called if something changes ("something" is not defined
     * and can change between implementations).
     *
     * (This is similar to how Composables are written - they should not be written with the
     * expectation that they will be composed a certain number of times)
     */
    @Test
    fun `when navigating forward - observers are notified`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataPath = dataPath,
        )
        navigationModel.addObserver(mockObserver)

        // act
        navigationModel.navigateTo(Tokyo)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertTrue(mockObserver.notifications() > 0)
    }

    @Test
    fun `when navigating back to a previous location - observers are notified`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataPath = dataPath,
        )

        // act
        navigationModel.navigateTo(Tokyo)
        navigationModel.addObserver(mockObserver) // add observer here
        navigationModel.navigateBackTo(London)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertTrue(mockObserver.notifications() > 0)
    }

    @Test
    fun `when navigating back - observers are notified`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataPath = dataPath,
        )

        // act
        navigationModel.navigateTo(Tokyo)
        navigationModel.addObserver(mockObserver) // add observer here
        navigationModel.navigateBack()
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertTrue(mockObserver.notifications() > 0)
    }

    @Test
    fun `when rewriting back stack - observers are notified`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataPath = dataPath,
        )

        // act
        navigationModel.navigateTo(Tokyo)
        navigationModel.addObserver(mockObserver) // add observer here
        navigationModel.reWriteNavigation(
            navigation = backStackOf(
                endNodeOf(Paris),
            )
        )
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertTrue(mockObserver.notifications() > 0)
    }

    @Test
    fun `navigation state is persisted between instantiations`() {

        // arrange
        var navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataPath = dataPath,
        )

        // act
        navigationModel.navigateTo(Tokyo, addToHistory = false)

        Fore.e(navigationModel.toString(diagnostics = true))

        navigationModel = NavigationModel(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataPath = dataPath,
        )

        Fore.e(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(Tokyo, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(null, navigationModel.state.comingFrom) // comingFrom is not persisted
        assertEquals(false, navigationModel.state.willBeAddedToHistory)
    }

    @Test
    fun `navigation state is not persisted between instantiations when clear flag is set in constructor`() {

        // arrange
        var navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataPath = dataPath,
        )

        // act
        navigationModel.navigateTo(Tokyo, addToHistory = false)

        Fore.e(navigationModel.toString(diagnostics = true))

        navigationModel = NavigationModel(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataPath = dataPath,
            clearPreviousNavGraph = true,
        )

        Fore.e(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(London, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(null, navigationModel.state.comingFrom) // comingFrom is not persisted
        assertEquals(true, navigationModel.state.willBeAddedToHistory)
    }

    @Test
    fun `clearing navigation state functions correctly`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataPath = dataPath,
        )
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(NewYork)

        // act
        navigationModel.clearNavigationGraph()

        Fore.e(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(London, navigationModel.state.currentLocation)
        assertEquals(null, navigationModel.state.comingFrom)
        assertEquals(false, navigationModel.state.canNavigateBack)
    }
}
