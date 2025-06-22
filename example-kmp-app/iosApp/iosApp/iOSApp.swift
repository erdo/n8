import SwiftUI
import shared

@main
struct iOSApp: App {

    init() {
        OG.create()
        OG.initialize()
    }
    
    var body: some Scene {
        let n8: NavigationModel<Location, TabHostId> = OG[NavigationModel<Location, TabHostId>.self]
        WindowGroup {
         //   PlatformTabView() // to test plaftormTabHostView it needs to be run at the top level
            N8Host<Location, TabHostId>(
                n8:n8
            ) { navState in
                Group {
                    if navState.initialLoading {
                        ProgressView()
                            .scaleEffect(3)
                            .padding().tint(.blue)
                    } else {
                        ContentRoot(navState:navState)
                    }
                }
            }
        }
    }
}

struct ContentRoot: View {
    
    @EnvironmentN8<Location, TabHostId> private var n8
    
    let navState: NavigationState<Location, TabHostId>
    
    init(navState: NavigationState<Location, TabHostId>) {
        self.navState = navState
    }

    var body: some View {
        if !n8.state.hostedBy.isEmpty {
            RootTabHostView(n8:n8) {
                AnyView(CurrentView(navState:navState))
            }
        } else {
            CurrentView(navState:navState)
        }
    }
}
