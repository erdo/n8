
import SwiftUI
import shared

struct ViewLA: View {

    private let navigationModel: NavigationModel<Location, TabHostId>

    init() {
        navigationModel = OG[NavigationModel<Location, TabHostId>.self]
    }
    
    var body: some View {
        VStack {
            Text("location: LA")
                .font(.system(size: 35, weight: .bold))
            
            Button(action: { navigationModel.switchTab(tabHostSpec: LocationsKt.tabHostSpecEurope)}){
                Text("Go to European Tabs")
                    .font(.system(size: 25, weight: .bold))
            }
            .padding()
            
            Button(action: { navigationModel.navigateTo(location: Location.EuropeanLocationDanube.shared) }){
                Text("Go to Danube")
                    .font(.system(size: 25, weight: .bold))
            }
            
            Button(action: { navigationModel.navigateBack() }){
                Text("Go back")
                    .font(.system(size: 25, weight: .bold))
            }
            .padding()
        }
        .padding()
        .background(Color.mint)
        .navigationTitle("LA")
        .navigationBarTitleDisplayMode(.inline)
    }
}
