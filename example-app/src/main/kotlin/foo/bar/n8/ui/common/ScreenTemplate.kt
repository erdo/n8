package foo.bar.n8.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import foo.bar.n8.ui.common.elements.SH3
import foo.bar.n8.ui.common.elements.TextSpec
import foo.bar.n8.ui.common.elements.Txt
import foo.bar.n8.ui.navigation.Location

data class BtnSpec(
    val label: String,
    val clicked: () -> Unit
)

@Composable
fun ScreenTemplate(
    location: Location,
    buttons: List<BtnSpec>,
    stateAsString: String,
    size: WindowSize = LocalWindowSize.current,
) {
    StateWrapperView(
        stateAsString = stateAsString,
        size = size
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Txt(
                text = location.toString(),
                textSpec = TextSpec.H2(size)
            )

            Spacer(
                modifier = Modifier.height(SH3(size))
            )

            buttons.forEach {
                OutlinedButton(
                    modifier = Modifier,
                    colors = ButtonDefaults.textButtonColors(),
                    onClick = { it.clicked() },
                    border = BorderStroke(
                        width = 1.dp,
                        color = ButtonDefaults.textButtonColors().contentColor,
                    ),
                    shape = ButtonDefaults.textShape,
                    enabled = true,
                ) {
                    Txt(
                        text = it.label,
                        textSpec = TextSpec.P2(size)
                    )
                }
            }
        }
    }
}