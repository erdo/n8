package foo.bar.n8.ui.navigation

import co.early.n8.TabHostSpecification
import kotlinx.serialization.Serializable

@Serializable
sealed class TabHostId {

    @Serializable
    data object TabsMain : TabHostId()

    @Serializable
    data object TabsSettings : TabHostId()

    @Serializable
    data object TabsAccount : TabHostId()
}

val tabHostSpecMain = TabHostSpecification<Location, TabHostId>(
    tabHostId = TabHostId.TabsMain,
    homeTabLocations = listOf(Location.EuropeanLocations.Paris, Location.NewYork, Location.Tokyo),
)

val tabHostSpecSettings = TabHostSpecification<Location, TabHostId>(
    tabHostId = TabHostId.TabsSettings,
    homeTabLocations = listOf(Location.Dakar, Location.Bangkok),
)

val tabHostSpecAccount = TabHostSpecification<Location, TabHostId>(
    tabHostId = TabHostId.TabsAccount,
    homeTabLocations = listOf(Location.Mumbai, Location.LA, Location.Seoul),
)
