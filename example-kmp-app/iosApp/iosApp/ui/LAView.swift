
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

            Button(action: { navigationModel.navigateBack() }) {
                Text("Go back")
                    .font(.system(size: 25, weight: .bold))
            }
        }.padding().background(Color.indigo)
    }
}

//struct ContentView_Previews: PreviewProvider {
//    static var previews: some View {
//        ContentView(counterModel: OG.shared[CounterModel.self])
//    }
//}
