package com.kmpfoo.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import co.early.fore.core.delegate.Fore
import co.early.n8.NavigationState
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
                N8Host<Location, TabHostId> { navigationState, peekBackNavState, backProgress ->

                    if (backProgress != 0f && peekBackNavState != null) {
                        // predictive back view
                        Box(Modifier.fillMaxSize().alpha(backProgress * 0.5f)) {
                            ContentRoot(peekBackNavState)
                        }
                        // current view
                        Box(Modifier.fillMaxSize().alpha(1f - backProgress)) {
                            ContentRoot(navigationState)
                        }
                    } else {
                        ContentRoot(navigationState)
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentRoot(
    navigationState: NavigationState<Location, TabHostId>,
) {
    if (navigationState.hostedBy.isNotEmpty()){
        RootTabHostView(navigationState, navigationState.hostedBy, 0){
            CurrentScreen(navigationState)
        }
    } else {
        CurrentScreen(navigationState)
    }
}
