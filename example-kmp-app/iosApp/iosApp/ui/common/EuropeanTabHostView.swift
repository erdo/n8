import SwiftUI
import shared



struct EuropeanTabHostView: View {
    
    
    @State private var selectedTab = 0
    private let tabHostId = LocationsKt.tabHostSpec1.tabHostId! //force unwrap here because we know this is not null, but KMP / Swift doesn't know
    private let navState: NavigationState<Location, TabHost>
    private let locationViewBuilder: (Location) -> AnyView
    
    init<V:View>(
        navState: NavigationState<Location, TabHost>,
        locationViewBuilder: @escaping (Location) -> V
    ) {
        self.navState = navState
        self.locationViewBuilder = { location in
            AnyView(locationViewBuilder(location))
        }
        
        if let tabHost = navState.navigation._tabHostFinder(
            tabHostIdToFind: tabHostId,
            skipParentCheck: false
        ) {
            // Use tabHost here
            print("Found tabHost: \(tabHost)")
            
            tabHost.tabs.map { backStack in
                
                let node = backStack.stack.first

                if let endNode = node as? NavigationEndNode {
                    return endNode.location
                } else if let tabHost = node as? SharedTabHost {
                    // Recursively inspect tabHost.tabs, etc.
                }
                
            
            
            
            
            let myTabs: [TabData] = tabHost.tabs.map { backStack in
                guard let firstLocation = (backStack.stack as? [SharedLocation])?.first else {
                    return nil
                }
            
        }
        
        Fore.companion.i(message:"creating European Tab Host")
    }
            
            func findEndNode() -> NavigationEndNode<Location, TabHost> {
                
            }
            
        func findRootEndNodes(from node: Navigation<Locatin, TabHost>) -> [NavigationEndNode<Location, TabHost>] {
            var result: [Any] = []

            if let endNode = node as? SharedEndNode {
                // Collect the location
                result.append(endNode.location)
            } else if let tabHost = node as? SharedTabHost {
                // Explore each backStack
                for backStack in tabHost.tabs {
                    // Each backStack has a stack: [SharedNode]
                    for subNode in backStack.stack {
                        result.append(contentsOf: findAllEndNodeLocations(from: subNode))
                    }
                }
            }

            return result
        }
    
    let myTabs: [TabData] = [
        TabData(
            title: "One",
            systemImage: "house.fill",
            content: AnyView(
                Text("Content for Tab 1")
                    .font(.largeTitle)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.red.opacity(0.2))
            )
        ),
        TabData(
            title: "Two",
            systemImage: "gearshape.fill",
            content: AnyView(
                VStack {
                    Text("Third Tab Content")
                    Button("Push to Nav Stack") {
                        // Example: Push a new view onto the NavigationStack of the third tab
                        // You would typically handle navigation programmatically here.
                        // Accessing navigationStore:
                        // If `NavigationPathStore` is an `EnvironmentObject` or passed down,
                        // you could do something like:
                        // @EnvironmentObject var navigationStore: NavigationPathStore
                        // self.navigationStore.path(for: selectedTab).wrappedValue.append("SomeDestination")
                        // Or if you had a specific index:
                        // self.navigationStore.path(for: 2).wrappedValue.append("SomeDestination")
                    }
                }
            )
        )
    ]

    var body: some View {
        N8TabView(selection: $selectedTab, tabs: myTabs)
            .navigationTitle("My Custom Tabs") // This applies to the overall container, if used in a NavigationView/Stack
            .onAppear {
                print("Initial Selected Tab: \(selectedTab)")
            }
    }
}



//struct EuropeanTabHostViewX: View {
//    
//    var body: some View {
//        NavigationStack(path: $navigationPath) {
//            TabView {
//                AnyView(content1)
//                    .tabItem {
//                        Label("First", systemImage: "1.circle")
//                    }
//                    .tag(tab1Index)
//                
//                AnyView(content2)
//                    .tabItem {
//                        Label("Second", systemImage: "2.circle")
//                    }
//                    .tag(tab2Index)
//            }
//            .navigationTitle("European Locations")
//            .navigationDestination(for: Location.self) { location in
//                locationViewBuilder(location)
//            }
//            
//            //        .navigationDestination(for: String.self) { destination in
//            //            switch destination {
//            //            case "Milan":
//            //                MilanView()
//            //            // Add other destinations as needed
//            //            default:
//            //                Text("Unknown destination")
//            //            }
//            //        }
//        }
//    }
//}
