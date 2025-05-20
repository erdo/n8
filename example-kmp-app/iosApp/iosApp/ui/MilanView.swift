import SwiftUI
import shared

struct MilanView: View {

    private let navigationModel: NavigationModel<Location, TabHost>

    init() {
        navigationModel = OG[NavigationModel<Location, TabHost>.self]
    }
    
    var body: some View {
        
        VStack {
            Text("location: Milan")
                .font(.system(size: 35, weight: .bold))
            
            Button(action: { navigationModel.navigateTo(location: Location.EuropeanLocationParis.shared) }){
                Text("Go to Paris")
                    .font(.system(size: 25, weight: .bold))
            }
            Button(action: { navigationModel.navigateTo(location: Location.EuropeanLocationLondon.shared) }){
                Text("Go to London")
                    .font(.system(size: 25, weight: .bold))
            }
            Button(action: { navigationModel.navigateBack() }){
                Text("Go back")
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
