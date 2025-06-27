import SwiftUI
import shared

struct RootTabHostView: View {
    
    let n8: NavigationModel<Location, TabHostId>
    let wrappingTabHosts: [TabHostLocation<TabHostId>]
    let tabHostLocation: TabHostLocation<TabHostId>
    let tabHost: NavigationTabHost<Location, TabHostId>
    let depth: Int
    let content: () -> AnyView
    
    init(
        n8: NavigationModel<Location, TabHostId>,
        depth: Int = 0,
        content: @escaping () -> AnyView
    ) {
        self.n8 = n8
        self.wrappingTabHosts = n8.state.hostedBy
        self.tabHostLocation = wrappingTabHosts[depth]
        self.tabHost = n8.state.locateTabHost(tabHostId:tabHostLocation.tabHostId!)!
        self.depth = depth
        self.content = content
    }
    
    var body: some View {

        let isLast = wrappingTabHosts.count <= depth + 1
        let nestedContent: () -> AnyView = isLast ? content : {
            AnyView(
                RootTabHostView(
                    n8: n8,
                    depth: depth + 1,
                    content: content
                )
            )
        }
        
        switch tabHostLocation.tabHostId {
            case is TabHostId.EuropeTabHost:
                TabHostEurope(selectedTabIndex: Int(truncating:tabHost.tabHistory.last!), content: nestedContent)
            case is TabHostId.GlobalTabHost:
                TabHostGlobal(selectedTabIndex: Int(truncating:tabHost.tabHistory.last!), content: nestedContent)
            default: Text("Unknown tabhost \(tabHostLocation.tabHostId!)")
        }
    }
}
