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
            N8Host<Location, TabHostId>(
                n8:n8
            ) { navState in
                Group {
                    if navState.initialLoading {
                        ProgressView()
                            .scaleEffect(3)
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



//            switch tabHostLocation.tabHostId {
//            case TabHostId.EuropeTabHost.shared:
//                return AnyView(TabHostEurope(content: nestedContent))
//            case TabHostId.GlobalTabHost.shared:
//                return AnyView(TabHostGlobal(content: nestedContent))
//            default:
//                fatalError("unrecognised tabHostId '\(String(describing: tabHostLocation.tabHostId))'")
//            }



//struct ExampleContentView: View {
//    let tabIds = ["one", "two", "three"]
//
//    var body: some View {
//        TabHost(
//            tabs: tabIds,
//            content: { id in
//                Text("Tab content for \(id)")
//            },
//            tabItem: { id in
//                Label(id.capitalized, systemImage: "\(tabIds.firstIndex(of: id)! + 1).circle")
//            }
//        )
//    }
//}





//struct ExampleContentView: View {
//    let tabIds = ["one", "two", "three"]
//
//    var body: some View {
//        TabHost(
//            tabs: tabIds,
//            content: { id in
//                Text("Tab content for \(id)")
//            },
//            tabItem: { id in
//                Label(id.capitalized, systemImage: "\(tabIds.firstIndex(of: id)! + 1).circle")
//            }
//        )
//    }
//}
