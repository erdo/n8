import SwiftUI
import shared

struct ViewMilan: View {

    @EnvironmentN8<Location, TabHostId> private var n8
    @EnvironmentN8PreBackHandler private var preBackHandler
    
    let label: String = "Millan"
    let color: Color = Color.brown
    
    let location: Location.EuropeanLocationMilan
    let noMessage = "-"
    
    init(location: Location.EuropeanLocationMilan) {
        self.location = location
    }

    var body: some View {
        ZStack {
            color.ignoresSafeArea()
            VStack {
                Text("location: \(label) msg:\(location.message ?? noMessage)")
                    .font(.system(size: 35, weight: .bold))
                    .padding()
                
                Button(action: { n8.navigateTo(location: Location.EuropeanLocationParis.shared) }){
                    Text("Go to Paris")
                        .font(.system(size: 25, weight: .bold))
                }
                .padding()
                
                Button(action: {
                    // the reason we switch tabs first here is because in the event that
                    // the Bangkok location is not found on the back path, the location
                    // Bangkok will be created in place and in that case we want it created
                    // in the correct tabHost and on the correct tab
                    n8.switchTab(tabHostSpec: LocationsKt.tabHostSpecGlobalIos, tabIndex: 0)
                    preBackHandler.prepareBack {
                        n8.navigateBackTo(location:Location.Bangkok(message: "hello"))
                    }
                }){
                    Text("Send \"hello\" back to Bangkok")
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
