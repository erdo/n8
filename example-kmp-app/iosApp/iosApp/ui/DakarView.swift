
import SwiftUI
import shared

struct DakarView: View {

    private let navigationModel: NavigationModel<Location, KotlinUnit>

    init() {
        navigationModel = OG[NavigationModel<Location, KotlinUnit>.self]
    }
    
    var body: some View {
        
        VStack {
            Text("location: Dakar")
                .font(.system(size: 35, weight: .bold))
            
            NavigationLink {
                LAView()
            } label: {
                Text("Go to LA")
                    .font(.system(size: 25, weight: .bold))
            }
            .padding()
        }
        .padding()
        .background(Color.orange)
        .navigationTitle("Dakar")
        .navigationBarTitleDisplayMode(.inline)
    }
}

//struct ContentView_Previews: PreviewProvider {
//    static var previews: some View {
//        ContentView(counterModel: OG.shared[CounterModel.self])
//    }
//}
