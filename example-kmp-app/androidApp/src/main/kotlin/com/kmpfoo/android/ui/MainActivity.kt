package com.kmpfoo.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import co.early.n8.NavigationState
import co.early.n8.compose.N8Host
import com.kmpfoo.android.ui.screens.CurrentScreen
import com.kmpfoo.android.ui.tabhost.RootTabHostView
import com.kmpfoo.ui.navigation.Location
import com.kmpfoo.ui.navigation.TabHostId

class MainActivity : ComponentActivity() {

    val alphaEasing = CubicBezierEasing(0.970f, 0.050f, 0.460f, 0.985f)
    val scaleEasing = CubicBezierEasing(0.380f, 0.185f, 0.000f, 0.990f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                N8Host<Location, TabHostId> { navigationState, peekBackNavState, backProgress ->

                    peekBackNavState?.let { peek ->
                        // predictive back view
                        Box(Modifier.fillMaxSize()) {
                            ContentRoot(peek)
                        }
                        // current view
                        val elevationPx = with(LocalDensity.current) { (6.dp * backProgress.let { it * it }).toPx() }
                        val alpha = 1 - alphaEasing.transform(backProgress)
                        val scale = 1f - (0.4f * scaleEasing.transform(backProgress))

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(alpha)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    shadowElevation = elevationPx
                                }
                        ) {
                            ContentRoot(navigationState)
                        }
                    } ?: run { ContentRoot(navigationState) }
                }
            }
        }
    }
}

@Composable
private fun ContentRoot(
    navigationState: NavigationState<Location, TabHostId>,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        if (navigationState.hostedBy.isNotEmpty()) {
            RootTabHostView(navigationState, navigationState.hostedBy, 0) {
                CurrentScreen(navigationState)
            }
        } else {
            CurrentScreen(navigationState)
        }
    }
}
