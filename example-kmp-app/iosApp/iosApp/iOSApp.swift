import SwiftUI
import shared

@main
struct iOSApp: App {
    
    private let navigationModel: NavigationModel<Location, KotlinUnit>
    @StateObject private var navigationState: ObservableState<NavigationState<Location, KotlinUnit>>
    
    init() {
        OG.create()
        OG.initialize()
        
        let navModel = OG[NavigationModel<Location, KotlinUnit>.self]
        _navigationState = navModel.toStateObject { navModel.state }
        navigationModel = navModel
    }
    
	var body: some Scene {
        
        let state = navigationState.state
        
		WindowGroup {
            Group {
                if state.initialLoading {
                    ProgressView()
                        .scaleEffect(2)
                        .padding().tint(.blue)
                } else {
                    switch state.currentLocation {
                    case Location.Bangkok.shared:
                        BangkokView()
                    case Location.Dakar.shared:
                        DakarView()
                    case Location.LA.shared:
                        LAView()
                    default:
                        Text("Unknown location")
                    }
                }
            }
            .onAppear {
                // Log the initial state when view appears
                Fore.Companion().i(message: "initial navigation state initialLoading: \(state.initialLoading)")
            }
            .onChange(of: state) { newState in
                Fore.Companion().i(message: "navigation state changed initialLoading: \(newState.initialLoading)")
            }
		}
	}
}
