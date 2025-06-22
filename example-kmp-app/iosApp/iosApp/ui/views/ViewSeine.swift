
import SwiftUI
import shared

struct ViewSeine: View {
    
    @EnvironmentN8<Location, TabHostId> private var n8
    @EnvironmentN8PreBackHandler private var preBackHandler
    
    let label: String = "Seine"
    let color: Color = Color.gray

    var body: some View {
        ZStack {
            color.ignoresSafeArea()
            VStack {
                Text("location: \(label)")
                    .font(.system(size: 35, weight: .bold))
                    .padding()
                
                Button(action: { n8.navigateTo(location: Location.EuropeanLocationThames.shared) }){
                    Text("Go to Thames")
                        .font(.system(size: 25, weight: .bold))
                }
                .padding()
                
                Button(action: {
                    n8.switchTab(tabHostSpec: LocationsKt.tabHostSpecEuropeIos, tabIndex: 0)
                }){
                    Text("European Countries")
                        .font(.system(size: 25, weight: .bold))
                }
                .padding()
                
                Button(action: { preBackHandler.prepareBack {
                    n8.navigateBack()
                } }){
                    Text("Go back")
                        .font(.system(size: 25, weight: .bold))
                }
                .padding()
            }
            .padding()
            .navigationTitle(label)
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
