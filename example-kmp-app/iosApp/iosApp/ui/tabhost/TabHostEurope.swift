import SwiftUI
import shared

struct TabHostEurope: View {
    
    @EnvironmentN8<Location, TabHostId> private var n8
    
    let tabHostSpec = LocationsKt.tabHostSpecEuropeIos
    let tabLabels = ["Country", "City", "River"]
    
    let selectedTabIndex: Int
    let content: () -> AnyView
    
    init(
        selectedTabIndex: Int,
        content: @escaping () -> AnyView
    ) {
        self.selectedTabIndex = selectedTabIndex
        self.content = content
    }
    
    var body: some View {
        VStack(spacing: 0) {
            HStack {
                ForEach(Array(tabLabels.enumerated()), id: \.offset) { index, label in
                    Button(action: {
                        n8.switchTab(tabHostSpec: tabHostSpec, tabIndex: Int32(index))
                    }) {
                        Text(label)
                    }
                    .disabled(selectedTabIndex == index)

                    if index < tabLabels.count - 1 {
                        Spacer()
                    }
                }
            }
            .padding()
            .background(Color.gray.opacity(0.1))
            
            content()
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
