package com.kmpfoo.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import co.early.n8.compose.N8Host
import com.kmpfoo.android.ui.screens.CurrentScreen
import com.kmpfoo.android.ui.tabhost.RootTabHostView
import com.kmpfoo.ui.navigation.Location
import com.kmpfoo.ui.navigation.TabHostId

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface( modifier = Modifier.fillMaxSize()) {
                N8Host<Location, TabHostId> { navigationState ->
                    if (navigationState.hostedBy.isNotEmpty()){
                        RootTabHostView(navigationState, navigationState.hostedBy, 0){
                            CurrentScreen(navigationState)
                        }
                    } else {
                        CurrentScreen(navigationState)
                    }
                }
            }
        }
    }
}
