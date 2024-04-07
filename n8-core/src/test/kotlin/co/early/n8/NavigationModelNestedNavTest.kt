package co.early.n8

import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.n8.NestedExample.Location
import co.early.n8.NestedExample.Location.A
import co.early.n8.NestedExample.Location.B
import co.early.n8.NestedExample.Location.C
import co.early.n8.NestedExample.Location.D
import co.early.n8.NestedExample.Location.E
import co.early.n8.NestedExample.Location.F
import co.early.n8.NestedExample.Location.Home
import co.early.n8.NestedExample.Location.X1
import co.early.n8.NestedExample.Location.X2
import co.early.n8.NestedExample.Location.X3
import co.early.n8.NestedExample.Location.Y1
import co.early.n8.NestedExample.Location.Y2
import co.early.n8.NestedExample.Location.Y3
import co.early.n8.NestedExample.Location.Z1
import co.early.n8.NestedExample.TabHost
import io.mockk.MockKAnnotations
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.reflect.typeOf

class NavigationModelNestedNavTest {

    private lateinit var dataDirectory: File

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        Fore.setDelegate(TestDelegateDefault())

        val dataFolder = TemporaryFolder()
        dataFolder.create()
        dataDirectory = dataFolder.newFolder()
    }

    @Ignore
    @Test
    fun `when navigating straight to a TabHost from the home location with addHomeLocationToHistory=false, the home location is not added to history`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory,
            addHomeLocationToHistory = false,
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(B, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when navigating to a new TabHost, the TabHost is setup correctly`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(B, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when navigating within a tab, backstack is maintained correctly`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)
        navigationModel.navigateTo(location = X1)
        navigationModel.navigateTo(location = X2)
        navigationModel.navigateTo(location = X3)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(5, navigationModel.state.backsToExit)
        assertEquals(X3, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when navigating within a tab, then navigating back, backstack is maintained correctly`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)
        navigationModel.navigateTo(location = X1)
        navigationModel.navigateTo(location = X2)
        navigationModel.navigateTo(location = X3)
        navigationModel.navigateBack()

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(X2, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when navigating back out of a tab, backstack is maintained correctly`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)
        navigationModel.navigateTo(location = X1)
        navigationModel.navigateTo(location = X2)
        navigationModel.navigateTo(location = X3)
        navigationModel.navigateBack()
        navigationModel.navigateBack()
        navigationModel.navigateBack()
        val backedOk = navigationModel.navigateBack()

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(Home, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(0, navigationModel.state.hostedBy.size)
        assertEquals(true, backedOk)
    }

    @Test
    fun `when navigating within a tab, then switching tabs, backstack is maintained correctly`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)
        navigationModel.navigateTo(location = X1)
        navigationModel.navigateTo(location = X2)
        navigationModel.navigateTo(location = X3)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 0)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(6, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when navigating within a tab, switching tabs, then navigating back, backstack is maintained correctly`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)
        navigationModel.navigateTo(location = X1)
        navigationModel.navigateTo(location = X2)
        navigationModel.navigateTo(location = X3)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 0)
        val backedOk = navigationModel.navigateBack()

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(5, navigationModel.state.backsToExit)
        assertEquals(X3, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
        assertEquals(true, backedOk)
    }

    @Test
    fun `when navigating within a structural tab, then switching tabs, backstack is maintained correctly`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbcStructural, tabIndex = 1)
        navigationModel.navigateTo(location = X1)
        navigationModel.navigateTo(location = X2)
        navigationModel.navigateTo(location = X3)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbcStructural, tabIndex = 0)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(
            1, navigationModel.state.navigation.currentItem()
                .parent?.parent?.isTabHost()?.selectedTabHistory?.size
        )
        assertEquals(
            0, navigationModel.state.navigation.currentItem()
                .parent?.parent?.isTabHost()?.selectedTabHistory?.get(0)
        )
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(
            tabHostSpecAbcStructural.tabHostId,
            navigationModel.state.hostedBy[0].tabHostId
        )
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when navigating within a structural tab, switching tabs, then navigating back, backstack is maintained correctly`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbcStructural, tabIndex = 1)
        navigationModel.navigateTo(location = X1)
        navigationModel.navigateTo(location = X2)
        navigationModel.navigateTo(location = X3)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbcStructural, tabIndex = 0)
        navigationModel.navigateBack()

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(Home, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(0, navigationModel.state.hostedBy.size)
    }

    @Test
    fun `when returning to previous tab with clearToTabRoot = false, backstack is maintained correctly`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)
        navigationModel.navigateTo(location = X1)
        navigationModel.navigateTo(location = X2)
        navigationModel.navigateTo(location = X3)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 0)
        navigationModel.switchTab(
            tabHostSpec = tabHostSpecAbc,
            tabIndex = 1,
            clearToTabRoot = false
        )

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(6, navigationModel.state.backsToExit)
        assertEquals(X3, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when returning to previous tab with clearToTabRoot = true, the previous tabs history is maintained`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)
        navigationModel.navigateTo(location = X1)
        navigationModel.navigateTo(location = X2)
        navigationModel.navigateTo(location = X3)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 0)
        navigationModel.switchTab(
            tabHostSpec = tabHostSpecAbc,
            tabIndex = 1,
            clearToTabRoot = true
        )

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(3, navigationModel.state.backsToExit)
        assertEquals(B, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when repeatedly switching tabs, tab visit history is stored only once for each tab`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 0)
        navigationModel.navigateTo(location = X1)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)
        navigationModel.navigateTo(location = X2)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 2)
        navigationModel.navigateTo(location = X3)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 0)
        navigationModel.navigateTo(location = Y1)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)
        navigationModel.navigateTo(location = Y2)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 2)
        navigationModel.navigateTo(location = Y3)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(10, navigationModel.state.backsToExit)
        assertEquals(Y2, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
        assertEquals(
            listOf(0, 2, 1), navigationModel.state.navigation.currentItem().parent
                ?.isBackStack()?.parent?.isTabHost()!!.selectedTabHistory
        )
    }

    @Test
    fun `when navigating to a different tab with a tabIndex which is too high, fails`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        var exception: Exception? = null

        // act
        try {
            navigationModel.switchTab(
                tabHostSpec = tabHostSpecAbc,
                tabIndex = tabHostSpecAbc.homeTabLocations.size
            )
        } catch (e: Exception) {
            Fore.e(e.message ?: "exception with no message")
            exception = e
        }

        // assert
        Assert.assertNotEquals(null, exception)
    }

    @Test
    fun `when navigating to a different tab with a tabIndex which is too low, fails`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        var exception: Exception? = null

        // act
        try {
            navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = -1)
        } catch (e: Exception) {
            Fore.e(e.message ?: "exception with no message")
            exception = e
        }

        // assert
        Assert.assertNotEquals(null, exception)
    }

    @Test
    fun `given a tabbed initial navigation, creating an additional nested tab succeeds`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = tabsOf(
                selectedTabHistory = listOf(0, 1),
                tabHostId = TabHost.TabAbc,
                backStackOf(
                    endNodeOf(A)
                ),
                backStackOf(
                    endNodeOf(B)
                ),
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecX12, tabIndex = 1)
        navigationModel.switchTab(tabHostSpec = tabHostSpecX12, tabIndex = 0)
        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(X1, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(2, navigationModel.state.hostedBy.size)
        assertEquals(TabHost.TabAbc, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
        assertEquals(tabHostSpecX12.tabHostId, navigationModel.state.hostedBy[1].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[1].tabIndex)
    }

    @Test
    fun `given a nested tab, navigating back out of the inner tab succeeds`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = tabsOf(
                selectedTabHistory = listOf(0, 1),
                tabHostId = TabHost.TabAbc,
                backStackOf(
                    endNodeOf(A)
                ),
                backStackOf(
                    endNodeOf(B)
                ),
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.switchTab(tabHostSpec = tabHostSpecX12, tabIndex = 1)
        navigationModel.switchTab(tabHostSpec = tabHostSpecX12, tabIndex = 0)
        navigationModel.navigateTo(location = B)

        Fore.i(navigationModel.toString(diagnostics = true))

        // act
        navigationModel.navigateBack()
        navigationModel.navigateBack()
        navigationModel.navigateBack()

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(B, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(TabHost.TabAbc, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `given top level nav is BackStack, when navigating forward by breaking to top level, navigation history is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = A,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 0)

        // act
        navigationModel.navigateTo(location = X1) { null }

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(3, navigationModel.state.backsToExit)
        assertEquals(X1, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(0, navigationModel.state.hostedBy.size)
    }

    @Test
    fun `given top level nav is TabHost, when navigating forward by breaking to top level, navigation history is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = tabsOf(
                selectedTabHistory = listOf(0, 1),
                tabHostId = TabHost.TabAbc,
                backStackOf(
                    endNodeOf(A)
                ),
                backStackOf(
                    endNodeOf(B)
                ),
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.switchTab(tabHostSpec = tabHostSpecXyz, tabIndex = 0)

        // act
        navigationModel.navigateTo(location = X1) { null }

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(X1, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(1, navigationModel.state.hostedBy.size)
    }

    @Test
    fun `when navigating forward by breaking to an outer tabHost, navigation history is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = A,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbcStructural, tabIndex = 0)
        navigationModel.navigateTo(location = D)
        navigationModel.navigateTo(location = E)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbcStructural, tabIndex = 1)
        navigationModel.switchTab(tabHostSpec = tabHostSpecXyz, tabIndex = 1)
        navigationModel.navigateTo(location = Y2)
        navigationModel.navigateTo(location = Y3)

        // act
        navigationModel.navigateTo(location = F) { tabHostSpecAbcStructural.tabHostId }

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(6, navigationModel.state.backsToExit)
        assertEquals(F, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbcStructural.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
    }

    @Test
    fun `when navigating forward by breaking to an outer tabHost which is not found, continues in current TabHost`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = A,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 0)
        navigationModel.navigateTo(location = D)
        navigationModel.navigateTo(location = E)
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)
        navigationModel.switchTab(tabHostSpec = tabHostSpecXyz, tabIndex = 1)
        navigationModel.navigateTo(location = Y2)
        navigationModel.navigateTo(location = Y3)

        // act
        navigationModel.navigateTo(location = F) { tabHostSpecX12.tabHostId }

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(9, navigationModel.state.backsToExit)
        assertEquals(F, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(2, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(tabHostSpecXyz.tabHostId, navigationModel.state.hostedBy[1].tabHostId)
    }

    @Ignore
    @Test
    fun `using backTo from within a tab to outside of tab (what about to parallel tabHost)`() {
//here
//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = NewYork,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateToTab(tabHostSpec = europeTabs, tabIndex = 1)
//        navigationModel.navigateTo(NewYork)
//        navigationModel.navigateTo(Tokyo)
//
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, navigationModel.state.loading)
//        assertEquals(2, navigationModel.state.backsToExit)
//        assertEquals(Paris, navigationModel.state.currentLocation)
//        assertEquals(true, navigationModel.state.canNavigateBack)
//        assertEquals(1, navigationModel.state.hostedBy.size)
//        assertEquals("EUROPEAN_LOCATIONS", navigationModel.state.hostedBy[0].tabHostId)
//        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
        assert(false)
    }

    @Ignore
    @Test
    fun `given location has been visited twice before, when navigating back to it from inside a tab to outside?, the most recent entry becomes the current page`() {

//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = London,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateTo(NewYork)
//        navigationModel.navigateTo(Tokyo)
//        navigationModel.navigateTo(NewYork)
//        navigationModel.navigateTo(Paris)
//        navigationModel.navigateBackTo(NewYork)
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, navigationModel.state.loading)
//        assertEquals(4, navigationModel.state.backsToExit)
//        assertEquals(NewYork, navigationModel.state.currentLocation)
//        assertEquals(true, navigationModel.state.canNavigateBack)
//        assertEquals(Tokyo, navigationModel.state.navigation.stack[2].currentLocation())
        assert(false)
    }

    @Ignore
    @Test
    fun `given previous location was added with addToHistory = false, when navigating back to the same location, addToHistory is set back to true`() {

//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = London,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateTo(Paris)
//        navigationModel.navigateTo(Sydney(), addToHistory = false)
//        navigationModel.navigateBackTo(Sydney(50))
//        navigationModel.navigateTo(Tokyo)
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, navigationModel.state.loading)
//        assertEquals(4, navigationModel.state.backsToExit)
//        assertEquals(Tokyo, navigationModel.state.currentLocation)
//        assertEquals(true, navigationModel.state.canNavigateBack)
//        assertEquals(Sydney(50), navigationModel.state.navigation.stack[2].currentLocation())
        assert(false)
    }

    @Ignore
    @Test
    fun `given previous location was added with addToHistory = false, when rewriting path, addToHistory is set back to true`() {

//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = London,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateTo(Tokyo, addToHistory = false)
//        navigationModel.reWriteNavigation(
//            navigation = backStackOf(
//                endNodeOf(Paris),
//                endNodeOf(NewYork),
//            ),
//        )
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, navigationModel.state.loading)
//        assertEquals(2, navigationModel.state.backsToExit)
//        assertEquals(NewYork, navigationModel.state.currentLocation)
//        assertEquals(true, navigationModel.state.canNavigateBack)
//        assertEquals(true, navigationModel.state.willBeAddedToHistory)
        assert(false)
    }

    @Test
    fun `given previous location was added with addToHistory = false, when rewriting navigation state with nested graph, addToHistory is set back to true`() {

        // arrange
        val navigationModel = NavigationModel<Location, String>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, String>>(),
            dataDirectory = dataDirectory
        )
        val nav = backStackOf(
            endNodeOf(A),
            endNodeOf(B),
            tabsOf(
                selectedTabHistory = listOf(0),
                tabHostId = "TABS_01",
                backStackOf(
                    endNodeOf(X1),
                    endNodeOf(C),
                    endNodeOf(D),
                    tabsOf(
                        selectedTabHistory = listOf(0, 1),
                        tabHostId = "TABS_02",
                        backStackOf(
                            endNodeOf(Y1),
                            endNodeOf(E)
                        ),
                        backStackOf(
                            endNodeOf(Y2)
                        )
                    )
                ),
                backStackOf(
                    endNodeOf(X1)
                ),
                backStackOf(
                    endNodeOf(X2)
                )
            )
        )

        // act
        navigationModel.navigateTo(Z1, addToHistory = false)
        navigationModel.reWriteNavigation(navigation = nav)

        Fore.e(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(8, navigationModel.state.backsToExit)
        assertEquals(Y2, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(true, navigationModel.state.willBeAddedToHistory)
    }

    @Test
    fun `when rewriting navigation state with nested graph, state is replaced correctly`() {

        // arrange
        val navigationModel = NavigationModel<Location, String>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, String>>(),
            dataDirectory = dataDirectory
        )
        val nav = backStackOf(
            endNodeOf(A),
            endNodeOf(B),
            tabsOf(
                selectedTabHistory = listOf(0),
                tabHostId = "TABS_01",
                backStackOf(
                    endNodeOf(X1),
                    endNodeOf(C),
                    endNodeOf(D),
                    tabsOf(
                        selectedTabHistory = listOf(0, 1),
                        tabHostId = "TABS_02",
                        backStackOf(
                            endNodeOf(Y1),
                            endNodeOf(E)
                        ),
                        backStackOf(
                            endNodeOf(Y2)
                        )
                    )
                ),
                backStackOf(
                    endNodeOf(X1)
                ),
                backStackOf(
                    endNodeOf(X2)
                )
            )
        )

        // act
        navigationModel.navigateTo(Z1, addToHistory = false)
        navigationModel.reWriteNavigation(navigation = nav)

        Fore.e(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(8, navigationModel.state.backsToExit)
        assertEquals(Y2, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(true, navigationModel.state.willBeAddedToHistory)
    }


    @Test
    fun `when rewriting navigation graph with a history including tabHosts with willBeAddedToHistory=false, state is replaced correctly`() {
        // arrange
        val navigationModel = NavigationModel<Location, String>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, String>>(),
            dataDirectory = dataDirectory
        )
        val nav = backStackOf(
            endNodeOf(A),
            endNodeOf(B),
            tabsOf(
                selectedTabHistory = listOf(0),
                tabHostId = "TABS_01",
                backStackOf(
                    endNodeOf(X1),
                    endNodeOf(C),
                    endNodeOf(D),
                    tabsOf(
                        selectedTabHistory = listOf(0, 1),
                        tabHostId = "TABS_02",
                        backStackOf(
                            endNodeOf(Y1),
                            endNodeOf(E)
                        ),
                        backStackOf(
                            endNodeOf(Y2)
                        )
                    )
                ),
                backStackOf(
                    endNodeOf(X1)
                ),
                backStackOf(
                    endNodeOf(X2)
                )
            )
        )


        // act
        navigationModel.reWriteNavigation(navigation = nav, addToHistory = false)
        navigationModel.navigateTo(Z1)

        Fore.e(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.loading)
        assertEquals(8, navigationModel.state.backsToExit)
        assertEquals(Z1, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(true, navigationModel.state.willBeAddedToHistory)
    }

    @Ignore
    @Test
    fun `some custom navigation operations`() {
//
//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = London,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateTo(NewYork)
//        navigationModel.navigateTo(Tokyo)
//        navigationModel.navigateTo(Paris)
//        navigationModel.navigateBack()
//        navigationModel.navigateBack()
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, navigationModel.state.loading)
//        assertEquals(2, navigationModel.state.backsToExit)
//        assertEquals(NewYork, navigationModel.state.currentLocation)
//        assertEquals(true, navigationModel.state.canNavigateBack)
        assert(false)
    }

    @Ignore
    @Test
    fun `when navigateBack() is called with times=3, back stack is cleared three times`() {

//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = London,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateTo(NewYork)
//        navigationModel.navigateTo(Tokyo)
//        navigationModel.navigateTo(Paris)
//        navigationModel.navigateTo(Tokyo)
//        navigationModel.navigateBack(times = 3)
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, navigationModel.state.loading)
//        assertEquals(2, navigationModel.state.backsToExit)
//        assertEquals(NewYork, navigationModel.state.currentLocation)
//        assertEquals(true, navigationModel.state.canNavigateBack)
        assert(false)
    }

    @Ignore
    @Test
    fun `given location is the current page, when navigating back to it, history status is updated`() {
//
//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = London,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateTo(NewYork)
//        navigationModel.navigateTo(Tokyo)
//        navigationModel.navigateTo(Paris)
//        navigationModel.navigateBackTo(Paris, addToHistory = false)
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, navigationModel.state.loading)
//        assertEquals(4, navigationModel.state.backsToExit)
//        assertEquals(Paris, navigationModel.state.currentLocation)
//        assertEquals(true, navigationModel.state.canNavigateBack)
//        assertEquals(
//            false,
//            navigationModel.state.willBeAddedToHistory
//        )
        assert(false)
    }

    @Ignore
    @Test
    fun `given location has NOT been previously visited, when navigating BACK to it, navigate to the location as normal`() {

//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = London,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateTo(NewYork)
//        navigationModel.navigateTo(Tokyo)
//        navigationModel.navigateBackTo(Paris)
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, navigationModel.state.loading)
//        assertEquals(4, navigationModel.state.backsToExit)
//        assertEquals(Paris, navigationModel.state.currentLocation)
//        assertEquals(true, navigationModel.state.canNavigateBack)
        assert(false)
    }

    @Ignore
    @Test
    fun `given location is the current page, when navigating back to it, new location is swapped for current location`() {

//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = London,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateTo(NewYork)
//        navigationModel.navigateTo(Tokyo)
//        navigationModel.navigateTo(Sydney())
//        navigationModel.navigateBackTo(Sydney(50))
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, navigationModel.state.loading)
//        assertEquals(4, navigationModel.state.backsToExit)
//        assertNotEquals(Sydney(), navigationModel.state.currentLocation)
//        assertEquals(Sydney(50), navigationModel.state.currentLocation)
//        assertEquals(true, navigationModel.state.canNavigateBack)
        assert(false)
    }

    @Ignore
    @Test
    fun `when navigateBackTo is called with the same location as homepage, all other locations are cleared and the homepage is swapped`() {

//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = Sydney(),
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateTo(NewYork)
//        navigationModel.navigateTo(Tokyo)
//        navigationModel.navigateTo(Paris)
//        navigationModel.navigateBackTo(Sydney(50))
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, navigationModel.state.loading)
//        assertEquals(1, navigationModel.state.backsToExit)
//        assertNotEquals(Sydney(), navigationModel.state.currentLocation)
//        assertEquals(Sydney(50), navigationModel.state.currentLocation)
//        assertEquals(false, navigationModel.state.canNavigateBack)
        assert(false)
    }

    @Ignore
    @Test
    fun `when navigateBack is called with setData, final location receives data`() {

//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = London,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateTo(NewYork)
//        navigationModel.navigateTo(Tokyo)
//        navigationModel.navigateTo(Sydney())
//        navigationModel.navigateTo(SunCreamSelector)
//        navigationModel.navigateBack(
//            setData = {
//                when (it) {
//                    is Sydney -> {
//                        it.copy(withSunCreamFactor = 50)
//                    }
//
//                    else -> it
//                }
//            }
//        )
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, navigationModel.state.loading)
//        assertEquals(4, navigationModel.state.backsToExit)
//        assertNotEquals(Sydney(), navigationModel.state.currentLocation)
//        assertEquals(Sydney(50), navigationModel.state.currentLocation)
//        assertEquals(true, navigationModel.state.canNavigateBack)
        assert(false)
    }

    @Ignore
    @Test
    fun `when navigateBack is called with times = 2 and setData, final location receives data`() {

//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = London,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateTo(NewYork)
//        navigationModel.navigateTo(Sydney())
//        navigationModel.navigateTo(Tokyo)
//        navigationModel.navigateTo(SunCreamSelector)
//        navigationModel.navigateBack(
//            times = 2,
//            setData = {
//                when (it) {
//                    is Sydney -> {
//                        it.copy(withSunCreamFactor = 50)
//                    }
//
//                    else -> it
//                }
//            }
//        )
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, navigationModel.state.loading)
//        assertEquals(3, navigationModel.state.backsToExit)
//        assertNotEquals(Sydney(), navigationModel.state.currentLocation)
//        assertEquals(Sydney(50), navigationModel.state.currentLocation)
//        assertEquals(true, navigationModel.state.canNavigateBack)
        assert(false)
    }

    @Ignore
    @Test
    fun `given we are already on the home location, when navigateBack is called, false is returned`() {
//
//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = London,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        val result = navigationModel.navigateBack()
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(false, result)
        assert(false)
    }

    @Ignore
    @Test
    fun `given we are not on the home location, when navigateBack is called, true is returned`() {

//        // arrange
//        val navigationModel = NavigationModel<Location>(
//            homeLocation = London,
//            stateKType = typeOf<NavigationState<Location>>(),
//            dataDirectory = dataDirectory
//        )
//
//        // act
//        navigationModel.navigateTo(Tokyo)
//        val result = navigationModel.navigateBack()
//        Fore.i(navigationModel.toString(diagnostics = true))
//
//        // assert
//        assertEquals(true, result)
        assert(false)
    }
}
