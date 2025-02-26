@file:OptIn(LowLevelApi::class)

package co.early.n8

import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.n8.NestedTestData.Location
import co.early.n8.NestedTestData.Location.A
import co.early.n8.NestedTestData.Location.B
import co.early.n8.NestedTestData.Location.C
import co.early.n8.NestedTestData.Location.D
import co.early.n8.NestedTestData.Location.E
import co.early.n8.NestedTestData.Location.F
import co.early.n8.NestedTestData.Location.Home
import co.early.n8.NestedTestData.Location.X1
import co.early.n8.NestedTestData.Location.X2
import co.early.n8.NestedTestData.Location.X3
import co.early.n8.NestedTestData.Location.Y1
import co.early.n8.NestedTestData.Location.Y2
import co.early.n8.NestedTestData.Location.Y3
import co.early.n8.NestedTestData.Location.Z1
import co.early.n8.NestedTestData.Location.Z3
import co.early.n8.NestedTestData.TabHost
import co.early.n8.lowlevel.LowLevelApi
import co.early.n8.lowlevel._isBackStack
import co.early.n8.lowlevel._isTabHost
import io.mockk.MockKAnnotations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
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

    @Test
    fun `when navigating straight to a TabHost from the home location with addHomeLocationToHistory=false, the home location is not added to history`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory,
            initialAddHomeLocationToHistory = false,
        )

        // act
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 1)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(B, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(Home, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
        assertEquals("B", navigationModel.state.breadcrumbs.joinToString(">"))
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

        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(B, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(Home, navigationModel.state.comingFrom)
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
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(5, navigationModel.state.backsToExit)
        assertEquals(X3, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X2, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
        assertEquals("Home>B>X1>X2>X3", navigationModel.state.breadcrumbs.joinToString(">"))
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
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(X2, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X3, navigationModel.state.comingFrom)
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
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(Home, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(B, navigationModel.state.comingFrom)
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
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(6, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X3, navigationModel.state.comingFrom)
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
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(5, navigationModel.state.backsToExit)
        assertEquals(X3, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(A, navigationModel.state.comingFrom)
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
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X3, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.navigation.currentItem().parent?.parent?._isTabHost()?.tabHistory?.size)
        assertEquals(0, navigationModel.state.navigation.currentItem().parent?.parent?._isTabHost()?.tabHistory?.get(0))
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbcStructural.tabHostId,navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
        assertEquals("Home>A", navigationModel.state.breadcrumbs.joinToString(">"))
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

        Fore.i(navigationModel.toString(diagnostics = true))

        navigationModel.switchTab(tabHostSpec = tabHostSpecAbcStructural, tabIndex = 0)

        Fore.i(navigationModel.toString(diagnostics = true))

        navigationModel.navigateBack()

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(Home, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(A, navigationModel.state.comingFrom)
        assertEquals(0, navigationModel.state.hostedBy.size)
    }

    @Test
    fun `when switching to an implicit tab from outside a tabHost, no mutation occurs`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = backStackOf(
                tabsOf(tabHostSpecAbc)
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(D) { null }

        // act
        val success = navigationModel.switchTab(tabIndex = 1)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, success)
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(D, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(A, navigationModel.state.comingFrom)
        assertEquals(0, navigationModel.state.hostedBy.size)
    }

    @Test
    fun `when switching to a tab but not specifying tabHost or tabIndex, exception thrown`() {

        // arrange
        var exceptionMessage : String? = null
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = backStackOf(
                tabsOf(tabHostSpecAbc)
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(D) { null }

        // act
        try {
            navigationModel.switchTab()
        }catch (e: Exception){
            exceptionMessage = e.message
        }

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertNotNull(exceptionMessage)
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(D, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(A, navigationModel.state.comingFrom)
        assertEquals(0, navigationModel.state.hostedBy.size)
    }

    @Test
    fun `when switching to a tab which doesn't already exist and not specifying a tabIndex, default tab index is used`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = backStackOf(
                tabsOf(
                    tabHistory = listOf(0),
                    tabHostId = tabHostSpecAbc.tabHostId,
                    backStackOf(
                        endNodeOf(A),
                        endNodeOf(B),
                        endNodeOf(B),
                    ),
                    backStackOf(
                        endNodeOf(C)
                    ),
                    backStackOf(
                        endNodeOf(D)
                    ),
                )
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpecX123)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(X2, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(B, navigationModel.state.comingFrom)
        assertEquals(2, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(tabHostSpecX123.tabHostId, navigationModel.state.hostedBy[1].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
        assertEquals(1, navigationModel.state.hostedBy[1].tabIndex)
    }

    @Test
    fun `when switching to a tab which doesn't already exist and specifying a tabIndex, specified tab index is used`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = backStackOf(
                tabsOf(
                    tabHistory = listOf(0),
                    tabHostId = tabHostSpecAbc.tabHostId,
                    backStackOf(
                        endNodeOf(A),
                        endNodeOf(B),
                        endNodeOf(B),
                    ),
                    backStackOf(
                        endNodeOf(C)
                    ),
                    backStackOf(
                        endNodeOf(D)
                    ),
                )
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpecX123, tabIndex = 0)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(X1, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(B, navigationModel.state.comingFrom)
        assertEquals(2, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(tabHostSpecX123.tabHostId, navigationModel.state.hostedBy[1].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
        assertEquals(0, navigationModel.state.hostedBy[1].tabIndex)
    }

    @Test
    fun `when switching to a tab which does already exist and not specifying a tabIndex, tab index is left unchanged`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = backStackOf(
                tabsOf(
                    tabHistory = listOf(0),
                    tabHostId = tabHostSpecAbc.tabHostId,
                    backStackOf(
                        endNodeOf(A),
                        endNodeOf(B),
                        endNodeOf(B),
                        tabsOf(
                            tabHistory = listOf(1,0,2),
                            tabHostId = tabHostSpecX123.tabHostId,
                            backStackOf(
                                endNodeOf(X1)
                            ),
                            backStackOf(
                                endNodeOf(X2)
                            ),
                            backStackOf(
                                endNodeOf(X3)
                            ),
                        )
                    ),
                    backStackOf(
                        endNodeOf(C)
                    ),
                    backStackOf(
                        endNodeOf(D)
                    ),
                ),
                endNodeOf(A),
                endNodeOf(E),
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.switchTab(tabHostSpecX123)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(6, navigationModel.state.backsToExit)
        assertEquals(X3, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(E, navigationModel.state.comingFrom)
        assertEquals(2, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(tabHostSpecX123.tabHostId, navigationModel.state.hostedBy[1].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
        assertEquals(2, navigationModel.state.hostedBy[1].tabIndex)
    }

    @Test
    fun `when switching to a Temporal TabHost which does already exist and specifying a tabIndex, mutation is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = backStackOf(
                tabsOf(tabHostSpecAbc),
                endNodeOf(D),
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(E) { null }
        navigationModel.switchTab(tabHostSpecAbc, tabIndex = 2)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(C, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(E, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(2, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when switching to a Structural TabHost which does already exist and specifying a tabIndex, mutation is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = backStackOf(
                tabsOf(tabHostSpecAbcStructural),
                endNodeOf(D),
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateTo(E) { null }
        navigationModel.switchTab(tabHostSpecAbcStructural, tabIndex = 2)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(C, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(E, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbcStructural.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(2, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when switching within a Temporal TabHost and only specifying a tabIndex, mutation is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = backStackOf(
                tabsOf(tabHostSpecAbc)
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        val success = navigationModel.switchTab(tabIndex = 1)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(true, success)
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(B, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(A, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when switching within a nested Temporal TabHost and only specifying a tabIndex, mutation is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = backStackOf(
                tabsOf(
                    tabHistory = listOf(0),
                    tabHostId = tabHostSpecAbc.tabHostId,
                    backStackOf(
                        endNodeOf(A),
                        endNodeOf(B),
                        tabsOf(
                            tabHistory = listOf(0, 1, 2),
                            tabHostId = tabHostSpecXyz.tabHostId,
                            backStackOf(
                                endNodeOf(X1),
                            ),
                            backStackOf(
                                endNodeOf(X2)
                            ),
                            backStackOf(
                                endNodeOf(X3)
                            ),
                        )
                    ),
                    backStackOf(
                        endNodeOf(C)
                    ),
                    backStackOf(
                        endNodeOf(D)
                    ),
                )
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        val success = navigationModel.switchTab(tabIndex = 1)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(true, success)
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(5, navigationModel.state.backsToExit)
        assertEquals(X2, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X3, navigationModel.state.comingFrom)
        assertEquals(2, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(tabHostSpecXyz.tabHostId, navigationModel.state.hostedBy[1].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
        assertEquals(1, navigationModel.state.hostedBy[1].tabIndex)
        assertEquals("A>B>X1>X3>X2", navigationModel.state.breadcrumbs.joinToString(">"))
    }

    @Test
    fun `when switching within a Structural TabHost and only specifying a tabIndex, mutation is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = tabsOf(tabHostSpecAbcStructural),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        val success = navigationModel.switchTab(tabIndex = 2)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(true, success)
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(C, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(A, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbcStructural.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(2, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when switching within a TabHost with a clearToRoot flag and specifying the tabHostSpec and tabIndex, mutation is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = tabsOf(tabHostSpecAbcClear),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(B)
        navigationModel.navigateTo(C)

        // act
        val success = navigationModel.switchTab(tabHostSpecAbcClear, tabIndex = 0)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(true, success)
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(C, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbcClear.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when switching within a TabHost with a clearToRoot flag and only specifying a tabIndex, mutation is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = tabsOf(tabHostSpecAbcClear),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(D)
        navigationModel.navigateTo(E)
        navigationModel.switchTab(tabHostSpecAbcClear, tabIndex = 1)

        // act
        val success = navigationModel.switchTab(tabIndex = 0)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(true, success)
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(B, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbcClear.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when switching within a TabHost but overriding a clearToRoot flag to false and specifying a tabIndex, mutation is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = tabsOf(tabHostSpecAbcClear),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(D)
        navigationModel.navigateTo(E)
        navigationModel.switchTab(tabHostSpecAbcClear, tabIndex = 1)
        navigationModel.navigateTo(F)

        // act
        val success = navigationModel.switchTab(tabIndex = 0, clearToTabRootOverride = false)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(true, success)
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(5, navigationModel.state.backsToExit)
        assertEquals(E, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(F, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbcClear.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `when switching within a TabHost but overriding a clearToRoot flag to true and specifying a tabIndex, mutation is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = tabsOf(tabHostSpecAbc),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(D)
        navigationModel.navigateTo(E)
        navigationModel.switchTab(tabHostSpecAbc, tabIndex = 1)

        // act
        val success = navigationModel.switchTab(tabIndex = 0, clearToTabRootOverride = true)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(true, success)
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(B, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbcClear.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
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
            clearToTabRootOverride = false
        )

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(6, navigationModel.state.backsToExit)
        assertEquals(X3, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(A, navigationModel.state.comingFrom)
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
            clearToTabRootOverride = true
        )

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(3, navigationModel.state.backsToExit)
        assertEquals(B, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(A, navigationModel.state.comingFrom)
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
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(10, navigationModel.state.backsToExit)
        assertEquals(Y2, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(Y3, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
        assertEquals(
            listOf(0, 2, 1), navigationModel.state.navigation.currentItem().parent
                ?._isBackStack()?.parent?._isTabHost()!!.tabHistory
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
        assertNotEquals(null, exception)
        assertEquals(null, navigationModel.state.comingFrom)
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
        assertNotEquals(null, exception)
        assertEquals(null, navigationModel.state.comingFrom)
    }

    @Test
    fun `given a tabbed initial navigation, creating an additional nested tab succeeds`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = tabsOf(
                tabHistory = listOf(0, 1),
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
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(X1, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X2, navigationModel.state.comingFrom)
        assertEquals(2, navigationModel.state.hostedBy.size)
        assertEquals(TabHost.TabAbc, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
        assertEquals(tabHostSpecX12.tabHostId, navigationModel.state.hostedBy[1].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[1].tabIndex)
        assertEquals("A>B>X2>X1", navigationModel.state.breadcrumbs.joinToString(">"))
    }

    @Test
    fun `given a nested tab, navigating back out of the inner tab succeeds`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = tabsOf(
                tabHistory = listOf(0, 1),
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
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(B, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X2, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(TabHost.TabAbc, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `given top level nav is BackStack, when navigating forward by breaking to top level, navigation history is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.switchTab(tabHostSpec = tabHostSpecAbc, tabIndex = 0)

        // act
        navigationModel.navigateTo(location = X1) { null }

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(3, navigationModel.state.backsToExit)
        assertEquals(X1, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(A, navigationModel.state.comingFrom)
        assertEquals(0, navigationModel.state.hostedBy.size)
    }

    @Test
    fun `given top level nav is TabHost, when navigating forward by breaking to top level, navigation history is correct`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = tabsOf(
                tabHistory = listOf(0, 1),
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
        navigationModel.navigateTo(location = X2) { null }

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(X2, navigationModel.state.currentLocation)
        assertEquals(X1, navigationModel.state.comingFrom)
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
        navigationModel.navigateTo(location = F) { tabHostSpecAbcStructural }

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(6, navigationModel.state.backsToExit)
        assertEquals(F, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(Y3, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(
            tabHostSpecAbcStructural.tabHostId,
            navigationModel.state.hostedBy[0].tabHostId
        )
    }

    @Test
    fun `when navigating forward by breaking to an outer tabHost which is not found, creates that TabHost in place`() {

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

        Fore.i(navigationModel.toString(diagnostics = true))

        // act
        navigationModel.navigateTo(location = F) { tabHostSpecX12 }

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(10, navigationModel.state.backsToExit)
        assertEquals(F, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(Y3, navigationModel.state.comingFrom)
        assertEquals(3, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(tabHostSpecXyz.tabHostId, navigationModel.state.hostedBy[1].tabHostId)
        assertEquals(tabHostSpecX12.tabHostId, navigationModel.state.hostedBy[2].tabHostId)
    }

    @Test
    fun `given target exists on back path inside tab, backTo target navigates to target`() {
        // arrange
        val initialTabs = tabsOf<Location, TabHost>(
            tabHistory = listOf(0),
            tabHostId = TabHost.TabAbc,
            backStackOf(
                endNodeOf(A)
            ),
            backStackOf(
                endNodeOf(B)
            ),
        )
        val navigationModel = NavigationModel(
            initialNavigation = initialTabs,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(D)
        navigationModel.navigateTo(E)
        navigationModel.navigateTo(F)

        // act
        navigationModel.navigateBackTo(A)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(F, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(TabHost.TabAbc, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `given target exists in another tab index, and is on back path, backTo target navigates to target`() {
        // arrange
        val initialTabs = tabsOf<Location, TabHost>(
            tabHistory = listOf(0, 1),
            tabHostId = TabHost.TabAbc,
            backStackOf(
                endNodeOf(A)
            ),
            backStackOf(
                endNodeOf(B)
            ),
        )
        val navigationModel = NavigationModel(
            initialNavigation = initialTabs,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(D)
        navigationModel.navigateTo(E)
        navigationModel.navigateTo(F)

        // act
        navigationModel.navigateBackTo(A)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(F, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(TabHost.TabAbc, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `given target exists in another tab, but is not on back path, using backTo target behaves as if target is not present`() {
        // arrange
        val initialTabs = tabsOf<Location, TabHost>(
            tabHistory = listOf(0),
            tabHostId = TabHost.TabAbc,
            backStackOf(
                endNodeOf(A)
            ),
            backStackOf(
                endNodeOf(B)
            ),
        )
        val navigationModel = NavigationModel(
            initialNavigation = initialTabs,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(D)
        navigationModel.navigateTo(E)
        navigationModel.navigateTo(F)

        // act
        navigationModel.navigateBackTo(B)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(5, navigationModel.state.backsToExit)
        assertEquals(B, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(F, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(TabHost.TabAbc, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `given target exists outside of tab, but still on back path, backTo target from within tab navigates to target`() {
        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        navigationModel.navigateTo(A)
        navigationModel.navigateTo(B)
        navigationModel.switchTab(tabHostSpec = tabHostSpecXyz, tabIndex = 1)
        navigationModel.navigateTo(C)
        navigationModel.navigateTo(D)

        // act
        navigationModel.navigateBackTo(A)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(D, navigationModel.state.comingFrom)
        assertEquals(0, navigationModel.state.hostedBy.size)
    }

    @Test
    fun `given current location is at ROOT of tabHost, and backTo target does not exist, target is navigated to in current tab index`() {
        // arrange
        val initialTabs = tabsOf<Location, TabHost>(
            tabHistory = listOf(0, 1),
            tabHostId = TabHost.TabAbc,
            backStackOf(
                endNodeOf(A)
            ),
            backStackOf(
                endNodeOf(B)
            ),
        )
        val navigationModel = NavigationModel(
            initialNavigation = initialTabs,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateBackTo(C)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(3, navigationModel.state.backsToExit)
        assertEquals(C, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(B, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(TabHost.TabAbc, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `given target has been visited twice before, when navigating back to it from within a tab, the most recent target is chosen`() {
        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        navigationModel.navigateTo(A)
        navigationModel.navigateTo(B)
        navigationModel.switchTab(tabHostSpec = tabHostSpecXyz, tabIndex = 1)
        navigationModel.navigateTo(C)
        navigationModel.navigateTo(A)
        navigationModel.navigateTo(D)

        // act
        navigationModel.navigateBackTo(A)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(6, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(D, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
    }

    @Test
    fun `given previous location was added with addToHistory = false, when navigating back to the same location, location is not present`() {
        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        navigationModel.navigateTo(A)
        navigationModel.navigateTo(B)
        navigationModel.switchTab(tabHostSpec = tabHostSpecXyz, tabIndex = 1)
        navigationModel.navigateTo(C)
        navigationModel.navigateTo(D)

        // act
        navigationModel.navigateTo(A, addToHistory = false)

        Fore.i(navigationModel.toString(diagnostics = true))

        navigationModel.navigateBackTo(A)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(true, navigationModel.state.willBeAddedToHistory)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(A, navigationModel.state.comingFrom)
        assertEquals(0, navigationModel.state.hostedBy.size)
    }

    @Test
    fun `given previous location was added with addToHistory = false, when navigating back, addToHistory is set back to true`() {
        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        navigationModel.navigateTo(A)
        navigationModel.navigateTo(B)
        navigationModel.switchTab(tabHostSpec = tabHostSpecXyz, tabIndex = 1)
        navigationModel.navigateTo(C)
        navigationModel.navigateTo(D)

        // act
        navigationModel.navigateTo(A, addToHistory = false)
        navigationModel.navigateBack()

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(6, navigationModel.state.backsToExit)
        assertEquals(true, navigationModel.state.willBeAddedToHistory)
        assertEquals(D, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(A, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
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
                tabHistory = listOf(0),
                tabHostId = "TABS_01",
                backStackOf(
                    endNodeOf(X1),
                    endNodeOf(C),
                    endNodeOf(D),
                    tabsOf(
                        tabHistory = listOf(0, 1),
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
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(8, navigationModel.state.backsToExit)
        assertEquals(Y2, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(Z1, navigationModel.state.comingFrom)
        assertEquals(true, navigationModel.state.willBeAddedToHistory)
        assertEquals("A>B>X1>C>D>Y1>E>Y2", navigationModel.state.breadcrumbs.joinToString(">"))
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
                tabHistory = listOf(0),
                tabHostId = "TABS_01",
                backStackOf(
                    endNodeOf(X1),
                    endNodeOf(C),
                    endNodeOf(D),
                    tabsOf(
                        tabHistory = listOf(0, 1),
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
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(8, navigationModel.state.backsToExit)
        assertEquals(Y2, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(Z1, navigationModel.state.comingFrom)
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
                tabHistory = listOf(0),
                tabHostId = "TABS_01",
                backStackOf(
                    endNodeOf(X1),
                    endNodeOf(C),
                    endNodeOf(D),
                    tabsOf(
                        tabHistory = listOf(0, 1),
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
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(8, navigationModel.state.backsToExit)
        assertEquals(Z1, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(Y2, navigationModel.state.comingFrom)
        assertEquals(true, navigationModel.state.willBeAddedToHistory)
    }


    @Test
    fun `when navigating to a previous TabHost in history, from outside that TabHost, navigates correctly`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = backStackOf(
                tabsOf(
                    tabHistory = listOf(1, 0, 3),
                    tabHostId = tabHostSpecAbc.tabHostId,
                    backStackOf(
                        endNodeOf(A)
                    ),
                    backStackOf(
                        endNodeOf(B)
                    ),
                    backStackOf(
                        endNodeOf(C)
                    ),
                    backStackOf(
                        endNodeOf(D)
                    )
                )
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateBack()
        navigationModel.navigateTo(E) // continue in same tabHost
        navigationModel.navigateTo(A) { null } // break out to top level
        navigationModel.navigateTo(B)

        Fore.i(navigationModel.toString(diagnostics = true))

        navigationModel.navigateTo(D) { tabHostSpecAbc } // break back to previous tabHost

        Fore.i(navigationModel.toString())

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(4, navigationModel.state.backsToExit)
        assertEquals(D, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(B, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
    }


    @Test
    fun `when navigating to a previous TabHost outside of history, from outside that TabHost, navigates and sets current`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = backStackOf(
                tabsOf(
                    tabHistory = listOf(0, 1, 2),
                    tabHostId = tabHostSpecAbc.tabHostId,
                    backStackOf(
                        endNodeOf(A)
                    ),
                    backStackOf(
                        endNodeOf(B)
                    ),
                    backStackOf(
                        tabsOf(
                            tabHistory = listOf(0),
                            tabHostId = tabHostSpecXyz.tabHostId,
                            backStackOf(
                                endNodeOf(C)
                            ),
                            backStackOf(
                                endNodeOf(D)
                            )
                        )
                    )
                )
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateBack() //so TabXyz is not longer on history path
        navigationModel.navigateTo(E)
        navigationModel.navigateTo(A) { null } // break out to top level
        navigationModel.navigateTo(B)

        Fore.i(navigationModel.toString(diagnostics = true))

        navigationModel.navigateTo(F) { tabHostSpecXyz } // break back to previous tabHost (but it doesn't exist in the backStack)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(5, navigationModel.state.backsToExit)
        assertEquals(F, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(B, navigationModel.state.comingFrom)
        assertEquals(2, navigationModel.state.hostedBy.size)
    }


    @Test
    fun `given target exists on back path inside tab, back times=3 navigates to target`() {
        // arrange
        val initialTabs = tabsOf<Location, TabHost>(
            tabHistory = listOf(0),
            tabHostId = TabHost.TabAbc,
            backStackOf(
                endNodeOf(A)
            ),
            backStackOf(
                endNodeOf(B)
            ),
        )
        val navigationModel = NavigationModel(
            initialNavigation = initialTabs,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(D)
        navigationModel.navigateTo(E)
        navigationModel.navigateTo(F)

        // act
        navigationModel.navigateBack(times = 3)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(F, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(TabHost.TabAbc, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(0, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `given target exists in another tab index, and is on back path, back times=3 navigates to target`() {
        // arrange
        val initialTabs = tabsOf<Location, TabHost>(
            tabHistory = listOf(0, 1),
            tabHostId = TabHost.TabAbc,
            backStackOf(
                endNodeOf(A)
            ),
            backStackOf(
                endNodeOf(B)
            ),
        )
        val navigationModel = NavigationModel(
            initialNavigation = initialTabs,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(D)
        navigationModel.navigateTo(E)
        navigationModel.navigateTo(F)

        // act
        navigationModel.navigateBack(times = 3)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(B, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(F, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(TabHost.TabAbc, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals(1, navigationModel.state.hostedBy[0].tabIndex)
    }

    @Test
    fun `given target exists outside of tab, but still on back path, back times=4 from within tab navigates to target`() {
        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = Home,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        navigationModel.navigateTo(A)
        navigationModel.navigateTo(B)
        navigationModel.switchTab(tabHostSpec = tabHostSpecXyz, tabIndex = 1)
        navigationModel.navigateTo(C)
        navigationModel.navigateTo(D)

        // act
        navigationModel.navigateBack(times = 4)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(2, navigationModel.state.backsToExit)
        assertEquals(A, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(D, navigationModel.state.comingFrom)
        assertEquals(0, navigationModel.state.hostedBy.size)
    }

    @Test
    fun `when navigateBack times=2 is called with setData on a nested graph, final location receives data`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            initialNavigation = backStackOf(
                tabsOf(
                    tabHistory = listOf(0, 1, 2),
                    tabHostId = tabHostSpecAbc.tabHostId,
                    backStackOf(
                        endNodeOf(Z3(999))
                    ),
                    backStackOf(
                        endNodeOf(B)
                    ),
                    backStackOf(
                        tabsOf(
                            tabHistory = listOf(0),
                            tabHostId = tabHostSpecXyz.tabHostId,
                            backStackOf(
                                endNodeOf(C)
                            ),
                            backStackOf(
                                endNodeOf(D)
                            )
                        )
                    )
                )
            ),
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )

        // act
        navigationModel.navigateBack(
            times = 2
        ) {
            when (it) {
                is Z3 -> {
                    it.copy(id = 123)
                }

                else -> it
            }
        }

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(1, navigationModel.state.backsToExit)
        assertNotEquals(Z3(999), navigationModel.state.currentLocation)
        assertEquals(Z3(123), navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.canNavigateBack)
        assertEquals(C, navigationModel.state.comingFrom)
    }

    @Test
    fun `when navigating back to tabHost from outside of tabHost, mutation is correct`() {

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
        navigationModel.navigateTo(location = F)
        navigationModel.navigateTo(location = X1) { null }

        // act
        navigationModel.navigateBackTo(tabHostSpec = tabHostSpecAbc)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(6, navigationModel.state.backsToExit)
        assertEquals(F, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X1, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
    }

    @Test
    fun `when navigating back to tabHost we are already in, mutation occurs to the same place`() {

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
        navigationModel.navigateTo(location = F)
        navigationModel.navigateTo(location = X2)
        navigationModel.navigateTo(location = X3)

        // act
        navigationModel.navigateBackTo(tabHostSpec = tabHostSpecAbc)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(8, navigationModel.state.backsToExit)
        assertEquals(X3, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X3, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals( tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
    }

    @Test
    fun `when navigating back to tabHost, from within nested tabHost, graph is updated with new data`() {

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
        navigationModel.navigateTo(location = F)
        navigationModel.navigateTo(location = X2) { tabHostSpecXyz }
        navigationModel.navigateTo(location = X3)

        // act
        navigationModel.navigateBackTo(tabHostSpec = tabHostSpecAbc, addToHistory = false)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(9, navigationModel.state.backsToExit)
        assertEquals(X3, navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.willBeAddedToHistory)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X3, navigationModel.state.comingFrom)
        assertEquals(2, navigationModel.state.hostedBy.size)
        assertEquals( tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals( tabHostSpecXyz.tabHostId, navigationModel.state.hostedBy[1].tabHostId)
    }

    @Test
    fun `when navigating back to tabHost which is not found, creates that TabHost in place`() {

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
        navigationModel.navigateTo(location = F)
        navigationModel.navigateTo(location = X2)
        navigationModel.navigateTo(location = X3)

        // act
        navigationModel.navigateBackTo(tabHostSpec = tabHostSpecXyz)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(9, navigationModel.state.backsToExit)
        assertEquals(X1, navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X3, navigationModel.state.comingFrom)
        assertEquals(2, navigationModel.state.hostedBy.size)
        assertEquals( tabHostSpecAbc.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
        assertEquals( tabHostSpecXyz.tabHostId, navigationModel.state.hostedBy[1].tabHostId)
    }

    @Test
    fun `when passing data back to tabHost - data is sent`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = A,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(location = B)
        navigationModel.switchTab(tabHostSpec = tabHostSpecZ3)
        navigationModel.navigateTo(location = X1) { null }

        // act
        navigationModel.navigateBackTo(tabHostSpec = tabHostSpecZ3) {
            when (it) {
                is Z3 -> {
                    it.copy(id = 123)
                }

                else -> it
            }
        }

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(3, navigationModel.state.backsToExit)
        assertNotEquals(Z3(), navigationModel.state.currentLocation)
        assertEquals(Z3(123), navigationModel.state.currentLocation)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X1, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecZ3.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
    }

    @Test
    fun `when navigating back to TabHost and setting addToHistory=false, flag is set`() {

        // arrange
        val navigationModel = NavigationModel<Location, TabHost>(
            homeLocation = A,
            stateKType = typeOf<NavigationState<Location, TabHost>>(),
            dataDirectory = dataDirectory
        )
        navigationModel.navigateTo(location = B)
        navigationModel.switchTab(tabHostSpec = tabHostSpecZ3)
        navigationModel.navigateTo(location = X1) { null }

        // act
        navigationModel.navigateBackTo(tabHostSpec = tabHostSpecZ3, addToHistory = false)

        Fore.i(navigationModel.toString(diagnostics = true))

        // assert
        assertEquals(false, navigationModel.state.initialLoading)
        assertEquals(3, navigationModel.state.backsToExit)
        assertEquals(Z3(), navigationModel.state.currentLocation)
        assertEquals(false, navigationModel.state.willBeAddedToHistory)
        assertEquals(true, navigationModel.state.canNavigateBack)
        assertEquals(X1, navigationModel.state.comingFrom)
        assertEquals(1, navigationModel.state.hostedBy.size)
        assertEquals(tabHostSpecZ3.tabHostId, navigationModel.state.hostedBy[0].tabHostId)
    }

}
