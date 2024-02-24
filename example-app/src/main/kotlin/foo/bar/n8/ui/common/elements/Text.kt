package foo.bar.n8.ui.common.elements

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import co.early.fore.ui.size.minimumDimension

@Composable
fun Txt(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    size: WindowSize = LocalWindowSize.current,
    textSpec: TextSpec = TextSpec.P1(size),
    style: TextStyle = TextStyle(
        fontSize = textSpec.fontSize,
        fontWeight = textSpec.fontWeight
    ),
    color: Color = Color.Unspecified,
) {
    Text(
        text = text,
        modifier = modifier,
        textAlign = textAlign,
        style = style,
        color = color,
    )
}

sealed class TextSpec(
    val fontWeight: FontWeight = FontWeight.Normal,
    factor: Float,
    size: WindowSize,
) {

    val fontSize: TextUnit = (size.dpSize.minimumDimension() / factor).value.sp

    data class H1(val size: WindowSize) : TextSpec(FontWeight.W300, 12f, size)
    data class H2(val size: WindowSize) : TextSpec(FontWeight.W300, 15f, size)
    data class H3(val size: WindowSize) : TextSpec(FontWeight.W300, 20f, size)
    data class P1(val size: WindowSize) : TextSpec(FontWeight.W400, 25f, size)
    data class P2(val size: WindowSize) : TextSpec(FontWeight.W400, 30f, size)
    data class S(val size: WindowSize) : TextSpec(FontWeight.W400, 35f, size)
}
