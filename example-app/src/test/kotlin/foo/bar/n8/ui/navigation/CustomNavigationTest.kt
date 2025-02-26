package foo.bar.n8.ui.navigation

import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.n8.backStackOf
import co.early.n8.endNodeOf
import co.early.n8.tabsOf
import foo.bar.n8.ui.navigation.Location.Bangkok
import foo.bar.n8.ui.navigation.Location.Dakar
import foo.bar.n8.ui.navigation.Location.EuropeanLocations.London
import foo.bar.n8.ui.navigation.Location.Seoul
import foo.bar.n8.ui.navigation.Location.Sydney
import foo.bar.n8.ui.navigation.Location.Tokyo
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomNavigationTest {

    @Before
    fun setup() {
        Fore.setDelegate(TestDelegateDefault())
    }

    @Test
    fun `given a basic BackStack with backsToExit greater than 3, limiting the back path to 3 mutates the navigation correctly`() {

        // arrange
        val nav = backStackOf<Location, TabHostId>(
            endNodeOf(London),
            endNodeOf(Seoul),
            endNodeOf(Bangkok),
            endNodeOf(Tokyo),
            endNodeOf(Dakar),
        )

        val expected = backStackOf<Location, TabHostId>(
            endNodeOf(Bangkok),
            endNodeOf(Tokyo),
            endNodeOf(Dakar),
        )

        // act
        val actual = limitBackPath(3, nav)

        Fore.i(actual.toString(diagnostics = true))

        // assert
        assertEquals(
            expected.toString(diagnostics = true),
            actual.toString(diagnostics = true)
        )
    }

    @Test
    fun `given a basic TabHost with backsToExit greater than 3, limiting the back path to 3 mutates the navigation correctly`() {

        // arrange
        val nav = tabsOf<Location, TabHostId>(
            tabHistory = listOf(0),
            tabHostId = TabHostId.TabsMain,
            backStackOf(
                endNodeOf(London),
                endNodeOf(Bangkok),
                endNodeOf(Tokyo),
                endNodeOf(Dakar),
            )
        )

        val expected = tabsOf<Location, TabHostId>(
            tabHistory = listOf(0),
            tabHostId = TabHostId.TabsMain,
            backStackOf(
                endNodeOf(Bangkok),
                endNodeOf(Tokyo),
                endNodeOf(Dakar),
            )
        )

        // act
        val actual = limitBackPath(3, nav)

        Fore.i(actual.toString(diagnostics = true))

        // assert
        assertEquals(
            expected.toString(diagnostics = true),
            actual.toString(diagnostics = true)
        )
    }

    @Test
    fun `given a twice nested TabHost with backsToExit greater than 3, limiting the back path to 3 mutates the navigation correctly`() {

        // arrange
        val nav = tabsOf(
            tabHistory = listOf(0),
            tabHostId = TabHostId.TabsMain,
            backStackOf(
                tabsOf(
                    tabHistory = listOf(0),
                    tabHostId = TabHostId.TabsAccount,
                    backStackOf(
                        endNodeOf(London),
                        endNodeOf(Bangkok),
                        endNodeOf(Tokyo),
                        endNodeOf(Dakar),
                    )
                )
            )
        )

        val expected = tabsOf(
            tabHistory = listOf(0),
            tabHostId = TabHostId.TabsMain,
            backStackOf(
                tabsOf(
                    tabHistory = listOf(0),
                    tabHostId = TabHostId.TabsAccount,
                    backStackOf(
                        endNodeOf(Bangkok),
                        endNodeOf(Tokyo),
                        endNodeOf(Dakar),
                    )
                )
            )
        )

        // act
        val actual = limitBackPath(3, nav)

        Fore.i(actual.toString(diagnostics = true))

        // assert
        assertEquals(
            expected.toString(diagnostics = true),
            actual.toString(diagnostics = true)
        )
    }

    @Test
    fun `given a complex navgraph that requires switching tabs to resolve with backsToExit greater than 3, limiting the back path to 3 mutates the navigation correctly`() {

        // arrange
        val nav = tabsOf(
            tabHistory = listOf(0),
            tabHostId = TabHostId.TabsMain,
            backStackOf(
                endNodeOf(London),
                tabsOf(
                    tabHistory = listOf(1),
                    tabHostId = TabHostId.TabsAccount,
                    backStackOf(
                        endNodeOf(Bangkok),
                    ),
                    backStackOf(
                        tabsOf(
                            tabHistory = listOf(0, 1),
                            tabHostId = TabHostId.TabsSettings,
                            backStackOf(
                                endNodeOf(Bangkok),
                                endNodeOf(Tokyo),
                            ),
                            backStackOf(
                                endNodeOf(Dakar),
                                endNodeOf(Seoul),
                                endNodeOf(Sydney),
                            ),
                        )
                    )
                )
            ),
            backStackOf(
                endNodeOf(Bangkok),
                endNodeOf(Tokyo),
                endNodeOf(Dakar),
            )
        )

        val expected = tabsOf(
            tabHistory = listOf(0),
            tabHostId = TabHostId.TabsMain,
            backStackOf(
                tabsOf(
                    tabHistory = listOf(1),
                    tabHostId = TabHostId.TabsAccount,
                    backStackOf(
                        endNodeOf(Bangkok),
                    ),
                    backStackOf(
                        tabsOf(
                            tabHistory = listOf(1),
                            tabHostId = TabHostId.TabsSettings,
                            backStackOf(
                                endNodeOf(Tokyo),
                            ),
                            backStackOf(
                                endNodeOf(Dakar),
                                endNodeOf(Seoul),
                                endNodeOf(Sydney),
                            ),
                        )
                    )
                )
            ),
            backStackOf(
                endNodeOf(Bangkok),
                endNodeOf(Tokyo),
                endNodeOf(Dakar),
            )
        )

        // act
        val actual = limitBackPath(3, nav)

        Fore.i(actual.toString(diagnostics = true))

        // assert
        assertEquals(
            expected.toString(diagnostics = true),
            actual.toString(diagnostics = true)
        )
    }
}
