package foo.bar.n8.ui.common.elements

import androidx.compose.ui.unit.dp
import co.early.fore.ui.size.HeightBasedDp
import co.early.fore.ui.size.WidthBasedDp

// Usage:
//
// Spacer(
//    modifier = Modifier.height(SH2(size))
// )
// Spacer(
//    modifier = Modifier.width(SW2(size))
// )
//

val SH1 = HeightBasedDp(2.dp, 3.dp, 5.dp)
val SH2 = HeightBasedDp(5.dp, 8.dp, 20.dp)
val SH3 = HeightBasedDp(12.dp, 20.dp, 80.dp)
val SH4 = HeightBasedDp(20.dp, 50.dp, 200.dp)

val SW1 = WidthBasedDp(2.dp, 3.dp, 5.dp)
val SW2 = WidthBasedDp(5.dp, 8.dp, 20.dp)
val SW3 = WidthBasedDp(12.dp, 20.dp, 80.dp)
val SW4 = WidthBasedDp(20.dp, 50.dp, 200.dp)
