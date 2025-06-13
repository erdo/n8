import SwiftUI
import shared

struct MainContentView: View {
    
    let navState: NavigationState<Location, TabHost>
    
    init(navState: NavigationState<Location, TabHost>) {
        self.navState = navState
    }
    
    var body: some View {
        
        let hostedInTabHost1 = navState.hostedBy.contains { element in
            return element is TabHostLocation<TabHost.TabHost1>
        }
        
        if (hostedInTabHost1){
            EuropeanTabHostView(
                navState: navState,
                locationViewBuilder: { location in
                    currentViewMapper(currentLocation: navState.currentLocation)
                }
            ).onAppear(){
                Fore.companion.i(message:"EuropeanTabHostView (Hosted in TabHost) state:\(navState)")
            }
        } else {
            currentViewMapper(currentLocation: navState.currentLocation).onAppear(){
                Fore.companion.i(message:"(Not Hosted in TabHost)")
            }
        }
    }
    
    @ViewBuilder
    private func currentViewMapper(currentLocation: Location) -> some View {
        switch currentLocation {
            case Location.Bangkok.shared:
                BangkokView()
            case Location.Dakar.shared:
                DakarView()
            case Location.LA.shared:
                LAView()
            case Location.EuropeanLocationParis.shared:
                ParisView()
            case Location.EuropeanLocationLondon.shared:
                LondonView()
            case Location.EuropeanLocationMilan.shared:
                MilanView()
            default:
                Text("Unknown location \(navState.currentLocation)")
        }
    }
}
