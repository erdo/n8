import SwiftUI
import shared

struct ViewParis: View {

    @EnvironmentN8<Location, TabHostId> private var n8
    @EnvironmentN8PreBackHandler private var preBackHandler
    
    let label: String = "Paris"
    let color: Color = Color.red

    var body: some View {
        ZStack {
            color.ignoresSafeArea()
            VStack {
                Text("location: \(label)")
                    .font(.system(size: 35, weight: .bold))
                    .padding()
                
                Button(action: { n8.navigateTo(location: Location.EuropeanLocationLondon.shared) }){
                    Text("Go to London")
                        .font(.system(size: 25, weight: .bold))
                }
                .padding()
                
                Button(action: {
                    n8.switchTab(tabHostSpec: LocationsKt.tabHostSpecGlobalIos, tabIndex: 0)
                    n8.navigateTo(location:Location.Dakar.shared)
                }){
                    Text("Global Tab & Goto Dakar")
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
