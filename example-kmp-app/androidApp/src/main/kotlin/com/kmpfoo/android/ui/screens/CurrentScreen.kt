package com.kmpfoo.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import co.early.fore.core.delegate.Fore
import co.early.n8.NavigationState
import com.kmpfoo.ui.navigation.Location
import com.kmpfoo.ui.navigation.TabHostId


@Composable
fun CurrentScreen(
    navigationState: NavigationState<Location, TabHostId>,
    modifier: Modifier = Modifier
) {

    Fore.i("CurrentScreen ${navigationState.currentLocation}")

    when (val currentLocation = navigationState.currentLocation) {
        Location.Welcome -> ScreenWelcome(modifier = Modifier.background(color = Color(0xFFB3FFD9.toInt())).then(modifier)) // Mint Green
        Location.Home -> ScreenHome(modifier = Modifier.background(color = Color(0xFFFFC1CC.toInt())).then(modifier)) // Soft Pink
        is Location.Bangkok -> ScreenBangkok(
            location = currentLocation,
            modifier = Modifier.background(color = Color(0xFFFFFFB3.toInt())).then(modifier) // Light Yellow
        )
        Location.Dakar -> ScreenDakar(modifier = Modifier.background(color = Color(0xFFCCFFFF.toInt())).then(modifier)) // Pale Cyan
        Location.LA -> ScreenLA(modifier = Modifier.background(color = Color(0xFFFFE5B4.toInt())).then(modifier)) // Peach
        is Location.NewYork -> ScreenNewYork(modifier = Modifier.background(color = Color(0xFFCCE5FF.toInt())).then(modifier)) // Baby Blue
        Location.EuropeanLocation.London -> ScreenLondon(modifier = Modifier.background(color = Color(0xFFFFCCFF.toInt())).then(modifier)) // Light Magenta
        is Location.EuropeanLocation.Milan -> ScreenMilan(
            location = currentLocation,
            modifier = Modifier.background(color = Color(0xFFE0FFE0.toInt())).then(modifier)  // Pale Green
        )
        Location.EuropeanLocation.Paris -> ScreenParis(modifier = Modifier.background(color = Color(0xFFD5CCFF.toInt())).then(modifier)) // Lavender
        Location.EuropeanLocation.Danube -> ScreenDanube(modifier = Modifier.background(color = Color(0xFFFFDAB9.toInt())).then(modifier)) // Pastel Orange
        Location.EuropeanLocation.France -> ScreenFrance(modifier = Modifier.background(color = Color(0xFFFFF0F5.toInt())).then(modifier)) // Lavender Blush
        Location.EuropeanLocation.Poland -> ScreenPoland(modifier = Modifier.background(color = Color(0xFFF0FFF0.toInt())).then(modifier)) // Honeydew
        Location.EuropeanLocation.Rhine -> ScreenRhine(modifier = Modifier.background(color = Color(0xFFFAF0FF.toInt())).then(modifier)) // Light Purple
        Location.EuropeanLocation.Seine -> ScreenSeine(modifier = Modifier.background(color = Color(0xFFFFFAE0.toInt())).then(modifier)) // Light Cream
        Location.EuropeanLocation.Spain -> ScreenSpain(modifier = Modifier.background(color = Color(0xFFE0FFFF.toInt())).then(modifier)) // Light Aqua
        Location.EuropeanLocation.Thames -> ScreenThames(modifier = Modifier.background(color = Color(0xFFB3F0FF.toInt())).then(modifier)) // Pastel Teal
    }
}
