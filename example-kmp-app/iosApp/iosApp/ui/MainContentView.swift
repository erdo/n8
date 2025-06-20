import SwiftUI
import shared

struct MainContentView: View {
    
    let navState: NavigationState<Location, TabHostId>
    
    init(navState: NavigationState<Location, TabHostId>) {
        self.navState = navState
    }
    
    var body: some View {
        
//        let hostedInTabHost1 = navState.hostedBy.contains { element in
//            return element is TabHostLocation<TabHostId.TabHost1>
//        }
//        
//        if (hostedInTabHost1){
//            EuropeanTabHostView(
//                navState: navState,
//                locationViewBuilder: { location in
//                    currentViewMapper(currentLocation: navState.currentLocation)
//                }
//            ).onAppear(){
//                Fore.companion.i(message:"EuropeanTabHostView (Hosted in TabHost) state:\(navState)")
//            }
//        } else {
            currentViewMapper(currentLocation: navState.currentLocation).onAppear(){
                Fore.companion.i(message:"(Not Hosted in TabHost)")
            }
//        }
    }
    
    @ViewBuilder
    private func currentViewMapper(currentLocation: Location) -> some View {
        switch currentLocation {
            case let location as Location.Bangkok:
                ViewBangkok()
            case Location.Dakar.shared:
                ViewDakar()
            case Location.EuropeanLocationDanube.shared:
                ViewDanube()
            case Location.Home.shared:
                ViewHome()
            case Location.LA.shared:
                ViewLA()
            case Location.EuropeanLocationLondon.shared:
                ViewLondon()
            case let location as Location.EuropeanLocationMilan:
                ViewMilan()
            case Location.EuropeanLocationParis.shared:
                ViewParis()
            case Location.EuropeanLocationSpain.shared:
                ViewSpain()
            case Location.Welcome.shared:
                ViewWelcome()
            default:
                Text("Unknown location \(navState.currentLocation)")
        }
    }
}
