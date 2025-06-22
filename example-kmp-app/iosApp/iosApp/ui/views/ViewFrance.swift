import SwiftUI
import shared

struct ViewFrance: View {
    
    
    @EnvironmentN8<Location, TabHostId> private var n8
    @EnvironmentN8PreBackHandler private var preBackHandler
    
    let label: String = "France"
    let color: Color = Color.teal

    var body: some View {
        ZStack {
            color.ignoresSafeArea()
            VStack {
                Text("location: \(label)")
                    .font(.system(size: 35, weight: .bold))
                    .padding()
                
                Button(action: { n8.navigateTo(location: Location.EuropeanLocationPoland.shared) }){
                    Text("Go to Poland")
                        .font(.system(size: 25, weight: .bold))
                }
                .padding()
                
                Button(action: {
                    // the reason we switch tabs first here is because in the event that
                    // the London location is not found on the back path, the location
                    // London will be created in place and in that case we want it created
                    // in the correct tabHost and on the correct tab
                    n8.switchTab(tabHostSpec: LocationsKt.tabHostSpecEuropeIos, tabIndex: 0)
                    preBackHandler.prepareBack {
                        n8.navigateBackTo(location: Location.EuropeanLocationLondon.shared)
                    }
                }){
                    Text("City > London")
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
