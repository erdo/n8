
import SwiftUI
import shared

struct ViewSpain: View {

    @EnvironmentN8<Location, TabHostId> private var n8
    @EnvironmentN8PreBackHandler private var preBackHandler
    
    let label: String = "Spain"
    let color: Color = Color.yellow

    var body: some View {
        ZStack {
            color.ignoresSafeArea()
            VStack {
                Text("location: \(label)")
                    .font(.system(size: 35, weight: .bold))
                    .padding()
                
                Button(action: { n8.navigateTo(location: Location.EuropeanLocationFrance.shared) }){
                    Text("Go to France")
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
