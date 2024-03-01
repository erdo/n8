package co.early.n8

import kotlinx.serialization.Serializable


@Serializable
sealed class Location {

    @Serializable
    data object NewYork : Location()

    @Serializable
    data object Tokyo : Location()

    @Serializable
    data class Sydney(val withSunCreamFactor: Int? = null) : Location()

    @Serializable
    data object SunCreamSelector : Location()

    @Serializable
    sealed class EuropeanLocations : Location() {
        @Serializable
        data object London : EuropeanLocations()

        @Serializable
        data object Paris : EuropeanLocations()
    }
}
