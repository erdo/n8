import SwiftUI
import shared

struct CurrentView: View {
    
    @EnvironmentN8<Location, TabHostId> private var n8
    
    let navState: NavigationState<Location, TabHostId>
    
    init(navState: NavigationState<Location, TabHostId>) {
        self.navState = navState
    }
    
    var body: some View {
        AnyView(currentViewMapper(currentLocation: navState.currentLocation))
    }
}

@ViewBuilder
private func currentViewMapper(currentLocation: Location) -> any View {
    switch currentLocation {
        case Location.Welcome.shared:
            ViewWelcome()
        case Location.Home.shared:
            ViewHome()
        case let location as Location.Bangkok:
            ViewBangkok()
        case Location.Dakar.shared:
            ViewDakar()
        case Location.LA.shared:
            ViewLA()
        case Location.NewYork.shared:
            ViewNewYork()
        case Location.EuropeanLocationLondon.shared:
            ViewLondon()
        case let location as Location.EuropeanLocationMilan:
            ViewMilan(location:location)
        case Location.EuropeanLocationParis.shared:
            ViewParis()
        case Location.EuropeanLocationDanube.shared:
            ViewDanube()
        case Location.EuropeanLocationFrance.shared:
            ViewFrance()
        case Location.EuropeanLocationPoland.shared:
            ViewPoland()
        case Location.EuropeanLocationRhine.shared:
            ViewRhine()
        case Location.EuropeanLocationSeine.shared:
            ViewSeine()
        case Location.EuropeanLocationSpain.shared:
            ViewSpain()
        case Location.EuropeanLocationThames.shared:
            ViewThames()
        default:
            Text("Unknown location \(currentLocation)")
    }
}
