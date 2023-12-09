package foo.bar.n8.ui.screens.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import foo.bar.clean.domain.features.todo.TodoState
import foo.bar.clean.ui.R
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.n8.ui.common.components.CircularProgressIndicatorDelayed
import foo.bar.n8.ui.common.components.StateWrapperView
import foo.bar.n8.ui.common.components.ViewTemplate
import foo.bar.n8.ui.common.components.elements.Btn
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.common.components.elements.SH1
import foo.bar.n8.ui.common.components.elements.SH2
import foo.bar.n8.ui.common.components.elements.SH3
import foo.bar.n8.ui.common.components.elements.SW1
import foo.bar.n8.ui.common.components.elements.SW2
import foo.bar.n8.ui.common.components.elements.Txt
import foo.bar.n8.ui.common.components.extraPaddingForHideUiBtn
import foo.bar.n8.ui.theme.LocalAppColors

@Composable
fun TodoView(
    todoState: TodoState,
    perform: (Act) -> Unit = {},
    size: WindowSize = LocalWindowSize.current,
) {
    StateWrapperView(
        state = todoState,
        size = size,
    ) {
        ViewTemplate {
            Todos(
                todoState = todoState,
                size = size,
                itemSelected = { index -> perform(Act.ScreenTodo.ToEditScreen(index)) },
                itemDoneChecked = { index, _ -> perform(Act.ScreenTodo.ToggleDone(index)) },
                itemDelete = { index -> perform(Act.ScreenTodo.ItemDelete(index)) },
                toggleShowDoneBtnClicked = { perform(Act.ScreenTodo.ToggleShowDone) },
                addBtnClicked = { perform(Act.ScreenTodo.ToAddScreen) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.Todos(
    todoState: TodoState,
    size: WindowSize = LocalWindowSize.current,
    itemSelected: (Int) -> Unit = {},
    itemDoneChecked: (Int, Boolean) -> Unit = { _, _ -> },
    itemDelete: (Int) -> Unit = {},
    toggleShowDoneBtnClicked: () -> Unit = {},
    addBtnClicked: () -> Unit = {},
) {

    Fore.i("Todo View")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = SH3(size))
    ) {

        if (todoState.items.isEmpty()) {
            if (todoState.loading) {
                CircularProgressIndicatorDelayed(
                    modifier = Modifier.align(Center)
                )
            } else {
                Column(
                    modifier = Modifier.align(Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Txt(
                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.todo_description)
                    )
                    Spacer(modifier = Modifier.height(SH2(size)))
                    Btn(
                        spec = BtnSpec(
                            label = stringResource(id = R.string.btn_add),
                            clicked = { addBtnClicked() }
                        ),
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = extraPaddingForHideUiBtn(size),
                        bottom = extraPaddingForHideUiBtn(size),
                    )
            ) {
                // this empty item ensures the list drops down when we add things to the top
                item { Spacer(modifier = Modifier.height(SH1(size))) }
                this.itemsIndexed(
                    items = todoState.items,
                    key = { _, item -> item.id }
                ) { index, item ->

                    val dismissState = rememberDismissState()

                    if (dismissState.isDismissed(direction = DismissDirection.EndToStart)) {
                        Fore.i("swiped to delete $index")
                        itemDelete(index)
                    }

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(
                            DismissDirection.EndToStart
                        ),
                        background = {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color = LocalAppColors.current.error)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    modifier = Modifier
                                        .align(CenterEnd)
                                        .padding(end = SW2(size)),
                                    contentDescription = stringResource(id = R.string.btn_delete),
                                    tint = LocalAppColors.current.primaryOn
                                )
                            }
                        },
                        dismissContent = {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = LocalAppColors.current.paper),
                                verticalAlignment = CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.done,
                                    onCheckedChange = { done -> itemDoneChecked(index, done) }
                                )
                                Spacer(modifier = Modifier.width(SW2(size)))
                                Txt(
                                    text = item.label,
                                    modifier = Modifier.clickable(onClick = { itemSelected(index) })
                                )
                            }
                        },
                    )
                }
            }
        }

        FlowRow(
            modifier = Modifier
                .align(BottomCenter),
            horizontalArrangement = Arrangement.Center,
        ) {
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.todo_show_done),
                    clicked = toggleShowDoneBtnClicked
                ),
                enabled = todoState.totalItems > 0 && !todoState.userOptions.includeDone,
                modifier = Modifier.padding(SW1(size)),
            )
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.todo_hide_done),
                    clicked = toggleShowDoneBtnClicked
                ),
                enabled = todoState.totalItems > 0 && todoState.userOptions.includeDone,
                modifier = Modifier.padding(SW1(size)),
            )
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.btn_add),
                    clicked = { addBtnClicked() }
                ),
                modifier = Modifier.padding(SW1(size)),
            )
        }
    }
}
