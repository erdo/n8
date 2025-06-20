
import SwiftUI
import shared

struct ViewDakar: View {

    private let navigationModel: NavigationModel<Location, TabHostId>

    init() {
        navigationModel = OG[NavigationModel<Location, TabHostId>.self]
    }
    
    var body: some View {
        
        VStack {
            Text("location: Dakar")
                .font(.system(size: 35, weight: .bold))
            
            Button(action: { navigationModel.navigateTo(location: Location.LA.shared) }){
                Text("Go to LA")
                    .font(.system(size: 25, weight: .bold))
            }

            Button(action: { navigationModel.navigateBack() }){
                Text("Go back")
                    .font(.system(size: 25, weight: .bold))
            }
            .padding()
        }
        .padding()
        .background(Color.orange)
        .navigationTitle("Dakar")
        .navigationBarTitleDisplayMode(.inline)
    }
}
