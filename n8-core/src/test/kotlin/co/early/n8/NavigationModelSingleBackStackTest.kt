package co.early.n8

import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.n8.Location.EuropeanLocations.London
import co.early.n8.Location.EuropeanLocations.Paris
import co.early.n8.Location.NewYork
import co.early.n8.Location.SunCreamSelector
import co.early.n8.Location.Sydney
import co.early.n8.Location.Tokyo
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class NavigationModelSingleBackStackTest {

    private lateinit var dataDirectory: File

    @MockK
    private lateinit var mockObserver: Observer

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        Fore.setDelegate(TestDelegateDefault())

        val dataFolder = TemporaryFolder()
        dataFolder.create()
        dataDirectory = dataFolder.newFolder()
    }


    @Test
    fun `when created, back stack has initial location only`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(1, navigationModel.state.backStack.size)
        assertEquals(London, navigationModel.state.currentPage())
    }

    @Test
    fun `when navigating forward, back stack is added to`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(NewYork)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(3, navigationModel.state.backStack.size)
        assertEquals(NewYork, navigationModel.state.currentPage())
    }

    @Test
    fun `when navigating forward with addToHistory set to false, location is not added to history`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris, addToHistory = false)
        navigationModel.navigateTo(NewYork, addToHistory = false)
        navigationModel.navigateTo(Tokyo)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(2, navigationModel.state.backStack.size)
        assertEquals(Tokyo, navigationModel.state.currentPage())
        assertEquals(London, navigationModel.state.backStack[0])
    }

    @Test
    fun `given previous location was added with addToHistory = false, when popping backstack, addToHistory is set back to true`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(NewYork, addToHistory = false)
        navigationModel.popBackStack()
        navigationModel.navigateTo(Tokyo)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(3, navigationModel.state.backStack.size)
        assertEquals(Paris, navigationModel.state.backStack[1])
    }

    @Test
    fun `given previous location was added with addToHistory = false, when navigating back, addToHistory is set back to true`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(NewYork, addToHistory = false)
        navigationModel.navigateBackTo(Paris)
        navigationModel.navigateTo(Tokyo)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(3, navigationModel.state.backStack.size)
        assertEquals(Paris, navigationModel.state.backStack[1])
    }

    @Test
    fun `given previous location was added with addToHistory = false, when navigating back to the same location, addToHistory is set back to true`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(Sydney(), addToHistory = false)
        navigationModel.navigateBackTo(Sydney(50))
        navigationModel.navigateTo(Tokyo)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(4, navigationModel.state.backStack.size)
        assertEquals(Sydney(50), navigationModel.state.backStack[2])
    }

    @Test
    fun `given previous location was added with addToHistory = false, when rewriting backstack, addToHistory is set back to true`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(Tokyo, addToHistory = false)
        navigationModel.updateBackStack(
            listOf(
                NewYork,
                London,
            )
        )
        navigationModel.navigateTo(Paris)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(3, navigationModel.state.backStack.size)
        assertEquals(London, navigationModel.state.backStack[1])
    }

    @Test
    fun `when popBackStack is called, back stack is cleared`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Paris)
        navigationModel.popBackStack()
        navigationModel.popBackStack()
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(2, navigationModel.state.backStack.size)
        assertEquals(NewYork, navigationModel.state.currentPage())
    }

    @Test
    fun `when popBackStack is called with times=3, back stack is cleared three times`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Paris)
        navigationModel.navigateTo(Tokyo)
        navigationModel.popBackStack(times = 3)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(2, navigationModel.state.backStack.size)
        assertEquals(NewYork, navigationModel.state.currentPage())
    }

    @Test
    fun `given the backStack is only 3 items long, when popBackStack is called with times=5, back stack is cleared to home item`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.popBackStack(times = 5)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(1, navigationModel.state.backStack.size)
        assertEquals(London, navigationModel.state.currentPage())
    }

    @Test
    fun `given location is visited twice, both entries are added to backstack`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Paris)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(5, navigationModel.state.backStack.size)
        assertEquals(Tokyo, navigationModel.state.backStack[2])
        assertEquals(Tokyo, navigationModel.state.backStack[3])
    }

    @Test
    fun `given location has been previously visited, when navigating back to it, back stack is cleared forward from that point`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Paris)
        navigationModel.navigateBackTo(NewYork)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(2, navigationModel.state.backStack.size)
        assertEquals(NewYork, navigationModel.state.currentPage())
    }

    @Test
    fun `given location has been visited twice before, when navigating back to it, the most recent entry becomes the current page`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Paris)
        navigationModel.navigateBackTo(NewYork)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(4, navigationModel.state.backStack.size)
        assertEquals(Tokyo, navigationModel.state.backStack[2])
        assertEquals(NewYork, navigationModel.state.currentPage())
    }

    @Test
    fun `given location is the current page, when navigating back to it, history status is updated`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Paris)
        navigationModel.navigateBackTo(Paris, addToHistory = false)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(4, navigationModel.state.backStack.size)
        assertEquals(Paris, navigationModel.state.currentPage())
        assertEquals(
            false,
            navigationModel.state.currentLocationWillBeAddedToHistoryOnNextNavigation
        )
    }

    @Test
    fun `given location has NOT been previously visited, when navigating BACK to it, navigate to the location as normal`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateBackTo(Paris)
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(4, navigationModel.state.backStack.size)
        assertEquals(Paris, navigationModel.state.currentPage())
    }

    @Test
    fun `given location is the current page, when navigating back to it, new location is swapped for current location`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Sydney())
        navigationModel.navigateBackTo(Sydney(50))
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(4, navigationModel.state.backStack.size)
        assertNotEquals(Sydney(), navigationModel.state.currentPage())
        assertEquals(Sydney(50), navigationModel.state.currentPage())
    }

    @Test
    fun `when navigateBackTo is called with the same location as homepage, all other locations are cleared and the homepage is swapped`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = Sydney(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Paris)
        navigationModel.navigateBackTo(Sydney(50))
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(1, navigationModel.state.backStack.size)
        assertNotEquals(Sydney(), navigationModel.state.currentPage())
        assertEquals(Sydney(50), navigationModel.state.currentPage())
    }

    @Test
    fun `when rewriting back stack, with a non empty back stack, back stack is replaced`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.updateBackStack(
            listOf(
                Paris,
                London,
            )
        )
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(2, navigationModel.state.backStack.size)
        assertEquals(London, navigationModel.state.currentPage())
    }

    @Test
    fun `when rewriting back stack, with an empty back stack, exception is thrown`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )
        var exception: Exception? = null

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        try {
            navigationModel.updateBackStack(emptyList())
        } catch (e: Exception) {
            exception = e
        }
        Fore.i(navigationModel.toString())

        // assert
        Assert.assertEquals(IllegalArgumentException::class.java, exception?.javaClass)
    }

    @Test
    fun `when rewriting back stack, with a single entry, home location is replaced`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.updateBackStack(
            listOf(
                Paris,
            )
        )
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(1, navigationModel.state.backStack.size)
        assertEquals(Paris, navigationModel.state.currentPage())
    }

    @Test
    fun `when popBackStack is called with setData, final location receives data`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(Sydney())
        navigationModel.navigateTo(SunCreamSelector)
        navigationModel.popBackStack(
            setData = {
                when (it) {
                    is Sydney -> {
                        it.copy(withSunCreamFactor = 50)
                    }
                    else -> it
                }
            }
        )
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(4, navigationModel.state.backStack.size)
        assertNotEquals(Sydney(), navigationModel.state.currentPage())
        assertEquals(Sydney(50), navigationModel.state.currentPage())
    }

    @Test
    fun `when popBackStack is called with times = 2 and setData, final location receives data`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(NewYork)
        navigationModel.navigateTo(Sydney())
        navigationModel.navigateTo(Tokyo)
        navigationModel.navigateTo(SunCreamSelector)
        navigationModel.popBackStack(
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
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(3, navigationModel.state.backStack.size)
        assertEquals(Sydney(50), navigationModel.state.currentPage())
    }

    @Test
    fun `given we are already on the home location, when popBackStack is called, false is returned`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        val result = navigationModel.popBackStack()
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, result)
    }

    @Test
    fun `given we are not on the home location, when popBackStack is called, true is returned`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Tokyo)
        val result = navigationModel.popBackStack()
        Fore.i(navigationModel.toString())

        // assert
        assertEquals(true, result)
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
    fun `when navigating forward, observers are notified`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )
        navigationModel.addObserver(mockObserver)

        // act
        navigationModel.navigateTo(Tokyo)
        Fore.i(navigationModel.toString())

        // assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    @Test
    fun `when navigating back to a previous location, observers are notified`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Tokyo)
        navigationModel.addObserver(mockObserver) // add observer here
        navigationModel.navigateBackTo(London)
        Fore.i(navigationModel.toString())

        // assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    @Test
    fun `when popping back stack, observers are notified`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(Tokyo)
        navigationModel.addObserver(mockObserver) // add observer here
        navigationModel.popBackStack()
        Fore.i(navigationModel.toString())

        // assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    @Test
    fun `when rewriting back stack, observers are notified`() {

        // arrange
        val navigationModel = NavigationModel<Location>(
            homeLocation = London,
            dataDirectory = dataDirectory
        )
        navigationModel.addObserver(mockObserver)

        // act
        navigationModel.updateBackStack(
            listOf(
                Paris,
                Tokyo,
            )
        )
        Fore.i(navigationModel.toString())

        // assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }
}
