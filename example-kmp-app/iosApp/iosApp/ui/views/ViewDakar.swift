
import SwiftUI
import shared

struct ViewDakar: View {
    
    @EnvironmentN8<Location, TabHostId> private var n8
    @EnvironmentN8PreBackHandler private var preBackHandler
    
    let label: String = "Dakar"
    let color: Color = Color.orange

    var body: some View {
        ZStack {
            color.ignoresSafeArea()
            VStack {
                Text("location: \(label)")
                    .font(.system(size: 35, weight: .bold))
                    .padding()
                
                Button(action: { n8.navigateTo(location: Location.LA.shared) }){
                    Text("Go to LA")
                        .font(.system(size: 25, weight: .bold))
                }
                .padding()
                
                Button(action: { preBackHandler.prepareBack {
                    n8.navigateBackTo(location: Location.NewYork.shared)
                } }){
                    Text("Back to New York")
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
