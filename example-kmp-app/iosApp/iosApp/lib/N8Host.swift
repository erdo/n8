
import SwiftUI
import shared


/**
 NB: mostly, if you create a sealed class in shared kotlin code to use as your Location (L) class, it's going to comply with Hashable once it gets to the iOS side. But
 not always (for example if your sealed class has list data in it). In that case you will need to make it conform to Hashable by implementing the
 hash(into:) and == operators in swift before using it here
 */
struct N8Host<L: AnyObject & Hashable, T: AnyObject & Hashable>: View {
    
    @State private var path = NavigationPath()
    @ObservedObject private var n8ObservableObject: ForeObservableObject<NavigationState<L, T>>
    private let uiBuilder: (NavigationState<L, T>) -> any View
    
    init(
        n8: NavigationModel<L, T>,
        @ViewBuilder uiBuilder: @escaping (NavigationState<L, T>) -> any View
    ) {
        self.uiBuilder = uiBuilder
        self.n8ObservableObject = ForeObservableObject(foreModel: n8){ n8.state }
    }

    var body: some View {
        NavigationStack(path: $path) {
            AnyView(uiBuilder(n8ObservableObject.state))
                .navigationDestination(for: NavigationState<L, T>.self) { newState in
                 //   Fore.companion.e(message: "navigationDestination() newState=\(newState)")
                    AnyView(uiBuilder(newState))
                }
        }
        .onReceive(n8ObservableObject.$state) { newState in // when n8 state changes
            Fore.companion.e(message:" onReceive() newState:\(newState)")
            syncPath(with: newState)
        }
        .onChange(of: path) { newPath in // for when user swipes back
            Fore.companion.e(message: "onChange(path) newPath.count=\(newPath.count)")
            handlePathChange(newPath)
        }
    }
    
    private func syncPath(with state: NavigationState<L, T>) {
        
        Fore.companion.e(message: "syncPath() state:\(state)")
        
        var newPath = NavigationPath()
        
        //TODO this matching needs to be more complex, we might need a peekBack function in n8...
        
        if state.canNavigateBack {
            newPath.append(state.comingFrom)
            newPath.append(state.currentLocation)
        } else {
            newPath.append(state.currentLocation)
        }
        
  //      withAnimation(.easeInOut) {
            self.path = newPath
  //      }
    }
    
    
    private func handlePathChange(_ newPath: NavigationPath) {
        
        Fore.companion.e(message: "handlePathChange() newPath:\(newPath)")
        
        let oldCount = path.count
        let newCount = newPath.count

        //TODO improve this
        
        if newCount < oldCount {
            Fore.companion.e(message: "User navigated back")
          //  n8ObservableObject.foreModel.navigateBack()
        }
    }
}
