
import SwiftUI
import shared

struct ViewHome: View {
    
    @EnvironmentN8<Location, TabHostId> private var n8
    @EnvironmentN8PreBackHandler private var preBackHandler
    
    let label: String = "Home"
    let color: Color = Color.mint

    var body: some View {
        ZStack {
            color.ignoresSafeArea()
            VStack {
                Text("location: \(label)")
                    .font(.system(size: 35, weight: .bold))
                    .padding()
                
                Button(action: { n8.switchTab(tabHostSpec: LocationsKt.tabHostSpecGlobalIos) } ) {
                    Text("Go to Tabs")
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
