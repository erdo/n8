import SwiftUI
import shared

struct EuropeanTabHostView: View {
    
    private let content1: any View
    private let content2: any View
    private let tab1Index: Int
    private let tab2Index: Int
    @Binding var navigationPath: NavigationPath
    private let locationViewBuilder: (Location) -> AnyView
    
    init<V:View>(
        tab1Content: any View,
        tab1Index: Int,
        tab2Content: any View,
        tab2Index: Int,
        navigationPath: Binding<NavigationPath>,
        locationViewBuilder: @escaping (Location) -> V
    ) {
        self.content1 = tab1Content
        self.content2 = tab2Content
        self.tab1Index = tab1Index
        self.tab2Index = tab2Index
        self._navigationPath = navigationPath
        self.locationViewBuilder = { location in
            AnyView(locationViewBuilder(location))
        }
    }
    
    var body: some View {
        NavigationStack(path: $navigationPath) {
            TabView {
                AnyView(content1)
                    .tabItem {
                        Label("First", systemImage: "1.circle")
                    }
                    .tag(tab1Index)
                
                AnyView(content2)
                    .tabItem {
                        Label("Second", systemImage: "2.circle")
                    }
                    .tag(tab2Index)
            }
            .navigationTitle("European Locations")
            .navigationDestination(for: Location.self) { location in
                locationViewBuilder(location)
            }
            
            //        .navigationDestination(for: String.self) { destination in
            //            switch destination {
            //            case "Milan":
            //                MilanView()
            //            // Add other destinations as needed
            //            default:
            //                Text("Unknown destination")
            //            }
            //        }
        }
    }
}
