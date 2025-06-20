
import SwiftUI
import shared


struct N8Host<L: AnyObject & Hashable, T: AnyObject & Hashable>: View {
        
    @ObservedObject private var pathController: N8PathController
    @State private var navigationSource: NavigationSource = .none
    
    @ObservedObject private var n8ObservableObject: ForeObservableObject<NavigationState<L, T>>
    private let uiBuilder: (NavigationState<L, T>) -> any View
    private let n8: NavigationModel<L, T>
    

    init(
        n8: NavigationModel<L, T>,
        @ViewBuilder uiBuilder: @escaping (NavigationState<L, T>) -> any View // takes NavigationState and then returns the UI (typically based on state.currentLocation)
    ) {
        self.uiBuilder = uiBuilder
        self.n8ObservableObject = ForeObservableObject(foreModel: n8){ n8.state }
        self.n8 = n8
        self.pathController = N8PathController(withInitialState: n8.state)
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
        
        Fore.companion.e(message: "stateChanged() lastOp:\(newState.lastOperationType) backsToExit:\(newState.backsToExit) navigationSource:\(navigationSource)")
        
        var originallyFromPath = true
        if (navigationSource != .path){
            originallyFromPath = false
            Fore.companion.e(message: "---- N8 CHANGED FIRST ----")
        }
            
        navigationSource = .n8
        
        if newState.backsToExit == 1 {
            // at home location, in iOS we depend on and empty NavigationPath and the root view, set above
            Fore.companion.e(message: "N8 updating path: Home location - so path should be empty")
            pathController.setPathForNoItems()
            
        } else if newState.backsToExit == 2 {
            // the path should contain only one item representing the current location (the home location is the root view in iOS)
            Fore.companion.e(message: "N8 updating path: Second location - so path represents the current state only [0]")
            pathController.setPathForOneItem()
        
        } else if newState.backsToExit == 3 {
            // the path should contain 2 items representing the back location and the current location (with the home location in the root view)
            Fore.companion.e(message: "N8 updating path: [1] = peekBack state, [0] = current state")
            pathController.setPathForTwoItems()
            
        } else {
            // the path should contain 3 items representing the back-back location, the back location, and the current location
            // previous navigation items in the back path still exist in memory managed by N8
            Fore.companion.e(message: "N8 updating path: [2] = peekBack.peekBack state, [1] = peekBack state, [0] = current state")
            pathController.setPathForThreeOrMoreItems(with: originallyFromPath ? OperationType.None() : newState.lastOperationType)
        }
    }
    
    private func pathChanged(to newPath: NavigationPath) {
        
        Fore.companion.e(message: "pathChanged() navigationSource:\(navigationSource)")
        
        if (navigationSource == .n8) {
            navigationSource = .none
            
        } else {
            
            Fore.companion.e(message: "---- PATH CHANGED FIRST ----")
            
            navigationSource = .path
            
            Fore.companion.e(message: "N8 about to update state: system back encountered (new PATH size:\(newPath.count))")
            n8.navigateBack()
        }
    }
    
    enum NavigationSource {
        case none
        case path
        case n8
    }
}
