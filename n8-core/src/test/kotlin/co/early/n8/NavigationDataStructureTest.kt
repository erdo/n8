package co.early.n8

import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.n8.TabbedExample.Location.A
import co.early.n8.TabbedExample.Location.B
import co.early.n8.TabbedExample.Location.C
import co.early.n8.TabbedExample.Location.D
import co.early.n8.TabbedExample.Location.E
import co.early.n8.TabbedExample.Location.TabX
import co.early.n8.TabbedExample.Location.TabY
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NavigationDataStructureTest {


    @Before
    fun setup() {
        Fore.setDelegate(TestDelegateDefault())
    }


    @Test
    fun `given parent of currentItem is BackStack, createdBackCopy is correct`() {

        // arrange
        val nav = backStackOf(
            endNodeOf(A),
            endNodeOf(B),
            endNodeOf(C),
        )

        Fore.i(nav.toString(true))

        Fore.i("--------------------------")

        // act
        val backedByOne = nav.currentItem().directParent?.invoke()?.createNavigatedBackCopy()

        Fore.i(backedByOne?.topParent()?.toString(true)?:"x")

        Fore.i("--------------------------")

        val result = mutateNavigation(
            oldItem = nav.currentItem(),
            newItem = endNodeOf(TabX.X2)
        )

        Fore.i(result.toString(true))

        // assert
    }

    @Test
    fun `when logging large nav graph`() {

        val nav = backStackOf(
            endNodeOf(A) ,
            endNodeOf(B) ,
            tabsOf(
                selectedTabHistory = listOf(0),
                backStackOf(
                    endNodeOf(TabX.X1) ,
                    endNodeOf(C) ,
                    endNodeOf(D) ,
                    tabsOf(
                        selectedTabHistory = listOf(0,1),
                        backStackOf(
                            endNodeOf(TabY.Y1) ,
                            endNodeOf(E)
                        ),
                        backStackOf(
                            endNodeOf(TabY.Y2)
                        )
                    )
                ),
                backStackOf(
                    endNodeOf(TabX.X1)
                ),
                backStackOf(
                    endNodeOf(TabX.X2)
                )
            )
        )

        Fore.i(nav.toString(true))

        // assert
        assertTrue(false)
    }
}
