
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
            AnyView(uiBuilder(n8ObservableObject.state))
                .navigationDestination(for: NavigationState<L, T>.self) { newState in
                    AnyView(uiBuilder(newState))
                }
        }
        .onReceive(n8ObservableObject.$state) { newState in // when n8 state changes
            Fore.companion.e(message:"(A) N8 state change newState:\(newState)")
            syncPath(with: newState)
        }
        .onChange(of: path) { newPath in // intercept when user swipes back
            Fore.companion.e(message: "(B) Path change (user went back?) newPath.count=\(newPath.count)")
            handlePathChange(newPath)
        }
    }
    
    
    private func syncPath(with state: NavigationState<L, T>) {
        
        Fore.companion.e(message: "Update path (currently:\(self.path.count)) to match state:\(state) state.canNavigateBack:\(state.canNavigateBack)")
        
        var newPath = NavigationPath()
        
        //TODO this matching needs to be more complex, we might need a peekBack function in n8...
        
        if state.canNavigateBack {
            newPath.append(state.comingFrom) //what we need is peekBack here, comingFrom is not our back destination when we are navigating back
        } else {
          //  newPath.append(state.currentLocation)
        }
        
//        var transaction = Transaction()
//        transaction.animation = .easeInOut(duration: 0.3)
//       
//        withTransaction(transaction) {
//           self.path = newPath
//        }
        
  //      withAnimation(.easeInOut) {
            self.path = newPath
  //      }
        
        Fore.companion.e(message: "Update path complete, count:\(self.path.count)")
    }
    
    
    private func handlePathChange(_ newPath: NavigationPath) {
        
        Fore.companion.e(message: "handlePathChange() oldPathCount:\(path.count) newPathCount:\(newPath.count), updating state to match if user went back")
        
        let oldCount = path.count
        let newCount = newPath.count

        //TODO improve this
        
        if newCount < oldCount {
            Fore.companion.e(message: "Confirmed: User navigated back")
            n8.navigateBack()
        } else {
            Fore.companion.e(message: "Confirmed: User did NOT navigate back")
            //  n8ObservableObject.foreModel.navigateTo() //but how would be prevent the observers being fired.... ok maybe this never happens, user should navigate with n8 only
        }
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            Fore.companion.e(message: "DONE waiting")
            self.n8.notifyObservers()
       }
    }
}
