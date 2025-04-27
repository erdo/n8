import SwiftUI
import shared

@main
struct iOSApp: App {

    init() {
        OG.create()
        OG.initialize()
    }
    
    var body: some Scene {
        let n8 = OG[NavigationModel<Location, KotlinUnit>.self]
        
        WindowGroup {
            N8Host<Location, KotlinUnit>(n8:n8) { navState in
                Group {
                    if navState.initialLoading {
                        ProgressView()
                            .scaleEffect(2)
                            .padding().tint(.blue)
                    } else {
                        ContentView(navState:navState)
                    }
                }
                .onAppear {
                    Fore.Companion().i(message: "(D) --onAppear() state: \(navState)")
                }
                .onChange(of: navState) { newState in
                    Fore.Companion().i(message: "(C) --onChange() state: \(newState)")
                }
            }
        }
    }
}


//@main
//struct MyApp: App {
//    @StateObject private var sessionManager = SessionManager()
//    @StateObject private var navController = NavigationController<String>()
//
//    var body: some Scene {
//        WindowGroup {
//            ContentView()
//                .environmentObject(sessionManager)
//                .environmentObject(navController)
//        }
//    }
//}
//
