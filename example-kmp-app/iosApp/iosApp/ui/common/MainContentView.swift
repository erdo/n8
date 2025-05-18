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
           // EuropeanTabHostView(currentView)
            
//            EuropeanTabHostView(
//                tab1Content: currentView,
//                tab1Index: 0,
//                tab2Content: currentView,
//                tab2Index: 1,
//              //  navigationPath: how can we get the path out of N8 to share about?,
//                locationViewBuilder: { location in
//                    currentViewMapper(location)
//                    
//                }
//            )
        } else {
//            currentView
        }
    }
    
    @ViewBuilder
    private var currentViewMapper(currentLocation: Location): some View {
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
