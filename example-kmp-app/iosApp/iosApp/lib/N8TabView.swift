import SwiftUI
import shared


struct N8TabView<L: AnyObject & Hashable, T: AnyObject & Hashable>: View {
    @State private var selection: Int = 0
    private let tabHostId: T
    private let tabCount: Int
    private let tabUiBuilder: (Int, Bool) -> any View // tabindex, selected
    private let contentUiBuilder: () -> any View //
    
    init(tabHostId: T,
         tabData: [TabData],
         @ViewBuilder tabUiBuilder: @escaping (Int, Bool) -> any View,
         @ViewBuilder contentUiBuilder: @escaping () -> any View
    ) {
        self.tabHostId = tabHostId
        self.tabCount = tabData.count
        self.tabUiBuilder = tabUiBuilder
        self.contentUiBuilder = contentUiBuilder
    }

    var body: some View {
        TabView(selection: $selection) {
            ForEach(0..<tabCount, id: \.self) { index in
                AnyView(contentUiBuilder())
                    .tag(index)
                    .tabItem {
                        AnyView(tabUiBuilder(index, index == selection))
                    }
            }
        }
    }
}

struct TabData: Identifiable {
    let id = UUID()
    let title: String
    let systemImage: String
    let content: AnyView // NB this is the content of the tab itself
}






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
