
import SwiftUI
import shared

struct BangkokView: View {
    
    private let navigationModel: NavigationModel<Location, KotlinUnit>
    
    init() {
        navigationModel = OG[NavigationModel<Location, KotlinUnit>.self]
    }
    
    var body: some View {
        VStack {
            Text("location: Bangkok")
                .font(.system(size: 35, weight: .bold))
            Button(action: { navigationModel.navigateTo(location: Location.Dakar.shared) }){
                Text("Go to Dakar")
                    .font(.system(size: 25, weight: .bold))
            }
            .padding()
        }
        .padding()
        .background(Color.green)
        .navigationTitle("Bangkok")
        .navigationBarTitleDisplayMode(.inline)
    }
}
