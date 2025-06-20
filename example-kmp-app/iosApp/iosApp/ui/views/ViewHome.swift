
import SwiftUI
import shared

struct ViewHome: View {
    
    private let navigationModel: NavigationModel<Location, TabHostId>
    
    init() {
        navigationModel = OG[NavigationModel<Location, TabHostId>.self]
    }
    
    var body: some View {
        VStack {
            Text("location: Home")
                .font(.system(size: 35, weight: .bold))
            Button(action: { navigationModel.navigateTo(location: Location.Bangkok(message: nil)) }){
                Text("Go to Bangkok")
                    .font(.system(size: 25, weight: .bold))
            }
            .padding()
        }
        .padding()
        .background(Color.pink)
        .navigationTitle("Home")
        .navigationBarTitleDisplayMode(.inline)
    }
}
