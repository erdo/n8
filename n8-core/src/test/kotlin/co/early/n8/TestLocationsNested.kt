package co.early.n8

import co.early.n8.TabbedExample.Location.*
import kotlinx.serialization.Serializable


class TabbedExample {

    @Serializable
    sealed class Location {

        @Serializable
        data object A : Location()

        @Serializable
        data object B : Location()

        @Serializable
        data object C : Location()

        @Serializable
        data object D : Location()

        @Serializable
        data object E : Location()

        @Serializable
        data object F : Location()

        @Serializable
        sealed class TabX : Location() {
            @Serializable
            data object X1 : TabX()

            @Serializable
            data object X2 : TabX()

            @Serializable
            data object X3 : TabX()
        }

        @Serializable
        sealed class TabY : Location() {
            @Serializable
            data object Y1 : TabY()

            @Serializable
            data object Y2 : TabY()

            @Serializable
            data object Y3 : TabY()
        }

        @Serializable
        sealed class TabZ : Location() {
            @Serializable
            data object Z1 : TabZ()

            @Serializable
            data object Z2 : TabZ()

            @Serializable
            data object Z3 : TabZ()
        }
    }
}

fun test1() {
    val nav = backStackOf (
        endNodeOf(A),
        endNodeOf(B),
        tabsOf(
            selectedTabHistory = listOf(0),
            backStackOf(
                endNodeOf(TabX.X1),
                endNodeOf(C),
                endNodeOf(D),
                tabsOf(
                    selectedTabHistory = listOf(0,1),
                    backStackOf(
                        endNodeOf(TabY.Y1),
                        endNodeOf(E),
                    ),
                    backStackOf(
                        endNodeOf(TabY.Y2),
                    )
                )
            ),
            backStackOf(
                endNodeOf(TabX.X1),
            ),
            backStackOf(
                endNodeOf(TabX.X2),
            ),
        )
    )
}

fun test2() {
    val nav = backStackOf(
        endNodeOf(A),
    )
}

fun test3() {
    val nav = backStackOf(
        tabsOf(
            selectedTabHistory = listOf(1),
            backStackOf(),
            backStackOf(endNodeOf(A)),
            backStackOf(),
        ), // TODO when navigating select "1 of 3" for example i.e. index of size so we can set up the tabsOf with appropriate empty backstacks
        // TODO need a function to add remove tabs from index
        // TODO how to identify tab host if there are more than one
    )
}
