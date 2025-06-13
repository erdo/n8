import SwiftUI
import shared

struct N8TabHost<L: AnyObject & Hashable, T: AnyObject & Hashable>: View {
    @State private var selection: Int
    private let tabHostId: T
    private let tabCount: Int
    private let tabUiBuilder: (T, Int, Bool) -> any View
    private let contentUiBuilder: () -> any View
    
    init(tabHostId: T,
         tabCount: Int,
         @ViewBuilder tabUiBuilder: @escaping (T, Int, Bool) -> any View,
         @ViewBuilder contentUiBuilder: @escaping () -> any View
    ) {
        self.tabHostId = tabHostId
        self.tabCount = tabCount
        self.tabUiBuilder = tabUiBuilder
        self.contentUiBuilder = contentUiBuilder
    }

    var body: some View {
        TabView(selection: $selection) {
            ForEach(0..<tabCount, id: \.self) { index in
                AnyView(contentUiBuilder())
                    .tag(index)
                    .tabItem {
                        AnyView(tabUiBuilder(tabHostId, index, index == selection))
                    }
            }
        }
    }
}



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
