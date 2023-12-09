package foo.bar.n8.ui.common.components.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import foo.bar.n8.ui.theme.LocalAppColors

@Composable
fun Btn(
    spec: BtnSpec,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: WindowSize = LocalWindowSize.current,
) {

    val color = when (spec.type) {
        BtnType.Positive -> LocalAppColors.current.btnPositive
        BtnType.Negative -> LocalAppColors.current.btnNegative
        BtnType.Neutral -> LocalAppColors.current.btnNeutral
        BtnType.Danger -> LocalAppColors.current.btnDanger
        is BtnType.CustomColour -> spec.type.color
    }

    OutlinedButton(
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(contentColor = color),
        onClick = { spec.clicked() },
        border = BorderStroke(
            width = 1.dp,
            color = color,
        ),
        shape = ButtonDefaults.textShape,
        enabled = enabled,
    ) {
        Txt(
            text = spec.label,
            textSpec = TextSpec.P2(size)
        )
    }
}

data class BtnSpec(
    val label: String,
    val clicked: () -> Unit,
    val type: BtnType = BtnType.Positive,
)

sealed class BtnType {
    data object Positive : BtnType()
    data object Negative : BtnType()
    data object Neutral : BtnType()
    data object Danger : BtnType()
    data class CustomColour(val color: Color) : BtnType()
}
