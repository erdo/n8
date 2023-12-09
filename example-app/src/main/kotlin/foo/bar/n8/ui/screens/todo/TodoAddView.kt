package foo.bar.n8.ui.screens.todo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import foo.bar.clean.ui.R
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.n8.ui.common.components.elements.Btn
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.common.components.elements.SH3
import foo.bar.n8.ui.common.components.elements.Txt

@Composable
fun TodoAddView(
    perform: (Act) -> Unit = {},
    size: WindowSize = LocalWindowSize.current,
) {

    Fore.i("TodoAdd View")

    Box(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            .padding(bottom = SH3(size))
    ) {

        val defaultLabel = stringResource(id = R.string.todo_default_label)
        var label by rememberSaveable { mutableStateOf("") }

        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        TextField(
            modifier = Modifier
                .align(Center)
                .focusRequester(focusRequester),
            value = label,
            placeholder = { Text(defaultLabel) },
            onValueChange = {
                label = it
            },
            label = { Txt(stringResource(id = R.string.todo_todo)) }
        )

        Btn(
            spec = BtnSpec(
                label = stringResource(id = R.string.btn_add),
                clicked = { perform(Act.ScreenTodo.CreateThenBack(label)) }
            ),
            modifier = Modifier.align(BottomEnd),
            enabled = label.isNotEmpty()
        )

        Btn(
            spec = BtnSpec(
                label = stringResource(id = R.string.btn_cancel),
                clicked = { perform(Act.Global.Back) }
            ),
            modifier = Modifier.align(BottomStart),
        )
    }
}
