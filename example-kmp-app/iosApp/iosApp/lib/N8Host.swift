
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
        self.pathController = N8PathController<L, T>(navigationModel: n8)
    }

    var body: some View {
        
        NavigationStack(path: $pathController.path) {
            AnyView(uiBuilder(NavigationState<L, T>(navigation:n8.state.homeNavigationSurrogate)))
                .navigationDestination(for: Int.self) { backCount in
                    if backCount == 0 {
                        AnyView(uiBuilder(n8.state))
                    } else if backCount == 1 {
                        if let peekBack = n8.state.peekBack {
                            AnyView(uiBuilder(NavigationState(navigation: peekBack)))
                        }
                    } else if backCount == 2 {
                        if let peekBack = n8.state.peekBack,
                           let peekBackPeekBack = NavigationState(navigation: peekBack).peekBack {
                            AnyView(uiBuilder(NavigationState(navigation: peekBackPeekBack)))
                        }
                    }
                }
        }
        .transformEnvironment(\.self) { environment in
            environment[NavigationModelKey<L, T>.self] = n8
            environment[BackPreHandlerKey.self] = pathController as any BackPreHandler
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
            if !pathController.preparing { // if we are not in the middle of preparing, that means this path change has come from the system
                n8.navigateBack()
            }
        }
    }
    
    enum NavigationSource {
        case none
        case path
        case n8
    }
}


// MARK: - @EnvironmentN8<Location, TabHostId> private var n8

struct NavigationModelKey<L: AnyObject & Hashable, T: AnyObject & Hashable>: EnvironmentKey {
    static var defaultValue: NavigationModel<L, T>? { nil }
}

extension EnvironmentValues {
    func n8<L: AnyObject & Hashable, T: AnyObject & Hashable>() -> NavigationModel<L, T> {
        guard let model = self[NavigationModelKey<L, T>.self] else {
            fatalError("n8 not found in environment. You can only call this from a child of the N8Host hierarchy")
        }
        return model
    }
}

@propertyWrapper
struct EnvironmentN8<L: AnyObject & Hashable, T: AnyObject & Hashable>: DynamicProperty {
    @Environment(\.self) private var environment
    
    var wrappedValue: NavigationModel<L, T> {
        environment.n8()
    }
}


// MARK: - @EnvironmentN8PreBackHandler private var preBackHandler

struct BackPreHandlerKey: EnvironmentKey {
    static let defaultValue: BackPreHandler = DefaultBackPreHandler()
}

extension EnvironmentValues {
    var backPreHandler: BackPreHandler {
        get { self[BackPreHandlerKey.self] }
    }
}

@propertyWrapper
struct EnvironmentN8PreBackHandler: DynamicProperty {
    @Environment(\.backPreHandler) private var _backPreHandler: BackPreHandler

    var wrappedValue: BackPreHandler {
        _backPreHandler
    }
}

struct DefaultBackPreHandler: BackPreHandler {
    var preparing: Bool = false
    
    func prepareBack(action: @escaping () -> Void) {
        fatalError("preBackHandler not found in environment. You can only call this from a child of the N8Host hierarchy")
    }
}
