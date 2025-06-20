//import SwiftUI
//import shared
//
//
//
//struct EuropeanTabHostView: View {
//    
//    private let tabHostId = LocationsKt.tabHostSpecEurope.tabHostId
//    
//    private let navModel: NavigationModel<Location, TabHostId>
//    private let navState: NavigationState<Location, TabHostId>
//    private let content: () -> AnyView
//    @State private var selectedTab = 0
//    
//    let europeTabs: [TabData] = [
//        TabData(
//            title: "Country",
//            systemImage: "house.fill",
//            content: AnyView(
//                Text(title)
//                    .font(.largeTitle)
//                    .background(Color.red.opacity(0.2))
//            )
//        ),
//        TabData(
//            title: "City",
//            systemImage: "gearshape.fill",
//            content:  AnyView(
//                Text(title)
//                    .font(.largeTitle)
//                    .background(Color.red.opacity(0.2))
//            )
//        ),
//        TabData(
//            title: "River",
//            systemImage: "house.fill",
//            content: AnyView(
//                   Text(title)
//                       .font(.largeTitle)
//                       .background(Color.red.opacity(0.2))
//            )
//        )
//    ]
//
//    
//    init(
//        navModel: NavigationModel<Location, TabHostId>,
//        navState: NavigationState<Location, TabHostId>,
//        content: @escaping () -> AnyView
//    ) {
//        self.navModel = navModel
//        self.navState = navState
//        self.content = content
//        
//        let tabHost = navState.locateTabHost(
//            tabHostId: tabHostId
//        )
//    }
//
//    var body: some View {
//        N8TabView(selection: $selectedTab, tabs: europeTabs)
//            .navigationTitle("My Custom Tabs") // This applies to the overall container, if used in a NavigationView/Stack
//            .onAppear {
//                print("Initial Selected Tab: \(selectedTab)")
//            }
//    }
//
//        
//  
//
//
//        
//        //struct ExampleContentView: View {
//        //    let tabIds = ["one", "two", "three"]
//        //
//        //    var body: some View {
//        //        TabHost(
//        //            tabs: tabIds,
//        //            content: { id in
//        //                Text("Tab content for \(id)")
//        //            },
//        //            tabItem: { id in
//        //                Label(id.capitalized, systemImage: "\(tabIds.firstIndex(of: id)! + 1).circle")
//        //            }
//        //        )
//        //    }
//        //}
