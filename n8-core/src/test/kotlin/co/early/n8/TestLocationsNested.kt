package co.early.n8

import co.early.n8.NestedExample.Location.A
import co.early.n8.NestedExample.Location.B
import co.early.n8.NestedExample.Location.C
import co.early.n8.NestedExample.Location.D
import co.early.n8.NestedExample.Location.E
import co.early.n8.NestedExample.Location.X1
import co.early.n8.NestedExample.Location.X2
import co.early.n8.NestedExample.Location.Y1
import co.early.n8.NestedExample.Location.Y2
import kotlinx.serialization.Serializable


class NestedExample {

    @Serializable
    sealed class TabHost {

        @Serializable
        data object TabAbc : TabHost()

        @Serializable
        data object TabAbcStructural : TabHost()

        @Serializable
        data object TabX12 : TabHost()

        @Serializable
        data object TabXyz : TabHost()
    }

    @Serializable
    sealed class Location {

        @Serializable
        data object Home : Location()

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
        data object X1 : Location()

        @Serializable
        data object X2 : Location()

        @Serializable
        data object X3 : Location()

        @Serializable
        data object Y1 : Location()

        @Serializable
        data object Y2 : Location()

        @Serializable
        data object Y3 : Location()

        @Serializable
        data object Z1 : Location()

        @Serializable
        data object Z2 : Location()

        @Serializable
        data object Z3 : Location()
    }
}

val tabHostSpecAbc = TabHostSpecification<NestedExample.Location, NestedExample.TabHost>(
    tabHostId = NestedExample.TabHost.TabAbc,
    homeTabLocations = listOf(A, B, C),
)

val tabHostSpecAbcStructural = TabHostSpecification<NestedExample.Location, NestedExample.TabHost>(
    tabHostId = NestedExample.TabHost.TabAbcStructural,
    homeTabLocations = listOf(A, B, C),
    backMode = TabBackMode.Structural,
)

val tabHostSpecX12 = TabHostSpecification<NestedExample.Location, NestedExample.TabHost>(
    tabHostId = NestedExample.TabHost.TabX12,
    homeTabLocations = listOf(X1, X2),
)

val tabHostSpecXyz = TabHostSpecification<NestedExample.Location, NestedExample.TabHost>(
    tabHostId = NestedExample.TabHost.TabXyz,
    homeTabLocations = listOf(X1, Y1, NestedExample.Location.Z1),
)

fun test1() {
    val nav = backStackOf(
        endNodeOf(A), endNodeOf(B), tabsOf(
            selectedTabHistory = listOf(0),
            tabHostId = "TAB_HOST_1",
            backStackOf(
                endNodeOf(X1), endNodeOf(C), endNodeOf(D), tabsOf(
                    selectedTabHistory = listOf(0, 1), tabHostId = "TAB_HOST_2", backStackOf(
                        endNodeOf(Y1),
                        endNodeOf(E),
                    ), backStackOf(
                        endNodeOf(Y2),
                    )
                )
            ),
            backStackOf(
                endNodeOf(X1),
            ),
            backStackOf(
                endNodeOf(X2),
            ),
        )
    )
}

fun test2() {
    val nav = backStackNoTabsOf(
        endNodeOf(A),
    )
}

