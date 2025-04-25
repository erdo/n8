
import SwiftUI
import shared

struct LAView: View {

    private let navigationModel: NavigationModel<Location, KotlinUnit>

    init() {
        navigationModel = OG[NavigationModel<Location, KotlinUnit>.self]
    }
    
    var body: some View {
        VStack {
            Text("location: LA")
                .font(.system(size: 35, weight: .bold))
            
            Button(action: { navigationModel.navigateBack() }) { // TH: this will do nothing, you should implement a pop, if the navigationModel implements a stack data structure
                Text("Go back")
                    .font(.system(size: 25, weight: .bold))
                
            }
        }
        .padding()
        .background(Color.indigo)
        .navigationTitle("LA")
        .navigationBarTitleDisplayMode(.inline)
    }
}

//struct ContentView_Previews: PreviewProvider {
//    static var previews: some View {
//        ContentView(counterModel: OG.shared[CounterModel.self])
//    }
//}
