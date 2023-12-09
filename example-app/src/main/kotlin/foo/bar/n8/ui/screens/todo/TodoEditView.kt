package foo.bar.n8.ui.screens.todo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import foo.bar.clean.domain.services.db.TodoItem
import foo.bar.clean.ui.R
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.n8.ui.common.components.elements.Btn
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.common.components.elements.SH3
import foo.bar.n8.ui.common.components.elements.Txt

@Composable
fun TodoEditView(
    todoItem: TodoItem?,
    perform: (Act) -> Unit = {},
    size: WindowSize = LocalWindowSize.current,
) {

    Fore.i("TodoEdit View")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = SH3(size))
    ) {

        var text by rememberSaveable { mutableStateOf(todoItem?.label ?: "") }

        Column(
            modifier = Modifier.align(Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (todoItem != null) {

                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

                TextField(
                    modifier = Modifier
                        .focusRequester(focusRequester),
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    label = { Txt(stringResource(id = R.string.todo_todo)) }
                )
            } else {
                Txt(text = stringResource(id = R.string.todo_no_data))
            }
        }

        Btn(
            spec = BtnSpec(
                label = stringResource(id = R.string.btn_update),
                clicked = {
                    todoItem?.let {
                        perform(Act.ScreenTodo.UpdateThenBack(it.copy(label = text)))
                    }
                },
            ),
            modifier = Modifier.align(BottomEnd),
            enabled = todoItem != null && text != todoItem.label
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
