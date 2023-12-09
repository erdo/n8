package foo.bar.n8.ui.common.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import co.early.fore.ui.size.hL_high
import co.early.fore.ui.size.hL_low
import co.early.fore.ui.size.hM_high
import co.early.fore.ui.size.hM_low
import co.early.fore.ui.size.hS_high
import co.early.fore.ui.size.hS_low
import co.early.fore.ui.size.hXL_high
import co.early.fore.ui.size.hXL_low
import co.early.fore.ui.size.hXS_high
import co.early.fore.ui.size.hXS_low
import co.early.fore.ui.size.wL_high
import co.early.fore.ui.size.wL_low
import co.early.fore.ui.size.wM_high
import co.early.fore.ui.size.wM_low
import co.early.fore.ui.size.wS_high
import co.early.fore.ui.size.wS_low
import co.early.fore.ui.size.wXL_high
import co.early.fore.ui.size.wXL_low
import co.early.fore.ui.size.wXS_high
import co.early.fore.ui.size.wXS_low
import foo.bar.clean.domain.features.counter.CounterState
import foo.bar.n8.ui.common.PreviewWithWindowSize

@Preview(showBackground = true, widthDp = wXS_low, heightDp = hXS_low)
@Preview(showBackground = true, widthDp = wXS_high, heightDp = hXS_high)
@Preview(showBackground = true, widthDp = wS_low, heightDp = hS_low)
@Preview(showBackground = true, widthDp = wS_high, heightDp = hS_high)
@Preview(showBackground = true, widthDp = wM_low, heightDp = hM_low)
@Preview(showBackground = true, widthDp = wM_high, heightDp = hM_high)
@Preview(showBackground = true, widthDp = wL_low, heightDp = hL_low)
@Preview(showBackground = true, widthDp = wL_high, heightDp = hL_high)
@Preview(showBackground = true, widthDp = wXL_low, heightDp = hXL_low)
@Preview(showBackground = true, widthDp = wXL_high, heightDp = hXL_high)
@Preview(showBackground = true, widthDp = wL_high, heightDp = hXS_low)
@Preview(showBackground = true, widthDp = wXS_low, heightDp = hM_high)
@Composable
fun MyPreviews() {
    PreviewWithWindowSize {
        StateWrapperView(
            state = CounterState(3, max = 0, min = 9),
            size = it,
            uiIsVisibleDefault = false
        ) {}
    }
}
