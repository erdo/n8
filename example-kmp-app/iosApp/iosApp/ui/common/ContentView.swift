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
                BangkokView().onAppear { Fore.companion.e(message: "BangkokView() onAppear") }
            case Location.Dakar.shared:
                DakarView().onAppear { Fore.companion.e(message: "DakarView() onAppear") }
            case Location.LA.shared:
                LAView().onAppear { Fore.companion.e(message: "LAView() onAppear") }
            default:
                Text("Unknown location \(navState.currentLocation)").onAppear { Fore.companion.e(message: "Text onAppear") }
        }
    }
}
