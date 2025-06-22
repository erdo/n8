
import SwiftUI
import shared


struct N8Host<L: AnyObject & Hashable, T: AnyObject & Hashable>: View {
        
    @ObservedObject private var pathController: N8PathController<L, T>
    @State private var navigationSource: NavigationSource = .none
    
    @ObservedObject private var n8ObservableObject: ForeObservableObject<NavigationState<L, T>>
    private let uiBuilder: (NavigationState<L, T>) -> any View
    private let n8: NavigationModel<L, T>
    
    init(
        n8: NavigationModel<L, T>,
        // uiBuilder takes NavigationState and then returns the UI (typically based on state.currentLocation)
        @ViewBuilder uiBuilder: @escaping (NavigationState<L, T>) -> any View
    ) {
        self.uiBuilder = uiBuilder
        self.n8ObservableObject = ForeObservableObject(foreModel: n8){ n8.state }
        self.n8 = n8
        self.pathController = N8PathController<L, T>(navigationModel:n8)
    }

    var body: some View {
        
        NavigationStack(path: $pathController.path) {
            AnyView(uiBuilder(NavigationState<L, T>(navigation:n8.state.homeNavigationSurrogate)))
                .navigationDestination(for: Int.self) { backCount in
                    if backCount == 0 {
                        AnyView(uiBuilder(n8.state))
                    } else if backCount == 1 {
                        AnyView(uiBuilder(NavigationState(navigation:n8.state.peekBack!)))
                    } else if backCount == 2 {
                        AnyView(uiBuilder(NavigationState(navigation:NavigationState(navigation:n8.state.peekBack!).peekBack!)))
                    }
                }
        }
        .onReceive(n8ObservableObject.$state) { newState in // for when n8 state changes
            stateChanged(to: newState)
        }
        .onChange(of: pathController.path) { newPath in // for when user swipes back or uses ios navigation
            pathChanged(to: newPath)
        }
    }
    
    private func stateChanged(to newState: NavigationState<L, T>) {
        
        Fore.companion.e(message: "stateChanged() navigationSource:\(navigationSource) backsToExit:\(newState.backsToExit)")

        navigationSource = .n8
        pathController.syncPathWithN8()
    }
    
    private func pathChanged(to newPath: NavigationPath) {
        
        Fore.companion.e(message: "pathChanged() navigationSource:\(navigationSource)")
        
        if (navigationSource == .n8) {
            navigationSource = .none
        } else {
            navigationSource = .path
            n8.navigateBack()
        }
    }
    
    enum NavigationSource {
        case none
        case path
        case n8
    }
}
