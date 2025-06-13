//import SwiftUI
//
//
//
///**
// 
// Attempt to get an API as close to system TabView as possible, not sure it's possible
// 
// */
//struct N8TabView<Content: View>: View {
//    @Binding var selection: Int
//    let content: Content
//
//    @StateObject private var navigationStore = NavigationPathStore()
//
//    init(selection: Binding<Int>, @ViewBuilder content: () -> Content) {
//        self._selection = selection
//        self.content = content()
//    }
//
//    init(@ViewBuilder content: () -> Content) {
//        self._selection = .constant(0) // Default to 0 if no selection binding is provided
//        self.content = content()
//    }
//
//    var body: some View {
//        TabView(selection: $selection) {
//            // This ViewBuilder wrapper extracts TabItem views and gives them indices
//            TabItemsExtractor(content: content) { index, view, tabItemContent in
//                // Wrap each tab in a NavigationStack with the index as its tag
//                NavigationStack(path: navigationStore.path(for: index)) {
//                    view
//                }
//                .tag(index)
//                .tabItem {
//                    tabItemContent()
//                }
//            }
//        }
//    }
//}
//
//struct TabItemsExtractor<Content: View, TabItemContent: View>: View {
//    let content: Content
//    let transform: (Int, AnyView, () -> TabItemContent) -> AnyView // Changed TransformedContent to AnyView
//
//    /// State to hold the extracted (view, tabItemContent) tuples.
//    /// `AnyView` is used for type erasure of the primary view and `() -> TabItemContent` for the tabItem's ViewBuilder content.
//    @State private var tabItems: [(view: AnyView, tabItem: () -> TabItemContent)] = []
//
//    /// Initializes the extractor.
//    /// - Parameters:
//    ///   - content: The `ViewBuilder` content provided to `N8TabView`.
//    ///   - transform: A closure that takes the index, the extracted view, and a closure for the tabItem content,
//    ///                and returns the transformed view to be displayed in the `TabView`.
//    init(
//        content: Content,
//        transform: @escaping (Int, AnyView, () -> TabItemContent) -> AnyView // Changed TransformedContent to AnyView
//    ) {
//        self.content = content
//        self.transform = transform
//    }
//
//    var body: some View {
//        ZStack {
//            // Render the original content but hide it.
//            // When SwiftUI renders `content`, it will trigger the .tabItem modifier,
//            // which in turn sets the TabItemPreferenceKey values.
//            content
//                .hidden() // Make it invisible but allow it to contribute preferences
//                .onPreferenceChange(TabItemPreferenceKey<TabItemContent>.self) { preferences in
//                    // When preferences change, update our internal state with the extracted tab items.
//                    // The `map` converts the stored (AnyView, TabItemContent) to (AnyView, () -> TabItemContent)
//                    // to match the expected type for `tabItems`.
//                    self.tabItems = preferences.map { (view, tabItem) in
//                        (view, { tabItem }) // Wrap the tabItem content in a closure as `tabItem` expects a ViewBuilder
//                    }
//                }
//
//            // Once tabItems are extracted, iterate over them and apply the transform.
//            ForEach(0..<tabItems.count, id: \.self) { index in
//                // Call the transform closure, ensuring its return type is AnyView,
//                // which is fine for ForEach as it accepts View-conforming types.
//                transform(index, tabItems[index].view, tabItems[index].tabItem)
//            }
//        }
//    }
//}
//
///// A PreferenceKey to extract the primary view content and its associated tabItem content.
/////
///// This PreferenceKey is set by a custom modifier that applies a 'fake' tabItem,
///// allowing us to capture the view and its tab item content.
//struct TabItemPreferenceKey<TabItemContent: View>: PreferenceKey {
//
//    static var defaultValue: [(AnyView, TabItemContent)] { [] }
//
//    /// Combines multiple preference values.
//    /// Since multiple views might set this preference, we append them to the array.
//    static func reduce(
//        value: inout [(AnyView, TabItemContent)],
//        nextValue: () -> [(AnyView, TabItemContent)]
//    ) {
//        value.append(contentsOf: nextValue())
//    }
//}
//
//extension View {
//    /// A custom modifier that captures the view it's applied to and its tabItem content,
//    /// then sets it as a PreferenceKey value.
//    /// This is what allows `TabItemsExtractor` to "see" the tab content.
//    func _tabItem<TabItemContent: View>(@ViewBuilder tabItem: () -> TabItemContent) -> some View {
//        // We render the original view, and on top of it, we use `preference` to
//        // inject the (AnyView of self, and the TabItemContent) into the preference key.
//        self.overlay {
//            // This invisible overlay is just for injecting the preference.
//            GeometryReader { _ in
//                Color.clear // Use a clear color to ensure it's invisible
//                    .preference(key: TabItemPreferenceKey<TabItemContent>.self, value: [(AnyView(self), tabItem())])
//            }
//        }
//    }
//}
//
//
///// An ObservableObject to store and manage separate NavigationPaths for each tab.
//class NavigationPathStore: ObservableObject {
//    @Published var paths: [Int: NavigationPath] = [:]
//
//    func path(for index: Int) -> Binding<NavigationPath> {
//        Binding(
//            get: { self.paths[index] ?? NavigationPath() },
//            set: { self.paths[index] = $0 }
//        )
//    }
//}
//
//
//// MARK: - Usage Examples
//
//struct N8TabViewExample: View {
//    @State private var selectedTab = 0
//
//    var body: some View {
//        N8TabView(selection: $selectedTab) {
//            Text("Content for Tab 1")
//                .font(.largeTitle)
//                .frame(maxWidth: .infinity, maxHeight: .infinity)
//                .background(Color.red.opacity(0.2))
//                ._tabItem {
//                    Label("Home", systemImage: "house.fill")
//                }
//
//            List {
//                Text("Item A")
//                Text("Item B")
//                NavigationLink("Go to Detail (Tab 2)") {
//                    Text("Detail View for Tab 2")
//                }
//            }
//            .navigationTitle("Second Tab List")
//            ._tabItem {
//                Label("List", systemImage: "list.bullet")
//            }
//
//            VStack {
//                Text("Third Tab Content")
//                Button("Push to Nav Stack") {
//                    // This push would be handled by the NavigationStack of this tab
//                    // and routed via the String.self destination in N8TabView
//                    // if you use navigationDestination(for: String.self) here.
//                    // For example:
//                    // navigationStore.path(for: 2).wrappedValue.append("Something dynamic")
//                }
//            }
//            .navigationTitle("Third Tab")
//            ._tabItem {
//                Label("Settings", systemImage: "gearshape.fill")
//            }
//        }
//        .navigationTitle("My Custom Tabs") // This modifier applies to the N8TabView itself
//        .onAppear {
//            print("Initial Selected Tab: \(selectedTab)")
//        }
//    }
//}
//
//struct N8TabView_Previews: PreviewProvider {
//    static var previews: some View {
//        N8TabViewExample()
//    }
//}
