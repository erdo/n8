import SwiftUI
import shared

struct ContentView: View {
    
    let navState: NavigationState<Location, KotlinUnit>
    
    init(navState: NavigationState<Location, KotlinUnit>) {
        self.navState = navState
    }
    
    var body: some View {
        switch navState.currentLocation {
            case Location.Bangkok.shared:
                BangkokView()
            case Location.Dakar.shared:
                DakarView()
            case Location.LA.shared:
                LAView()
            default:
                Text("Unknown location \(navState.currentLocation)")
        }
    }
}
