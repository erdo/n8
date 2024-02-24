package foo.bar.n8.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Location {

    @Serializable
    data object Home : Location()

    @Serializable
    data object Bangkok : Location()

    @Serializable
    data object Dakar : Location()

    @Serializable
    data object Damascus : Location()

    @Serializable
    data object Houston : Location()

    @Serializable
    data object Lagos : Location()

    @Serializable
    data object LA : Location()

    @Serializable
    data object Mumbai : Location()

    @Serializable
    data object NewYork : Location()

    @Serializable
    data object Seoul : Location()

    @Serializable
    data object Shanghai : Location()

    @Serializable
    data object Sydney : Location()

    @Serializable
    data object Tokyo : Location()

    @Serializable
    sealed class EuropeanLocations : Location() {
        @Serializable
        data object London : EuropeanLocations()

        @Serializable
        data object Milan : EuropeanLocations()

        @Serializable
        data object Paris : EuropeanLocations()

        @Serializable
        data object Krakow : EuropeanLocations()

        @Serializable
        data object Stockholm : EuropeanLocations()
    }
}
