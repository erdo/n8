package com.kmpfoo.ui.navigation

import co.early.n8.TabBackMode
import co.early.n8.TabHostSpecification
import co.early.n8.TabRoot
import com.kmpfoo.ui.navigation.Location.EuropeanLocation.Danube
import com.kmpfoo.ui.navigation.Location.EuropeanLocation.London
import com.kmpfoo.ui.navigation.Location.EuropeanLocation.Spain
import com.kmpfoo.ui.navigation.Location.NewYork
import kotlinx.serialization.Serializable

@Serializable
sealed class Location {

    @Serializable
    data object Welcome : Location()

    @Serializable
    data object Home : Location()

    @Serializable
    data class Bangkok(val message: String? = null)  : Location()

    @Serializable
    data object Dakar : Location()

    @Serializable
    data object LA : Location()

    @Serializable
    data object NewYork : Location()

    @Serializable
    sealed class EuropeanLocation(): Location() {

        @Serializable
        data object London : EuropeanLocation()

        @Serializable
        data object Paris : EuropeanLocation()

        @Serializable
        data class Milan(val message: String? = null) : EuropeanLocation()

        @Serializable
        data object Poland : EuropeanLocation()

        @Serializable
        data object Spain : EuropeanLocation()

        @Serializable
        data object France : EuropeanLocation()

        @Serializable
        data object Rhine : EuropeanLocation()

        @Serializable
        data object Danube : EuropeanLocation()

        @Serializable
        data object Thames : EuropeanLocation()

        @Serializable
        data object Seine : EuropeanLocation()
    }
}

@Serializable
sealed class TabHostId {

    @Serializable
    data object GlobalTabHost : TabHostId()

    @Serializable
    data object EuropeTabHost : TabHostId()
}


// android tabs
val tabHostSpecEurope = TabHostSpecification<Location, TabHostId>(
    tabHostId = TabHostId.EuropeTabHost,
    homeTabLocations = listOf(
        TabRoot.LocationRoot(Spain),
        TabRoot.LocationRoot(London),
        TabRoot.LocationRoot(Danube),
    )
)
val tabHostSpecGlobal = TabHostSpecification<Location, TabHostId>(
    tabHostId = TabHostId.GlobalTabHost,
    homeTabLocations = listOf(
        TabRoot.LocationRoot(NewYork),
        TabRoot.TabHostRoot(tabHostSpecEurope),
    ),
    initialTab = 0,
)

// slightly different behaviour for iOS tabs
val tabHostSpecEuropeIos = TabHostSpecification<Location, TabHostId>(
    tabHostId = TabHostId.EuropeTabHost,
    homeTabLocations = listOf(
        TabRoot.LocationRoot(Spain),
        TabRoot.LocationRoot(London),
        TabRoot.LocationRoot(Danube),
    ),
    backMode = TabBackMode.Structural,
)

val tabHostSpecGlobalIos = TabHostSpecification<Location, TabHostId>(
    tabHostId = TabHostId.GlobalTabHost,
    homeTabLocations = listOf(
        TabRoot.LocationRoot(NewYork),
        TabRoot.TabHostRoot(tabHostSpecEuropeIos),
    ),
    initialTab = 0,
    backMode = TabBackMode.Structural,
)

