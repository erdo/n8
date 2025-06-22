import SwiftUI
import shared

struct ViewNewYork: View {

    @EnvironmentN8<Location, TabHostId> private var n8
    @EnvironmentN8PreBackHandler private var preBackHandler
    
    let label: String = "NY"
    let color: Color = Color.yellow

    var body: some View {
        ZStack {
            color.ignoresSafeArea()
            VStack {
                Text("location: \(label)")
                    .font(.system(size: 35, weight: .bold))
                    .padding()
                
                Button(action: { n8.navigateTo(location: Location.Bangkok(message: nil)) }){
                    Text("Go to Bangkok")
                        .font(.system(size: 25, weight: .bold))
                }
                .padding()
                
                Button(action: { n8.switchTab(tabHostSpec: LocationsKt.tabHostSpecGlobalIos, tabIndex: 1) }){
                    Text("Switch to Europe")
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
