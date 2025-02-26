package co.early.n8

import co.early.n8.NestedTestData.Location.A
import co.early.n8.NestedTestData.Location.B
import co.early.n8.NestedTestData.Location.C
import co.early.n8.NestedTestData.Location.X1
import co.early.n8.NestedTestData.Location.X2
import co.early.n8.NestedTestData.Location.X3
import co.early.n8.NestedTestData.Location.Y1
import co.early.n8.NestedTestData.Location.Z1
import co.early.n8.NestedTestData.Location.Z3
import kotlinx.serialization.Serializable


class NestedTestData {

    @Serializable
    sealed class TabHost {

        @Serializable
        data object TabAbc : TabHost()

        @Serializable
        data object TabAbcStructural : TabHost()

        @Serializable
        data object TabX12 : TabHost()

        @Serializable
        data object TabX123 : TabHost()

        @Serializable
        data object TabXyz : TabHost()

        @Serializable
        data object TabZ3 : TabHost()
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
        data class Z3(val id:Int = 0) : Location()
    }
}

val tabHostSpecAbc = TabHostSpecification<NestedTestData.Location, NestedTestData.TabHost>(
    tabHostId = NestedTestData.TabHost.TabAbc,
    homeTabLocations = listOf(A, B, C),
)

val tabHostSpecAbcClear = TabHostSpecification<NestedTestData.Location, NestedTestData.TabHost>(
    tabHostId = NestedTestData.TabHost.TabAbc,
    homeTabLocations = listOf(A, B, C),
    clearToTabRoot = true
)

val tabHostSpecAbcStructural = TabHostSpecification<NestedTestData.Location, NestedTestData.TabHost>(
    tabHostId = NestedTestData.TabHost.TabAbcStructural,
    homeTabLocations = listOf(A, B, C),
    backMode = TabBackMode.Structural,
)

val tabHostSpecX12 = TabHostSpecification<NestedTestData.Location, NestedTestData.TabHost>(
    tabHostId = NestedTestData.TabHost.TabX12,
    homeTabLocations = listOf(X1, X2),
)

val tabHostSpecX123 = TabHostSpecification<NestedTestData.Location, NestedTestData.TabHost>(
    tabHostId = NestedTestData.TabHost.TabX123,
    homeTabLocations = listOf(X1, X2, X3),
    initialTab = 1,
)

val tabHostSpecXyz = TabHostSpecification<NestedTestData.Location, NestedTestData.TabHost>(
    tabHostId = NestedTestData.TabHost.TabXyz,
    homeTabLocations = listOf(X1, Y1, Z1),
)

val tabHostSpecZ3 = TabHostSpecification<NestedTestData.Location, NestedTestData.TabHost>(
    tabHostId = NestedTestData.TabHost.TabZ3,
    homeTabLocations = listOf(X1, Y1, Z3()),
    initialTab = 2,
)
