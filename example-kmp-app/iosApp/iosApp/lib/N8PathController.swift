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
 * In order to receive the correct push animation when the user navigates forward beyond 3 locations, we add a buffer item to the path at the bottom (so that the path size increases, triggering
 * the push animation). When the user navigates back, we remove a buffer item (so that the size of the path decreases, and the pop animation is displayed). When a n8 user switches tab, we
 * request no animation (and the length of the path will remain the same)
 *
 *Valid paths are:
 *
 * [] - nothing in the path, only the home location will be on screen
 * [0] - the current location is in the path and on screen, the previous location (home) is accessible by navigating back
 * [1, 0] - the home location is two swipes back away
 * [2, 1, 0] - the home location is three swipes away
 * [9, 9, 9, 9, 9,... 2, 1, 0] - we could be anywhere else in the navigation graph (we only keep track of the last 3 locations), in this example we have 5 buffer characters for performing 5 pop
 * animations before we run out
 */
class N8PathController: ObservableObject {

    var testArray : [Int] = [0]
    
    @Published var path = NavigationPath()
    private let backBufferSize: Int32
    private let bufferValue = 9
    
    init<L, T>(withInitialState state: NavigationState<L, T>) {
        self.backBufferSize = state.backsToExit - 1
    }
    
    init(_ backBufferSize: Int) {
        self.backBufferSize = Int32(backBufferSize)
    }
    
    func setPathForNoItems() { // the user is on the home screen
        applyPath([])
    }
    
    func setPathForOneItem() { // the user is at the second location, the home screen is one back swipe away
        applyPath([0])
    }
    
    func setPathForTwoItems() { // the home screen is two back swipes away
        applyPath([1, 0])
    }
    
    func setPathForThreeOrMoreItems(with operationType: OperationType) { // the user could be anywhere else
        applyPath([2, 1, 0], with: operationType)
    }
    
    // operationType is ignored for any path.count < 3
    private func applyPath(_ newPath: [Int], with operationType: OperationType = OperationType.None()) {
        
        var tempPath = NavigationPath()
        var tempArray : [Int] = []
        
        if newPath.count > 2 { // so we need to use back buffer

            if (path.count < 3){ // first time moving to three or more items, take this opportunity to fill up the buffer
                (0..<backBufferSize).forEach { _ in
                    tempPath.append(bufferValue)
                    tempArray.append(bufferValue)
                }
            } else {
                
                let newBufferSize = path.count - newPath.count +
                (operationType is OperationType.Push ? 1 : operationType is OperationType.Pop ? -1 : 0)
                
                Fore.companion.e(message:"newBufferSize:\(newBufferSize)")
                
                if newBufferSize > 0 {
                    (0..<newBufferSize).forEach { _ in
                        tempPath.append(bufferValue)
                        tempArray.append(bufferValue)
                    }
                }
            }
        }
        
        newPath.forEach {
            tempPath.append($0)
            tempArray.append($0)
        }
        
        Fore.companion.e(message:"path.count:\(path.count) newPath.count(no buffer):\(newPath.count) tempPath.count(w buffer):\(tempPath.count)")
        Fore.companion.e(message:"path:\(tempArray)")
                                 
        path = tempPath
        testArray = tempArray
    }
}
