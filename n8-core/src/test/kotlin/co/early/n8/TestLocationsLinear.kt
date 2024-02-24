package co.early.n8

import co.early.n8.LinearExample.Location
import co.early.n8.LinearExample.Location.EuropeanLocations.London
import co.early.n8.LinearExample.Location.EuropeanLocations.Paris
import co.early.n8.LinearExample.Location.Tokyo
import kotlinx.serialization.Serializable

class LinearExample {

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
}

fun testX() {

    val nav =
        backStackOf<Location, Unit>(
            endNodeOf(London),
            endNodeOf(Paris),
            endNodeOf(Tokyo),
        )

}
