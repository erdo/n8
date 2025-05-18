
import SwiftUI
import shared


/**
 NB: mostly, if you create a sealed class in shared kotlin code to use as your Location (L) class, it's going to comply with Hashable once it gets to the iOS side. But
 not always (for example if your sealed class has list data in it). In that case you will need to make it conform to Hashable by implementing the
 hash(into:) and == operators in swift before using it here
 */
struct N8Host<L: AnyObject & Hashable, T: AnyObject & Hashable>: View {
    
    @State private var path = NavigationPath()
    @State private var pathChangePending: Int = 0
    @State private var stateChangePending: Int = 0
    @ObservedObject private var n8ObservableObject: ForeObservableObject<NavigationState<L, T>>
    private let uiBuilder: (NavigationState<L, T>) -> any View
    private let n8: NavigationModel<L, T>
    
    init(
        n8: NavigationModel<L, T>,
        @ViewBuilder uiBuilder: @escaping (NavigationState<L, T>) -> any View
    ) {
        self.uiBuilder = uiBuilder
        self.n8ObservableObject = ForeObservableObject(foreModel: n8){ n8.state }
        self.n8 = n8
    }

    var body: some View {
        NavigationStack(path: $path) {
            AnyView(uiBuilder(NavigationState<L, T>(navigation:n8.state.homeNavigationSurrogate)))
                .navigationDestination(for: NavigationState<L, T>.self) { newState in
                    AnyView(uiBuilder(newState))
                }
        }
        .onReceive(n8ObservableObject.$state) { newState in // for when n8 state changes
            updatePath(with: newState)
        }
        .onChange(of: path) { newPath in // for when user swipes back or uses ios navigation
            updateState(with: newPath)
        }
    }
    
    
    private func updatePath(with newState: NavigationState<L, T>) {
        if (stateChangePending == 0) {
            var newPath = NavigationPath()
            if newState.backsToExit == 1 {
                // at home location, in iOS we depend on the root view, set above
                Fore.companion.d(message: "N8 updating path: Home location so path empty")
            } else if newState.backsToExit == 2 {
                // we add the _second_ (current) location to the path only (the home location is the root view in iOS)
                Fore.companion.d(message: "N8 updating path: Second location so path contains current state only")
                newPath.append(newState)
            } else if newState.backsToExit > 2 {
                // we add the peekBack location and then the current location to the path
                // previous navigation items in the back path still exist in memory managed by N8
                Fore.companion.d(message: "N8 updating path: [0] = peekBack state, [1] = current state")
                if let peekBack = newState.peekBack {
                    newPath.append(NavigationState<L, T>(navigation: peekBack))
                    newPath.append(newState)
                }
            }
            if (path != newPath){
                pathChangePending += 1
            }
            path = newPath
        } else {
            Fore.companion.d(message: "N8 updating path: ignoring, state was changed by N8")
            stateChangePending -= 1
        }
    }
    
    
    private func updateState(with newPath: NavigationPath) {
        if (pathChangePending == 0) {
            Fore.companion.d(message: "N8 updating state: system back encountered")
            stateChangePending += 1
            n8.navigateBack()
        } else {
            Fore.companion.d(message: "N8 updating state: ignoring, path was changed by N8")
            pathChangePending -= 1
        }
    }
}
