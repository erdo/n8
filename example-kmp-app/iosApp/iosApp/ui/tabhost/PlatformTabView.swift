import SwiftUI

// this is just to test out iOS swiftUI default Tabs behaviour, if you run this it must be the top level view in WindowGroup


// MARK: - Screen Views

struct ColorScreen: View {
    let color: Color
    let title: String
    let nextScreenAction: () -> Void

    var body: some View {
        ZStack {
            color.ignoresSafeArea()
            VStack {
                Text(title)
                    .font(.largeTitle)
                    .foregroundColor(.white)
                Spacer()
                Button("Go to Next Screen") {
                    nextScreenAction()
                }
                .buttonStyle(.borderedProminent)
                .tint(.blue)
                Spacer()
            }
        }
        .navigationTitle(title)
        .navigationBarTitleDisplayMode(.inline) // Ensure title is always visible
    }
}

// MARK: - Tab 1 Content

struct Tab1ContentView: View {
    @State private var path = NavigationPath()

    var body: some View {
        NavigationStack(path: $path) {
            ColorScreen(color: .red, title: "Red Screen 1") {
                path.append("green1")
            }
            .navigationDestination(for: String.self) { value in
                if value == "green1" {
                    ColorScreen(color: .green, title: "Green Screen 2") {
                        path.append("yellow1")
                    }
                } else if value == "yellow1" {
                    ColorScreen(color: .yellow, title: "Yellow Screen 3") {
                        // Optionally go back to root or push another screen
                        // For this example, we'll just stay on the last screen
                    }
                }
            }
        }
    }
}

// MARK: - Tab 2 Content

struct Tab2ContentView: View {
    @State private var path = NavigationPath()

    var body: some View {
        NavigationStack(path: $path) {
            ColorScreen(color: .red, title: "Red Screen A") {
                path.append("greenA")
            }
            .navigationDestination(for: String.self) { value in
                if value == "greenA" {
                    ColorScreen(color: .green, title: "Green Screen B") {
                        path.append("yellowA")
                    }
                } else if value == "yellowA" {
                    ColorScreen(color: .yellow, title: "Yellow Screen C") {
                        // Optionally go back to root or push another screen
                        // For this example, we'll just stay on the last screen
                    }
                }
            }
        }
    }
}

// MARK: - Main TabView Container

struct PlatformTabView: View {
    var body: some View {
        TabView {
            Tab1ContentView()
                .tabItem {
                    Label("Tab 1", systemImage: "1.circle.fill")
                }

            Tab2ContentView()
                .tabItem {
                    Label("Tab 2", systemImage: "2.circle.fill")
                }
        }
    }
}

