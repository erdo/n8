@file:OptIn(LowLevelApi::class)

package co.early.n8

import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.n8.LinearTestData.Location
import co.early.n8.LinearTestData.Location.EuropeanLocations.London
import co.early.n8.LinearTestData.Location.EuropeanLocations.Paris
import co.early.n8.LinearTestData.Location.NewYork
import co.early.n8.LinearTestData.Location.SunCreamSelector
import co.early.n8.LinearTestData.Location.Sydney
import co.early.n8.LinearTestData.Location.Tokyo
import co.early.n8.lowlevel.LowLevelApi
import co.early.n8.lowlevel._isBackStack
import io.mockk.MockKAnnotations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.reflect.typeOf

class NavigationModelLinearNavTest {

    private lateinit var dataDirectory: File

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        Fore.setDelegate(TestDelegateDefault())

        val dataFolder = TemporaryFolder()
        dataFolder.create()
        dataDirectory = dataFolder.newFolder()
    }

    @Test
    fun `when navigating forward, back stack is added to`() {

        // arrange
        val navigationModel = NavigationModel<Location, Int>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Int>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(NewYork)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(3, navigationModel.state.backsToExit)
        assertEquals(NewYork, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(0, navigationModel.state.hostedBy.size)
    }

    @Test
    fun `when navigating back with room, location is reverted & returns true`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris)
        val result = navigationModel.navigateBack()
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(London, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(true, result)
    }

    @Test
    fun `when navigating back with no room, location is not changed & returns false`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        val result = navigationModel.navigateBack()
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(London, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(false, result)
    }

    @Test
    fun `when navigating from the home location with addHomeLocationToHistory=false, the home location is not added to history`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory,
            initialAddHomeLocationToHistory = false,
        )

        // act
        navigationModel.navigateTo(Paris)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(Paris, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `when navigating forward with addToHistory set to false, location is not added to history`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris, addToHistory = false)
        navigationModel.navigateTo(NewYork, addToHistory = false)
        navigationModel.navigateTo(Tokyo)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(Tokyo, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `given previous location was added with addToHistory = false, when navigating back, addToHistory is set back to true`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(NewYork, addToHistory = false)
        navigationModel.navigateBack()
        navigationModel.navigateTo(Tokyo)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(3, navigationModel.state.backsToExit)
        assertEquals(Tokyo, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(Paris, navigationModel.state.navigation._isBackStack().stack[1].currentLocation())
    }

    @Test
    fun `given previous location was added with addToHistory = false, when navigating back to x, addToHistory is set back to true`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(NewYork, addToHistory = false)
        navigationModel.navigateBackTo(Paris)
        navigationModel.navigateTo(Tokyo)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(3, navigationModel.state.backsToExit)
        assertEquals(Tokyo, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(Paris, navigationModel.state.navigation._isBackStack().stack[1].currentLocation())
    }

    @Test
    fun `given previous location was added with addToHistory = false, when navigating back to the same location, addToHistory is set back to true`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(Sydney(), addToHistory = false)
        navigationModel.navigateBackTo(Sydney(50))
        navigationModel.navigateTo(Tokyo)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(Tokyo, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(Sydney(50), navigationModel.state.navigation._isBackStack().stack[2].currentLocation())
    }

    @Test
    fun `given previous location was added with addToHistory = false, when rewriting path, addToHistory is set back to true`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Tokyo, addToHistory = false)
        navigationModel.reWriteNavigation(
            navigation = backStackOf(
                endNodeOf(Paris),
                endNodeOf(NewYork),
            ),
        )

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(NewYork, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(true, navigationModel.state.willBeAddedToHistory)
    }

    @Test
    fun `when navigateBack() is called, back stack is cleared`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Paris)
        navigationModel.navigateBack()
        navigationModel.navigateBack()
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(NewYork, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `when navigateBack() is called with times=3, back stack is cleared three times`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateBack(times = 3)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(NewYork, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `given the backstack is only 3 items long, when navigateBack is called with times=5, back stack is cleared to home item`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateBack(times = 5)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(London, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `given location is visited twice, both entries are added to navigation back stack`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Paris)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(5, navigationModel.state.backsToExit)
        assertEquals(Paris, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(Tokyo, navigationModel.state.navigation._isBackStack().stack[2].currentLocation())
        assertEquals(Tokyo, navigationModel.state.navigation._isBackStack().stack[3].currentLocation())
    }

    @Test
    fun `given location has been previously visited, when navigating back to it, back stack is cleared forward from that point`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Paris)
        navigationModel.navigateBackTo(NewYork)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(NewYork, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `given location has been visited twice before, when navigating back to it, the most recent entry becomes the current page`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Paris)
        navigationModel.navigateBackTo(NewYork)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(NewYork, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(Tokyo, navigationModel.state.navigation._isBackStack().stack[2].currentLocation())
    }

    @Test
    fun `given location is the Paris addToHistory = true, when navigating back to Paris with addToHist = false, history status is updated`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Paris)
        navigationModel.navigateBackTo(Paris, addToHistory = false)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(Paris, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(false, navigationModel.state.willBeAddedToHistory)
    }

    @Test
    fun `given current location is the Paris addToHistory = false, when navigating back to Paris, location not found`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Paris, addToHistory = false)
        navigationModel.navigateBackTo(Paris)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(Paris, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(true, navigationModel.state.willBeAddedToHistory)
    }

    @Test
    fun `given location has NOT been previously visited, when navigating BACK to it, navigate forward to the location as normal`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateBackTo(Paris)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(Paris, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `given location is the current page, when navigating back to it, new location is swapped for current location`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Sydney())
        navigationModel.navigateBackTo(Sydney(50))
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertNotEquals(Sydney(), navigationModel.state.currentLocation)
        assertEquals(Sydney(50), navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `when navigateBackTo is called with the same location as homepage, all other locations are cleared and the homepage is swapped`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = Sydney(),
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(Paris)
        navigationModel.navigateBackTo(Sydney(50))
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertNotEquals(Sydney(), navigationModel.state.currentLocation)
        assertEquals(Sydney(50), navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `when rewriting back stack, back stack is replaced`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.reWriteNavigation(
            navigation = backStackOf(
                endNodeOf(Paris),
                endNodeOf(London),
            )
        )
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(London, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(true, navigationModel.state.willBeAddedToHistory)
    }

    @Test
    fun `when rewriting back stack with addToHistory = false, back stack is replaced`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.reWriteNavigation(
            navigation = backStackOf(
                endNodeOf(Paris),
            ),
            addToHistory = false
        )
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(Paris, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(false, navigationModel.state.willBeAddedToHistory)
    }

    @Test
    fun `when navigateBack is called with setData, final location receives data`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Sydney())
        navigationModel.navigateTo(SunCreamSelector)
        navigationModel.navigateBack(
            setData = {
                when (it) {
                    is Sydney -> {
                        it.copy(withSunCreamFactor = 50)
                    }

                    else -> it
                }
            }
        )
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertNotEquals(Sydney(), navigationModel.state.currentLocation)
        assertEquals(Sydney(50), navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
    }

    @Test
    fun `when navigateBack is called with times = 2 and setData, final location receives data`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Sydney())
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(SunCreamSelector)
        val success = navigationModel.navigateBack(
            times = 2,
            setData = {
                when (it) {
                    is Sydney -> {
                        it.copy(withSunCreamFactor = 50)
                    }

                    else -> it
                }
            }
        )
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(3, navigationModel.state.backsToExit)
        assertNotEquals(Sydney(), navigationModel.state.currentLocation)
        assertEquals(Sydney(50), navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(true, success)
    }

    @Test
    fun `when navigateBack is called with times = 3 and setData, but only room for 2 backs, final location receives data`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = Sydney(),
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Sydney())
        navigationModel.navigateTo(SunCreamSelector)
        val success = navigationModel.navigateBack(
            times = 3,
            setData = {
                when (it) {
                    is Sydney -> {
                        it.copy(withSunCreamFactor = 50)
                    }

                    else -> it
                }
            }
        )
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertNotEquals(Sydney(), navigationModel.state.currentLocation)
        assertEquals(Sydney(50), navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(false, success)
    }

    @Test
    fun `given we are already on the home location, when navigateBack is called, false is returned`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        val result = navigationModel.navigateBack()
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, result)
    }

    @Test
    fun `given we are not on the home location, when navigateBack is called, true is returned`() {

        // arrange
        val navigationModel = NavigationModel<Location, Unit>(
            homeLocation = London,
            stateKType = typeOf<NavigationState<Location, Unit>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Tokyo)
        val result = navigationModel.navigateBack()
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(true, result)
    }
}
