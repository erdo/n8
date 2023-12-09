package foo.bar.n8.ui.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.StrokeCap
import foo.bar.n8.ui.common.anim.CustomEasing

@Composable
fun AnimatedLinearProgressIndicator(
    newProgress: Float = 0f,
    show: Boolean = true, // this lets us animate from 0 -> currentValue when show becomes true
) {

    val progress by animateFloatAsState(
        targetValue = if (show) {
            newProgress
        } else 0f,
        animationSpec = tween(durationMillis = 300, easing = CustomEasing.straightNoChaser),
        label = "changing progress value",
    )

    AnimatedVisibility(
        visible = show,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        LinearProgressIndicator(
            progress = progress,
            strokeCap = StrokeCap.Round,
        )
    }
}
