import SwiftUI
import shared

struct ViewMilan: View {

    private let navigationModel: NavigationModel<Location, TabHostId>

    init() {
        navigationModel = OG[NavigationModel<Location, TabHostId>.self]
    }
    
    var body: some View {
        
        VStack {
            Text("location: Milan")
                .font(.system(size: 35, weight: .bold))
            
            Button(action: { navigationModel.navigateTo(location: Location.EuropeanLocationParis.shared) }){
                Text("Go to Paris")
                    .font(.system(size: 25, weight: .bold))
            }.padding()
            Button(action: { navigationModel.navigateTo(location: Location.EuropeanLocationLondon.shared) }){
                Text("Go to London")
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
        .background(Color.brown)
        .navigationTitle("Milan")
        .navigationBarTitleDisplayMode(.inline)
    }
}
