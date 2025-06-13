import SwiftUI
import shared

@main
struct iOSApp: App {

    init() {
        OG.create()
        OG.initialize()
    }
    
    var body: some Scene {
        let n8 = OG[NavigationModel<Location, TabHost>.self]
        
        WindowGroup {
            N8Host<Location, TabHost>(
                n8:n8,
                tabUiBuilder: { tabHostId, index, selected in
                    Label(tabHostId.description, systemImage: "gearshape.fill") // to do also need to handle click listeners here or not?
                }
            ) { navState in
                Group {
                    if navState.initialLoading {
                        ProgressView()
                            .scaleEffect(2)
                            .padding().tint(.blue)
                    } else {
                        MainContentView(navState:navState)
                    }
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
