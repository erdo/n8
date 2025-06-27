import SwiftUI
import shared


struct N8TabHost<L: AnyObject & Hashable, T: AnyObject & Hashable>: View {

    private let navModel: NavigationModel<L, T>
    private let navState: NavigationState<L, T>
    private let wrappingTabHosts: [TabHostLocation<T>]
    private let depth: Int
    private let tabHostUiBuilder: (T, Int, () -> any View) -> any View
    private let currentLocationUiBuilder: (NavigationState<L, T>) -> any View

//    init (
//        @ViewBuilder tabHostUiBuilder: @escaping (T, Int, () -> any View) -> any View, // takes 1. TabHostId (T) (e.g. SettingsTab, AudioTab, HomeTabs etc) 2. the index of the selected tab 3. the content hosted by the tabHost -> returns the UI for the tabHost including placing the content in the correct place
//        @ViewBuilder currentLocationUiBuilder: @escaping (NavigationState<L, T>) -> any View
//    ){
//        self.tabHostUiBuilder = tabHostUiBuilder
//        self.currentLocationUiBuilder = currentLocationUiBuilder
//    }
    
    
    var body: some View {
//        if depth < wrappingTabHosts.count {
//            let tabHostLocation = wrappingTabHosts[depth]
//            
//            guard let tabHost = navState.locateTabHost(tabHostId:tabHostLocation.tabHostId!) else {
//                fatalError("This should never be null. The tabHostId '\(String(describing: tabHostLocation.tabHostId))' does not exist in the navigation graph yet. Check your code.")
//            }
//            
//            let nestedContent: () -> any View = {
//                if wrappingTabHosts.count > depth + 1 {
//                    AnyView(N8TabHost(
//                        navModel: navModel,
//                        navState: navState,
//                        wrappingTabHosts: wrappingTabHosts,
//                        depth: depth + 1, content: currentLocationUiBuilder)
//                    )
//                } else {
//                    AnyView(currentLocationUiBuilder())
//                }
//            }
//            
//            return nestedContent()
//           // tabHosts(tabHostLocation.tabHostId!, tabHost.tabHistory.last, nestedContent)
//        }
        Text("N8TabHost")
    }
}
