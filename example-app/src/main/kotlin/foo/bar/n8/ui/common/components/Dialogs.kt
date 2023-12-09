package foo.bar.n8.ui.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import foo.bar.n8.ui.common.components.elements.Btn
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.theme.LocalAppColors

@Composable
fun AlertTwoButton(
    title: String,
    text: String,
    confirmBtnSpec: BtnSpec,
    dismissBtnSpec: BtnSpec,
    onDismissRequest: () -> Unit = dismissBtnSpec.clicked,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = { Btn(confirmBtnSpec) },
        dismissButton = { Btn(dismissBtnSpec) },
        title = { Text(text = title) },
        text = { Text(text = text) },
    )
}

@Composable
fun AlertOneButton(
    title: String,
    text: String,
    confirmBtnSpec: BtnSpec,
    onDismissRequest: () -> Unit = confirmBtnSpec.clicked,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = { Btn(confirmBtnSpec) },
        title = { Text(text = title) },
        text = { Text(text = text) },
        containerColor = LocalAppColors.current.paper,
    )
}
