
import SwiftUI

/*
 
 Wrapping TabView
 - so that the .tag is always set to the index of the tab
 - and each tab has it's own NavigationPath
 
 */

struct TabData: Identifiable {
    let id = UUID() // Unique identifier for ForEach
    let title: String
    let systemImage: String
    let content: AnyView // Type-erase the actual tab content View
}

struct N8TabView: View {
    @Binding var selection: Int
    let tabs: [TabData] // Array of your tab data

    @StateObject private var navigationStore = NavigationPathStore()

    // Initialize with a selection binding and an array of TabData
    init(selection: Binding<Int>, tabs: [TabData]) {
        self._selection = selection
        self.tabs = tabs
    }
    
    // Optional: An init to default selection to 0, if you wish
    init(tabs: [TabData]) {
        self._selection = .constant(0)
        self.tabs = tabs
    }

    var body: some View {
        TabView(selection: $selection) {
            // Iterate over your structured tab data
            ForEach(tabs.indices, id: \.self) { index in // id: \.self works because indices are Int and Hashable
                let tab = tabs[index] // Get the current tab data
                
                // Wrap the content in NavigationStack
//                NavigationStack(path: navigationStore.path(for: index)) {
//                    tab.content // Use the type-erased content
//                }
                tab.content
                .tag(index) // Tag for TabView selection
                .tabItem { // Use the tab item data directly
                    Label(tab.title, systemImage: tab.systemImage)
                }
            }
        }
    }
}

class NavigationPathStore: ObservableObject {
    @Published var paths: [Int: NavigationPath] = [:]

    func path(for index: Int) -> Binding<NavigationPath> {
        Binding(
            get: { self.paths[index] ?? NavigationPath() },
            set: { self.paths[index] = $0 }
        )
    }
}

// MARK: - Usage Examples

struct N8TabViewExample: View {
    @State private var selectedTab = 0

    // Define your tabs as an array of TabData
    let myTabs: [TabData] = [
        TabData(
            title: "Home",
            systemImage: "house.fill",
            content: AnyView(
                Text("Content for Tab 1")
                    .font(.largeTitle)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.red.opacity(0.2))
            )
        ),
        TabData(
            title: "List",
            systemImage: "list.bullet",
            content: AnyView(
                List {
                    Text("Item A")
                    Text("Item B")
                    NavigationLink("Go to Detail (Tab 2)") {
                        Text("Detail View for Tab 2")
                    }
                }
                .navigationTitle("Second Tab List") // This navigation title will apply to the NavigationStack
            )
        ),
        TabData(
            title: "Settings",
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
                .navigationTitle("Third Tab") // This navigation title will apply to the NavigationStack
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

struct N8TabView_Previews: PreviewProvider {
    static var previews: some View {
        N8TabViewExample()
    }
}
