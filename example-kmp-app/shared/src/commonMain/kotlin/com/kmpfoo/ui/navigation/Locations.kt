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

}
