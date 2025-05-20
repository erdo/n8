package com.kmpfoo.ui.navigation

import co.early.n8.TabHostSpecification
import com.kmpfoo.ui.navigation.Location.EuropeanLocation.London
import com.kmpfoo.ui.navigation.Location.EuropeanLocation.Paris
import kotlinx.serialization.Serializable

@Serializable
sealed class Location {

    @Serializable
    data object Bangkok : Location()

    @Serializable
    data object Dakar : Location()

    @Serializable
    data object LA : Location()

    @Serializable
    sealed class EuropeanLocation: Location() {
        @Serializable
        data object London : EuropeanLocation()

        @Serializable
        data object Paris : EuropeanLocation()

        @Serializable
        data object Milan : EuropeanLocation()
    }
}

@Serializable
sealed class TabHost {

    @Serializable
    data object TabHost1 : TabHost()
}

val tabHostSpec1 = TabHostSpecification<Location, TabHost>(
    tabHostId = TabHost.TabHost1,
    homeTabLocations = listOf(Paris, London),
)
