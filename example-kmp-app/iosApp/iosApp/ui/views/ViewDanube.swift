
import SwiftUI
import shared

struct ViewDanube: View {

    private let navigationModel: NavigationModel<Location, TabHostId>

    init() {
        navigationModel = OG[NavigationModel<Location, TabHostId>.self]
    }
    
    var body: some View {
        
        VStack {
            Text("location: Danube")
                .font(.system(size: 35, weight: .bold))
            
            Button(action: { navigationModel.navigateTo(location: Location.EuropeanLocationMilan(message: nil)) }){
                Text("Go to Milan")
                    .font(.system(size: 25, weight: .bold))
            }
            Button(action: { navigationModel.navigateBack() }){
                Text("Go back")
                    .font(.system(size: 25, weight: .bold))
            }
            .padding()
            Button(action: { navigationModel.navigateBack(times: 2) }){
                Text("Go back x2")
                    .font(.system(size: 25, weight: .bold))
            }
            .padding()
        }
        .padding()
        .background(Color.yellow)
        .navigationTitle("Danube")
        .navigationBarTitleDisplayMode(.inline)
    }
}
