//
//here
//
//struct TabHostEurope: View {
//    
//    let navModel: NavigationModel<Location, TabHostId>
//    let navState: NavigationState<Location, TabHostId>
//    let wrappingTabHosts: [TabHostLocation<TabHostId>]
//    let depth: Int
//    let content: () -> any View // Using 'any View' for type erasure
//    
//    
//
//    var body: some View {
//        
//        N8TabView<Location, TabHostId>(
//            tabHostId:
//            tabCount: tabIds,
//            content: { id in
//                Text("Tab content for \(id)")
//            },
//            tabItem: { id in
//                Label(id.capitalized, systemImage: "\(tabIds.firstIndex(of: id)! + 1).circle")
//            }
//        )
//        
//        
//        // The main TabView for your application
//        TabView(selection: $navModel.selectedMainTab) {
//            // Global Tab
//            NavigationStack { // Each main tab often has its own navigation stack
//                GlobalInnerTabView()
//            }
//            .tag(tabHostIdGlobal as! GenericTabHostId)
//            .tabItem {
//                Label("Global", systemImage: "globe")
//            }
//
//            // Europe Tab
//            NavigationStack { // Each main tab often has its own navigation stack
//                EuropeInnerTabView()
//            }
//            .tag(tabHostIdEurope as! GenericTabHostId)
//            .tabItem {
//                Label("Europe", systemImage: "eurosign.circle.fill")
//            }
//
//            // You could add more main tabs here
//            // NavigationStack {
//            //    Text("Another Tab Content")
//            // }
//            // .tag(GenericTabHostId("AnotherTab"))
//            // .tabItem {
//            //    Label("Other", systemImage: "ellipsis.circle")
//            // }
//        }
//    }
//}
