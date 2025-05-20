
import SwiftUI
import shared

struct LAView: View {

    private let navigationModel: NavigationModel<Location, TabHost>

    init() {
        navigationModel = OG[NavigationModel<Location, TabHost>.self]
    }
    
    var body: some View {
        VStack {
            Text("location: LA")
                .font(.system(size: 35, weight: .bold))
            
            Button(action: { navigationModel.switchTab(tabHostSpec: LocationsKt.tabHostSpec1)}){
                Text("Go to European Tabs")
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
