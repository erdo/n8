
import SwiftUI
import shared

struct ViewWelcome: View {
    
    private let navigationModel: NavigationModel<Location, TabHostId>
    
    init() {
        navigationModel = OG[NavigationModel<Location, TabHostId>.self]
    }
    
    var body: some View {
        VStack {
            Text("location: One time Welcome screen")
                .font(.system(size: 35, weight: .bold))
            Button(action: { navigationModel.navigateTo(location: Location.Home.shared) }){
                Text("Go to Home")
                    .font(.system(size: 25, weight: .bold))
            }
            .padding()
        }
        .padding()
        .background(Color.green)
        .navigationTitle("Welcome")
        .navigationBarTitleDisplayMode(.inline)
    }
}
