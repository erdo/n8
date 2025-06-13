import SwiftUI
import shared

struct LondonView: View {

    private let navigationModel: NavigationModel<Location, TabHost>

    init() {
        navigationModel = OG[NavigationModel<Location, TabHost>.self]
    }
    
    var body: some View {
        
        VStack {
            Text("location: London")
                .font(.system(size: 35, weight: .bold))
            
            Button(action: { navigationModel.navigateTo(location: Location.EuropeanLocationMilan.shared) }){
                Text("Go to Milan")
                    .font(.system(size: 25, weight: .bold))
            }
            Button(action: { navigationModel.navigateBack() }){
                Text("Go back")
                    .font(.system(size: 25, weight: .bold))
            }
            .padding()
        }
        .padding()
        .background(Color.yellow)
        .navigationTitle("London")
        .navigationBarTitleDisplayMode(.inline)
    }
}
