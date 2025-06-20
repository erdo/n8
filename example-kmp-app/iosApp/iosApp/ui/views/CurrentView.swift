import SwiftUI
import shared

//func currentScreenView(navigationState: NavigationState, modifier: some ViewModifier = EmptyModifier()) -> some View {
//    let currentLocation = navigationState.currentLocation
//
//    return Group {
//        switch currentLocation {
//        case .welcome:
//            ScreenWelcome().modifier(modifier.background(Color(red: 0.7, green: 1.0, blue: 0.85))) // Mint Green
//        case .home:
//            ScreenHome().modifier(modifier.background(Color(red: 1.0, green: 0.76, blue: 0.8))) // Soft Pink
//        case .bangkok(let location):
//            ScreenBangkok(location: location).modifier(modifier.background(Color(red: 1.0, green: 1.0, blue: 0.7))) // Light Yellow
//        case .dakar:
//            ScreenDakar().modifier(modifier.background(Color(red: 0.8, green: 1.0, blue: 1.0))) // Pale Cyan
//        // ... Add other cases similarly ...
//        }
//    }
//}
