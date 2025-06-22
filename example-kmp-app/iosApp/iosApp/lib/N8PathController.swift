import SwiftUI
import shared


/**
 * NavigationPath controller. SwiftUI runs its navigation change animations based on a diff of the old path size and the new path size. If the path increases in size the system assumes we
 * want a "push" animation, if it decreases in size it assumes a "pop" animation is requred. If the path size doesn't change, no animation is run.
 *
 * We manage a path of magic Ints here, the path does not contain locations. The ints mean this: 0 = current location, 1 = previous location, 2 = the previous location's previous location 9 = buffer character
 *
 * Because our path is based on the state of n8 (the back path of which can be arbitrarily re-written - unlike NavigationPath) we maintain a NavigationPath whose topmost 3 items match
 * the most recent 3 locations present in the n8 back path (or fewer if the back path has less than 3 items in it) at all times.
 *
 * In order to receive the correct push animation when the user navigates forward beyond 3 locations, we add a buffer item to the path at the bottom. We keep the number of buffered items in line with the backsToExit
 * number from n8 so that the correct animation will be triggered according to the path count change
 *
 * Valid paths are:
 *
 * [] - nothing in the path, only the home location will be on screen
 * [0] - the current location is in the path and on screen, the previous location (home) is accessible by navigating back
 * [1, 0] - the home location is two swipes back away
 * [2, 1, 0] - the home location is three swipes away
 * [9, 9, 9, 9, 9,... 2, 1, 0] - we could be anywhere else in the navigation graph (we only keep track of the last 3 locations), in this example we have 5 buffer characters for performing 5 pop
 * animations before we run out
 */
class N8PathController<L: AnyObject & Hashable, T: AnyObject & Hashable>: ObservableObject, BackPreHandler {

    @Published var path = NavigationPath()
    private let n8: NavigationModel<L, T>
    private let bufferValue = 9
    var loggingArray : [Int] = [0]
    
    private(set) var preparing: Bool = false
    
    init (navigationModel: NavigationModel<L, T>) {
        self.n8 = navigationModel
    }
    
    // use this to wrap any form of back navigation you want to perform to ensure the system
    // starts a valid back animation, _before_ our navigation state is changed, you will
    // see animation jank otherwise
    func prepareBack(action: @escaping () -> Void){
        preparing = true
        path.removeLast()
        DispatchQueue.main.async {
            action()
            self.preparing = false
        }
    }

    func syncPathWithN8(){
        
        if n8.state.backsToExit == 1 {
            // at home location, in iOS we depend on and empty NavigationPath and the root view, set above
            Fore.companion.e(message: "N8 updating path: Home location - so path should be empty")
            applyPath([])
            
        } else if n8.state.backsToExit == 2 {
            // the path should contain only one item representing the current location (the home location is the root view in iOS)
            Fore.companion.e(message: "N8 updating path: Second location - so path represents the current state only [0]")
            applyPath([0])
        
        } else if n8.state.backsToExit == 3 {
            // the path should contain 2 items representing the back location and the current location (with the home location in the root view)
            Fore.companion.e(message: "N8 updating path: [1] = peekBack state, [0] = current state")
            applyPath([1, 0])
            
        } else {
            // the path should contain 3 items representing the back-back location, the back location, and the current location
            // previous navigation items in the back path still exist in memory managed by N8
            Fore.companion.e(message: "N8 updating path: [2] = peekBack.peekBack state, [1] = peekBack state, [0] = current state")
            applyPath([2, 1, 0])
        }
    }
    
    // operationType is ignored for any path.count < 3
    private func applyPath(_ newPath: [Int]) {
        
        var tempPath = NavigationPath()
        var tempArray : [Int] = []
        
        if n8.state.backsToExit > 3 { // so we need to buffer the path first
            let newBufferSize = n8.state.backsToExit - 4
            if newBufferSize > 0 {
                (0..<newBufferSize).forEach { _ in
                    tempPath.append(bufferValue)
                    tempArray.append(bufferValue)
                }
            }
        }
        
        newPath.forEach {
            tempPath.append($0)
            tempArray.append($0)
        }
        
        Fore.companion.e(message:"new buffered path:\(tempArray)")
                                 
        path = tempPath
        loggingArray = tempArray
    }
}

protocol BackPreHandler {
    var preparing: Bool { get }
    func prepareBack(action: @escaping () -> Void)
}
