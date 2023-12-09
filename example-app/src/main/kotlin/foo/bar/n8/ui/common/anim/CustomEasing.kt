package foo.bar.n8.ui.common.anim

import androidx.compose.animation.core.CubicBezierEasing
import foo.bar.n8.ui.common.anim.CordicBounceOut

object CustomEasing {
    // you can design your own easing paths here: https://matthewlein.com/tools/ceaser
    val lateRiser = CubicBezierEasing(1.000f, 0.000f, 0.680f, 1.000f)
    val straightNoChaser = CubicBezierEasing(0.250f, 0.250f, 0.750f, 0.750f)
    val overPhlop = CubicBezierEasing(0.370f, 0.870f, 0.755f, 1.420f)
    val hardBounceOut = CordicBounceOut()
}