package com.kmpfoo.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import co.early.fore.ui.size.rememberWindowSize
import co.early.n8.compose.N8Host
import com.kmpfoo.android.ui.screens.BangkokScreen
import com.kmpfoo.android.ui.screens.DakarScreen
import com.kmpfoo.android.ui.screens.LAScreen
import com.kmpfoo.ui.navigation.Location

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface( modifier = Modifier.fillMaxSize()) {
                N8Host<Location, Unit> { navigationState ->
                    when(navigationState.currentLocation){
                        Location.Bangkok -> BangkokScreen(rememberWindowSize())
                        Location.Dakar -> DakarScreen(rememberWindowSize())
                        Location.LA -> LAScreen(rememberWindowSize())
                    }
                }
            }
        }
    }
}
