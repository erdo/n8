package foo.bar.n8.ui.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import foo.bar.n8.ui.common.anim.CustomEasing

@Composable
fun CircularProgressIndicatorDelayed(
    modifier: Modifier = Modifier,
) {

    val alpha = remember { Animatable(0.0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 400,
                easing = CustomEasing.lateRiser,
                delayMillis = 100,
            )
        )
    }

    CircularProgressIndicator(
        modifier = modifier
            .alpha(alpha.value)
    )
}
