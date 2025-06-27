
import SwiftUI
import shared

struct ViewPoland: View {

    @EnvironmentN8<Location, TabHostId> private var n8
    @EnvironmentN8PreBackHandler private var preBackHandler
    
    let label: String = "Poland"
    let color: Color = Color.orange

    var body: some View {
        ZStack {
            color.ignoresSafeArea()
            VStack {
                Text("location: \(label)")
                    .font(.system(size: 35, weight: .bold))
                    .padding()
                
                Button(action: { n8.navigateTo(location: Location.EuropeanLocationSpain.shared) }){
                    Text("Go to Spain")
                        .font(.system(size: 25, weight: .bold))
                }
                .padding()
                
                Button(action: { n8.navigateTo(location: Location.EuropeanLocationMilan(message: "hello from Poland")) }){
                    Text("Send hello from Poland to Milan")
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
