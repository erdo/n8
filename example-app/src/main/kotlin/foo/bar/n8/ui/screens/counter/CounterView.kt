package foo.bar.n8.ui.screens.counter

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.AspectBasedValue
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import co.early.fore.ui.size.minimumDimension
import foo.bar.clean.domain.features.counter.CounterState
import foo.bar.clean.ui.R
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.n8.ui.common.components.CircularProgressIndicatorDelayed
import foo.bar.n8.ui.common.components.StateWrapperView
import foo.bar.n8.ui.common.components.ViewTemplate

@Composable
fun CounterView(
    counterState: CounterState,
    perform: (Act) -> Unit = {},
    size: WindowSize = LocalWindowSize.current,
) {
    StateWrapperView(
        state = counterState,
        size = size
    ) {
        ViewTemplate(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Counter(
                counterState = counterState,
                size = size,
                increaseCallback = { perform(Act.ScreenCounter.IncreaseCounter) },
                decreaseCallback = { perform(Act.ScreenCounter.DecreaseCounter) },
            )
        }
    }
}

@Composable
fun Counter(
    counterState: CounterState,
    size: WindowSize = LocalWindowSize.current,
    increaseCallback: () -> Unit = {},
    decreaseCallback: () -> Unit = {},
) {

    Fore.i("Counter View")

    val minimumDimension = size.dpSize.minimumDimension()
    val borderThickness = minimumDimension * 0.10f
    val boxHeight = minimumDimension * 0.50f
    val numberFontSize = (minimumDimension / 5f).value.sp
    val buttonFontSize = (minimumDimension / 8f).value.sp
    val buttonSize = max(borderThickness * 3, 50.dp)

    val shape = AspectBasedValue(
        port = CircleShape,
        land = CircleShape,
        squarish = RectangleShape
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(boxHeight)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = borderThickness, end = borderThickness)
                .border(
                    width = borderThickness,
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = shape(size)
                ),
        )

        Box(modifier = Modifier.fillMaxSize()) {

            CustomButton(
                Modifier.align(Alignment.CenterStart),
                R.string.counter_decrease,
                counterState.canDecrease(),
                buttonSize,
                buttonFontSize,
                decreaseCallback,
            )

            if (counterState.loading) {
                CircularProgressIndicatorDelayed(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(borderThickness),
                )
            } else {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(borderThickness / 2),
                    text = counterState.amount.toString(),
                    style = TextStyle(fontSize = numberFontSize)
                )
            }

            CustomButton(
                Modifier.align(Alignment.CenterEnd),
                R.string.counter_increase,
                counterState.canIncrease(),
                buttonSize,
                buttonFontSize,
                increaseCallback,
            )
        }
    }
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    @StringRes labelResId: Int,
    enabled: Boolean,
    buttonSize: Dp,
    buttonFontSize: TextUnit,
    callback: () -> Unit = {},
) {

    Fore.d("CustomButton")

    Button(
        modifier = modifier.size(buttonSize),
        onClick = { callback() },
        enabled = enabled
    ) {
        Text(
            text = stringResource(id = labelResId),
            style = TextStyle(fontSize = buttonFontSize)
        )
    }
}
