import SwiftUI

//an example tabView which doesn't use swift's TabView


struct CustomTabView: View {
    @State private var selectedTab: Int = 0

    var body: some View {
        VStack(spacing: 0) {
            // Content
            Group {
                switch selectedTab {
                case 0:
                    Text("Main Tab 1")
                case 1:
                    InnerCustomTabView()
                default:
                    Text("Unknown")
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            // Tab bar
            HStack {
                Button("Tab 1") { selectedTab = 0 }
                Spacer()
                Button("Tab 2") { selectedTab = 1 }
            }
            .padding()
            .background(Color.gray.opacity(0.1))
        }
    }
}

struct InnerCustomTabView: View {
    @State private var innerTab: Int = 0

    var body: some View {
        VStack {
            // Inner content
            if innerTab == 0 {
                Text("Inner Tab A")
            } else {
                Text("Inner Tab B")
            }

            // Inner tab bar
            HStack {
                Button("A") { innerTab = 0 }
                Spacer()
                Button("B") { innerTab = 1 }
            }
            .padding()
            .background(Color.blue.opacity(0.1))
        }
    }
}
