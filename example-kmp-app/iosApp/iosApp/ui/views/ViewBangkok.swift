
import SwiftUI
import shared

struct ViewBangkok: View {
    
    @EnvironmentN8<Location, TabHostId> private var n8
    @EnvironmentN8PreBackHandler private var preBackHandler
    
    let label: String = "Bangkok"
    let color: Color = Color.green

    var body: some View {
        ZStack {
            color.ignoresSafeArea()
            VStack {
                Text("location: \(label)")
                    .font(.system(size: 35, weight: .bold))
                    .padding()
                
                Button(action: { n8.navigateTo(location: Location.Dakar.shared) }){
                    Text("Go to Dakar")
                        .font(.system(size: 25, weight: .bold))
                }
                .padding()

                Button(action: {
                    preBackHandler.prepareBack {
                        n8.navigateBack()
                    }
                }){
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
