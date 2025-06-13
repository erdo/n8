package com.kmpfoo.ui.navigation

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
    sealed class EuropeanLocation(val isCountry: Boolean): Location() {

        @Serializable
        data object London : EuropeanLocation(false)

        @Serializable
        data object Paris : EuropeanLocation(false)

        @Serializable
        data class Milan(val message: String? = null) : EuropeanLocation(false)

        @Serializable
        data object Poland : EuropeanLocation(true)

        @Serializable
        data object Spain : EuropeanLocation(true)

        @Serializable
        data object France : EuropeanLocation(true)

        @Serializable
        data object Rhine : EuropeanLocation(false)

        @Serializable
        data object Danube : EuropeanLocation(false)

        @Serializable
        data object Thames : EuropeanLocation(false)

        @Serializable
        data object Seine : EuropeanLocation(false)
    }
}

@Serializable
sealed class TabHostId {

    @Serializable
    data object GlobalTabHost : TabHostId()

    @Serializable
    data object EuropeTabHost : TabHostId()
}


// inner tab
val tabHostSpecEurope = TabHostSpecification<Location, TabHostId>(
    tabHostId = TabHostId.EuropeTabHost,
    homeTabLocations = listOf(
        TabRoot.LocationRoot(Spain),
        TabRoot.LocationRoot(London),
        TabRoot.LocationRoot(Danube),
    )
)

// outer tab
val tabHostSpecGlobal = TabHostSpecification<Location, TabHostId>(
    tabHostId = TabHostId.GlobalTabHost,
    homeTabLocations = listOf(
        TabRoot.LocationRoot(NewYork),
        TabRoot.TabHostRoot(tabHostSpecEurope),
    ),
    initialTab = 0,
)
