package com.kmpfoo.ui.navigation

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
