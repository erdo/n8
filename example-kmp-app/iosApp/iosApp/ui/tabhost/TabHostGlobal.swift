import SwiftUI
import shared

struct TabHostGlobal: View {
    
    @EnvironmentN8<Location, TabHostId> private var n8
    @State private var selectedTab: Int = 0
    
    let tabHostSpec = LocationsKt.tabHostSpecGlobalIos
    let tabLabels = ["Global", "Europe"]
    
    let content: () -> AnyView
    
    init(
        selectedTabIndex: Int,
        content: @escaping () -> AnyView
    ) {
        self.selectedTab = selectedTabIndex
        self.content = content
    }
    
    var body: some View {
        VStack(spacing: 0) {
            TabView(selection: $selectedTab) {
                content()
                    .tag(0)
                    .tabItem {
                        Label("Global", systemImage: "globe")
                    }

                content()
                    .tag(1)
                    .tabItem {
                        Label("Europe", systemImage: "eurosign.circle.fill")
                    }
            }
        }.onChange(of: selectedTab) { newTab in
            n8.switchTab(tabHostSpec: tabHostSpec, tabIndex: Int32(selectedTab))
        }
    }
}
