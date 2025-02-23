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
import co.early.n8.NestedTestData.Location.X1
import co.early.n8.NestedTestData.Location.X2
import co.early.n8.NestedTestData.Location.Y1
import co.early.n8.NestedTestData.Location.Y2
import co.early.n8.NestedTestData.Location.Z2
import co.early.n8.lowlevel.LowLevelApi
import co.early.n8.lowlevel._createItemNavigatedBackCopy
import co.early.n8.lowlevel._mutateNavigation
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class NavigationExtensionsTest {

    @Before
    fun setup() {
        Fore.setDelegate(TestDelegateDefault())
    }

    @Test
    fun `when logging nav graph with diagnostics, output is correct`() {

        // arrange
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

        Fore.i(nav.toString(true))

        // assert
        assertEquals(
            "\n" +
                    "backStackOf( [stackSize=3 parent=null child=TabHost(TABS_01 tabs:3 hist:[0])]\n" +
                    "    endNodeOf(A) [parent=BackStack(3) child=null],\n" +
                    "    endNodeOf(B) [parent=BackStack(3) child=null],\n" +
                    "    tabsOf( [tabs=3 parent=BackStack(3) child=BackStack(4)]\n" +
                    "        tabHistory = listOf(0),\n" +
                    "        tabHostId = TABS_01,\n" +
                    "        backMode = Temporal,\n" +
                    "        clearToTabRoot = false,\n" +
                    "        backStackOf( [stackSize=4 parent=TabHost(TABS_01 tabs:3 hist:[0]) child=TabHost(TABS_02 tabs:2 hist:[0, 1])]\n" +
                    "            endNodeOf(X1) [parent=BackStack(4) child=null],\n" +
                    "            endNodeOf(C) [parent=BackStack(4) child=null],\n" +
                    "            endNodeOf(D) [parent=BackStack(4) child=null],\n" +
                    "            tabsOf( [tabs=2 parent=BackStack(4) child=BackStack(1)]\n" +
                    "                tabHistory = listOf(0,1),\n" +
                    "                tabHostId = TABS_02,\n" +
                    "                backMode = Temporal,\n" +
                    "                clearToTabRoot = false,\n" +
                    "                backStackOf( [stackSize=2 parent=TabHost(TABS_02 tabs:2 hist:[0, 1]) child=EndNode[E]]\n" +
                    "                    endNodeOf(Y1) [parent=BackStack(2) child=null],\n" +
                    "                    endNodeOf(E) [parent=BackStack(2) child=null]\n" +
                    "                ),\n" +
                    "                backStackOf( [stackSize=1 parent=TabHost(TABS_02 tabs:2 hist:[0, 1]) child=EndNode[Y2]]\n" +
                    "                    endNodeOf(Y2) [parent=BackStack(1) child=null]     <--- Current Item\n" +
                    "                )\n" +
                    "            )\n" +
                    "        ),\n" +
                    "        backStackOf( [stackSize=1 parent=TabHost(TABS_01 tabs:3 hist:[0]) child=EndNode[X1]]\n" +
                    "            endNodeOf(X1) [parent=BackStack(1) child=null]\n" +
                    "        ),\n" +
                    "        backStackOf( [stackSize=1 parent=TabHost(TABS_01 tabs:3 hist:[0]) child=EndNode[X2]]\n" +
                    "            endNodeOf(X2) [parent=BackStack(1) child=null]\n" +
                    "        )\n" +
                    "    )\n" +
                    ")",
            nav.toString(true)
        )
    }

    @Test
    fun `when logging nav graph without diagnostics, output is correct`() {

        // arrange
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

        Fore.i(nav.toString(false))

        // assert
        assertEquals(
            "\n" +
                    "backStackOf( \n" +
                    "    endNodeOf(A),\n" +
                    "    endNodeOf(B),\n" +
                    "    tabsOf( \n" +
                    "        tabHistory = listOf(0),\n" +
                    "        tabHostId = TABS_01,\n" +
                    "        backStackOf( \n" +
                    "            endNodeOf(X1),\n" +
                    "            endNodeOf(C),\n" +
                    "            endNodeOf(D),\n" +
                    "            tabsOf( \n" +
                    "                tabHistory = listOf(0,1),\n" +
                    "                tabHostId = TABS_02,\n" +
                    "                backStackOf( \n" +
                    "                    endNodeOf(Y1),\n" +
                    "                    endNodeOf(E)\n" +
                    "                ),\n" +
                    "                backStackOf( \n" +
                    "                    endNodeOf(Y2) <---\n" +
                    "                )\n" +
                    "            )\n" +
                    "        ),\n" +
                    "        backStackOf( \n" +
                    "            endNodeOf(X1)\n" +
                    "        ),\n" +
                    "        backStackOf( \n" +
                    "            endNodeOf(X2)\n" +
                    "        )\n" +
                    "    )\n" +
                    ")",
            nav.toString(false)
        )
    }

    @Test
    fun `given a BackStack, createdBackCopy is correct`() {

        // arrange
        val nav = backStackNoTabsOf(
            endNodeOf(A),
            endNodeOf(B),
            endNodeOf(C),
        )

        Fore.i(nav.toString(true))

        // act
        val result = nav.currentItem().parent?._createItemNavigatedBackCopy()!!

        Fore.i(result.toString(true))

        // assert
        assertEquals(
            backStackNoTabsOf(
                endNodeOf(A),
                endNodeOf(B),
            ),
            result
        )
    }

    @Test
    fun `given a TabHost, createdBackCopy is correct`() {

        // arrange
        val nav = tabsOf(
            tabHistory = listOf(0, 1),
            tabHostId = "TAB_01",
            backStackOf(
                endNodeOf(A),
            ),
            backStackOf(
                endNodeOf(B),
            ),
        )

        Fore.i(nav.toString(true))

        // act
        val result = nav.currentItem().parent?.parent?._createItemNavigatedBackCopy()!!

        Fore.i(result.toString(true))

        // assert
        assertEquals(
            tabsOf(
                tabHistory = listOf(0),
                tabHostId = "TAB_01",
                backStackOf(
                    endNodeOf(A),
                ),
                backStackOf(
                    endNodeOf(B),
                ),
            ),
            result
        )
    }

    @Test
    fun `when removing item from a top level backStack, mutation completes successfully`() {

        // arrange
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

        Fore.i(nav.toString(true))

        // act
        val result = _mutateNavigation(
            oldItem = nav,
            newItem = nav.copy(
                stack = nav.stack.toMutableList().also { it.removeAt(1) }
            )
        )

        Fore.i(result.toString(true))

        // assert
        assertEquals(2, result.isBackStack().stack.size)
        assertEquals(2, result.isBackStack().stack[0].parent?.isBackStack()?.stack?.size)
        assertEquals(2, result.isBackStack().stack[1].parent?.isBackStack()?.stack?.size)
    }

    @Test
    fun `when removing an item from a backStack hosted in a TabHost, mutation completes successfully`() {

        // arrange
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

        Fore.i(nav.toString(true))

        // act
        val tabHost = nav.isBackStack().stack[2].isTabHost()
        val tabs = tabHost.tabs
        val tab = tabs[0]
        val newTab = tab.copy(stack = tab.stack.toMutableList().also { it.removeAt(1) })
        val newTabs = tabs.mapIndexed { index, backStack ->
            if (index == 0) {
                newTab
            } else backStack
        }

        val result = _mutateNavigation(
            oldItem = tabHost,
            newItem = tabHost.copy(
                tabs = newTabs
            )
        )

        Fore.i(result.toString(true))

        // assert
        assertEquals(3, result.isBackStack().stack[2].isTabHost().tabs[0].stack.size)
        assertEquals(
            3,
            result.isBackStack().stack[2].isTabHost().tabs[0].stack[0].parent?.isBackStack()?.stack?.size
        )
        assertEquals(
            3,
            result.isBackStack().stack[2].isTabHost().tabs[0].stack[2].parent?.isBackStack()?.stack?.size
        )
        assertEquals(
            X1,
            result.isBackStack().stack[2].isTabHost().tabs[0].stack[0].isEndNode().location
        )
        assertEquals(
            D,
            result.isBackStack().stack[2].isTabHost().tabs[0].stack[1].isEndNode().location
        )
    }

    @Test
    fun `when removing a tab from a nested TabHost, mutation completes successfully`() {

        // arrange
        val nav = tabsOf(
            tabHistory = listOf(0),
            tabHostId = "TABS_01",
            backStackOf(
                endNodeOf(X1),
                endNodeOf(C),
                endNodeOf(D),
                tabsOf(
                    tabHistory = listOf(0, 1, 2),
                    tabHostId = "TABS_02",
                    backStackOf(
                        endNodeOf(Y1),
                        endNodeOf(E)
                    ),
                    backStackOf(
                        endNodeOf(Y2)
                    ),
                    backStackOf(
                        endNodeOf(Z2)
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

        Fore.i(nav.toString(true))

        // act
        val tabHost = nav.tabs[0].stack[3].isTabHost()
        val result = _mutateNavigation(
            oldItem = tabHost,
            newItem = tabHost.copy(
                tabHistory = tabHost.tabHistory.toMutableList()
                    .also { it.removeLast() },
                tabs = tabHost.tabs.toMutableList().also { it.removeLast() }
            )
        )

        Fore.i(result.toString(true))

        // assert
        assertEquals(2, result.isTabHost().tabs[0].isBackStack().stack[3].isTabHost().tabs.size)
        assertEquals(
            Y2,
            result.isTabHost().tabs[0].isBackStack().stack[3].isTabHost().tabs[1].stack[0].isEndNode().location
        )
        assertEquals(Y2, result.currentLocation())
    }

    @Test
    fun `given an EndNode with a parent BackStack that contains multiple identical EndNodes, when mutating to swap EndNode, correct item is swapped`() {

        // arrange
        val nav = backStackOf<Location, Unit>(
            endNodeOf(A),
            endNodeOf(A),
            endNodeOf(A),
        )
        Fore.i(nav.toString(diagnostics = true))

        // act
        val mutatedNav = _mutateNavigation(
            oldItem = nav.stack[1],
            newItem = endNodeOf(B)
        )
        Fore.i(mutatedNav.toString(diagnostics = true))

        // assert
        assertEquals(A, mutatedNav.isBackStack().stack[0].isEndNode().location)
        assertEquals(B, mutatedNav.isBackStack().stack[1].isEndNode().location)
        assertEquals(A, mutatedNav.isBackStack().stack[2].isEndNode().location)
    }

    @Test
    fun `given an EndNode with a parent BackStack that contains multiple identical EndNodes, when mutating to swap EndNode with ensureOnHistoryPath=true, correct item is swapped`() {

        // arrange
        val nav = backStackOf<Location, Unit>(
            endNodeOf(A),
            endNodeOf(A),
            endNodeOf(A),
        )
        Fore.i(nav.toString(diagnostics = true))

        // act
        val mutatedNav = _mutateNavigation(
            oldItem = nav.stack[1],
            newItem = endNodeOf(B),
            ensureOnHistoryPath = true,
        )
        Fore.i(mutatedNav.toString(diagnostics = true))

        // assert
        assertEquals(A, mutatedNav.isBackStack().stack[0].isEndNode().location)
        assertEquals(B, mutatedNav.isBackStack().stack[1].isEndNode().location)
        assertEquals(2, mutatedNav.isBackStack().stack.size)
    }

    @Test
    fun `given a BackStack with a parent TabHost that contains multiple identical BackStacks, when mutating to swap BackStack, correct item is swapped`() {

        // arrange
        val nav = tabsOf(
            listOf(0),
            "TestTab",
            backStackOf(
                Navigation.EndNode(A),
            ),
            backStackOf(
                Navigation.EndNode(A),
            ),
            backStackOf(
                Navigation.EndNode(A),
            ),
            backStackOf(
                Navigation.EndNode(C),
            ),
        )
        val replacementBackStack = backStackOf<Location, String>(
            Navigation.EndNode(B),
        )
        Fore.i(nav.toString(diagnostics = true))

        // act
        val mutatedNav = _mutateNavigation(
            oldItem = nav.tabs[1],
            newItem = replacementBackStack,
        )
        Fore.i(mutatedNav.toString(diagnostics = true))

        // assert
        assertEquals(A, mutatedNav.isTabHost().tabs[0].isBackStack().stack[0].isEndNode().location)
        assertEquals(B, mutatedNav.isTabHost().tabs[1].isBackStack().stack[0].isEndNode().location)
        assertEquals(A, mutatedNav.isTabHost().tabs[2].isBackStack().stack[0].isEndNode().location)
        assertEquals(C, mutatedNav.isTabHost().tabs[3].isBackStack().stack[0].isEndNode().location)
        assertEquals(A, mutatedNav.currentLocation())
    }

    @Test
    fun `given a BackStack with a parent TabHost that contains multiple identical BackStacks, when mutating to swap BackStack with ensureOnHistoryPath = true, correct item is swapped`() {

        // arrange
        val nav = tabsOf(
            listOf(0),
            "TestTab",
            backStackOf(
                Navigation.EndNode(A),
            ),
            backStackOf(
                Navigation.EndNode(A),
            ),
            backStackOf(
                Navigation.EndNode(A),
            ),
            backStackOf(
                Navigation.EndNode(C),
            ),
        )
        val replacementBackStack = backStackOf<Location, String>(
            Navigation.EndNode(B),
        )
        Fore.i(nav.toString(diagnostics = true))

        // act
        val mutatedNav = _mutateNavigation(
            oldItem = nav.tabs[1],
            newItem = replacementBackStack,
            ensureOnHistoryPath = true,
        )
        Fore.i(mutatedNav.toString(diagnostics = true))

        // assert
        assertEquals(A, mutatedNav.isTabHost().tabs[0].isBackStack().stack[0].isEndNode().location)
        assertEquals(B, mutatedNav.isTabHost().tabs[1].isBackStack().stack[0].isEndNode().location)
        assertEquals(A, mutatedNav.isTabHost().tabs[2].isBackStack().stack[0].isEndNode().location)
        assertEquals(C, mutatedNav.isTabHost().tabs[3].isBackStack().stack[0].isEndNode().location)
        assertEquals(B, mutatedNav.currentLocation())
    }

    @Test
    fun `when hosted inside multiple tabHosts, showSelected is correct`() {

        // arrange
        val nav = tabsOf(
            tabHistory = listOf(1),
            tabHostId = "TABS_01",
            backStackOf(
                endNodeOf(A)
            ),
            backStackOf(
                endNodeOf(E),
                tabsOf(
                    tabHistory = listOf(0, 1, 2),
                    tabHostId = "TABS_02",
                    backStackOf(
                        endNodeOf(Y1),
                        endNodeOf(E)
                    ),
                    backStackOf(
                        endNodeOf(Y2)
                    ),
                    backStackOf(
                        endNodeOf(Z2)
                    )
                )
            ),
            backStackOf(
                endNodeOf(C)
            ),
            backStackOf(
                endNodeOf(D)
            )
        )

        // act
        val hostedBy = nav.currentItem().hostedBy()

        // assert
        assertEquals(false, hostedBy.isIndexOnPath(0, "TABS_01")) // not on path
        assertEquals(true, hostedBy.isIndexOnPath(1, "TABS_01")) // on path
        assertEquals(false, hostedBy.isIndexOnPath(2, "TABS_01")) // not on path
        assertEquals(false, hostedBy.isIndexOnPath(0, "TABS_0X")) // tabHost not present
        assertEquals(false, hostedBy.isIndexOnPath(0, "TABS_02")) // not on path
        assertEquals(true, hostedBy.isIndexOnPath(2, "TABS_02")) // on path
        assertEquals(false, hostedBy.isIndexOnPath(9, "TABS_02")) // out of bounds
    }

    @Ignore
    @Test
    fun `some custom navigation operations`() {
        // TODO these tests come from NavigationModelLinearNavTest,

        //  also look at listener interface where the client can get the change to change the mutations (can use for logging stats or actual mutations)


        // we just need to check the same thing when the navigation graph is a nested one
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
}
