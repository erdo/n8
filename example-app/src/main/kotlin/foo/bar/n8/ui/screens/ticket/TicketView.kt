package foo.bar.n8.ui.screens.ticket

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import foo.bar.clean.domain.DomainError
import foo.bar.clean.domain.features.network.Availability.Available
import foo.bar.clean.domain.features.ticket.Status
import foo.bar.clean.domain.features.ticket.Step
import foo.bar.clean.domain.services.api.Fruit
import foo.bar.clean.ui.R
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.n8.ui.common.components.AlertOneButton
import foo.bar.n8.ui.common.components.StateWrapperView
import foo.bar.n8.ui.common.components.ViewTemplate
import foo.bar.n8.ui.common.components.elements.Btn
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.common.components.elements.BtnType
import foo.bar.n8.ui.common.components.elements.SH2
import foo.bar.n8.ui.common.components.elements.SH3
import foo.bar.n8.ui.common.components.elements.SW1
import foo.bar.n8.ui.common.components.elements.SW2
import foo.bar.n8.ui.common.components.elements.SW3
import foo.bar.n8.ui.common.components.elements.TextSpec
import foo.bar.n8.ui.common.components.elements.Txt
import foo.bar.n8.ui.common.components.extraPaddingForHideUiBtn
import foo.bar.n8.ui.common.mapToUserMessage
import foo.bar.n8.ui.theme.LocalAppColors
import kotlinx.coroutines.launch

@Composable
fun TicketView(
    ticketViewState: TicketViewState,
    perform: (Act) -> Unit = {},
    size: WindowSize = LocalWindowSize.current,
) {
    StateWrapperView(
        state = ticketViewState,
        size = size,
    ) {
        ViewTemplate(modifier = Modifier.fillMaxSize()) {
            Ticket(
                viewState = ticketViewState,
                size = size,
                clearBtnClicked = { perform(Act.ScreenTicket.ClearData) },
                clearErrorBtnClicked = { perform(Act.ScreenTicket.ClearError) },
                fetchTicketBtnClicked = { perform(Act.ScreenTicket.RequestTicket) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.Ticket(
    viewState: TicketViewState,
    size: WindowSize = LocalWindowSize.current,
    clearBtnClicked: () -> Unit = {},
    clearErrorBtnClicked: () -> Unit = {},
    fetchTicketBtnClicked: () -> Unit = {},
) {

    Fore.i("Ticket View")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = SH3(size))
    ) {

        Column(
            modifier = Modifier
                .align(TopStart)
                .fillMaxSize()
                .padding(
                    top = extraPaddingForHideUiBtn(size),
                    bottom = extraPaddingForHideUiBtn(size)
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {

            Txt(
                textAlign = TextAlign.Start,
                text = stringResource(
                    id = R.string.ticket_wait_time_max,
                    viewState.ticketState.maxAcceptableWaitTimeMin
                )
            )

            Spacer(modifier = Modifier.height(SH2(size)))
            AnimatedVisibility(
                visible = viewState.ticketState.freeFruit != Fruit.FruitNone,
                enter = slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn(),
            ) {
                viewState.ticketState.freeFruit.takeUnless { it == Fruit.FruitNone }?.let {
                    Txt(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = SW1(size)),
                        textSpec = TextSpec.H1(size),
                        textAlign = TextAlign.Center,
                        text = it.toLabel(),
                        color = LocalAppColors.current.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(SH2(size)))

            for (stepAndStatus in viewState.ticketState.progress) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = SW2(size)),
                    verticalAlignment = CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(SW2(size)))
                    Box(
                        modifier = Modifier
                            .width(SW3(size))
                            .height(SW3(size))
                    ) {
                        if (stepAndStatus.second == Status.InProgress) {
                            CircularProgressIndicator(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(SW2(size)))
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stepAndStatus.first.toLabel(),
                        tint = stepAndStatus.second.toColor(),
                    )
                    Spacer(modifier = Modifier.width(SW2(size)))
                    Txt(
                        textSpec = TextSpec.H3(size),
                        textAlign = TextAlign.Center,
                        text = stepAndStatus.first.toLabel()
                    )
                    if (stepAndStatus.first is Step.FetchingWaitingTime) {
                        Spacer(modifier = Modifier.width(SW2(size)))
                        Txt(
                            textSpec = TextSpec.H3(size),
                            textAlign = TextAlign.Center,
                            text = viewState.ticketState.waitTimeMin?.let {
                                stringResource(id = R.string.ticket_wait_time, it)
                            } ?: "",
                            color = if (viewState.ticketState.waitIsTooLong())
                                LocalAppColors.current.error else LocalAppColors.current.primary
                        )
                    }
                }
            }
        }

        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        var showBottomSheet by remember { mutableStateOf(false) }

        FlowRow(
            modifier = Modifier
                .align(BottomCenter),
            horizontalArrangement = Arrangement.Center,
        ) {
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.btn_clear),
                    clicked = clearBtnClicked
                ),
                enabled = !viewState.ticketState.loading && viewState.ticketState.hasData(),
                modifier = Modifier.padding(SW1(size)),
            )
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.ticket_check_details),
                    clicked = { showBottomSheet = true }
                ),
                enabled = true,
                modifier = Modifier.padding(SW1(size)),
            )
            Btn(
                spec = BtnSpec(
                    label = stringResource(id = R.string.ticket_fetch_ticket),
                    clicked = fetchTicketBtnClicked
                ),
                enabled = !viewState.ticketState.loading,
                modifier = Modifier.padding(SW1(size)),
            )
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
            ) {

                Column(
                    modifier = Modifier.padding(SH2(size))
                ) {

                    Txt(
                        textAlign = TextAlign.Start,
                        text = stringResource(
                            id = R.string.ticket_network_status,
                            if (viewState.networkState.availability == Available) {
                                stringResource(id = R.string.btn_yes)
                            } else {
                                stringResource(id = R.string.btn_no)
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(SH2(size)))

                    Btn(
                        spec = BtnSpec(
                            label = stringResource(id = R.string.btn_ok),
                            clicked = {
                                scope.launch { sheetState.hide() }
                                    .invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet = false
                                        }
                                    }
                            }
                        ),
                    )

                    Spacer(modifier = Modifier.height(SH3(size)))
                }
            }
        }
    }

    viewState.ticketState.error.takeUnless { it == DomainError.NoError }?.let {
        AlertOneButton(
            title = stringResource(id = R.string.error),
            text = stringResource(id = it.mapToUserMessage()),
            confirmBtnSpec = BtnSpec(
                label = stringResource(id = R.string.btn_retry),
                clicked = fetchTicketBtnClicked,
                type = BtnType.Positive
            ),
            onDismissRequest = clearErrorBtnClicked,
        )
    }
}

@Composable
private fun Status.toColor(): Color {
    return when (this) {
        Status.Done -> LocalAppColors.current.primary
        Status.Failed -> LocalAppColors.current.error
        Status.InProgress -> LocalAppColors.current.scrimShadow
        Status.Waiting -> LocalAppColors.current.scrimShadow
    }
}

@Composable
private fun Step.toLabel(): String {
    return stringResource(
        id = when (this) {
            Step.Initialising -> R.string.ticket_step_init
            Step.CreatingUser -> R.string.ticket_step_createuser
            Step.CreatingTicket -> R.string.ticket_step_createticket
            Step.FetchingWaitingTime -> R.string.ticket_step_waittime
            Step.CancellingTicket -> R.string.ticket_step_cancelticket
            Step.ConfirmingTicket -> R.string.ticket_step_confirmticket
            Step.ClaimingFreeFruit -> R.string.ticket_step_claimfruit
            Step.Complete -> R.string.ticket_step_complete
        }
    )
}

@Composable
private fun Fruit.toLabel(): String {
    return when (this) {
        Fruit.FruitNone -> stringResource(id = R.string.ticket_fruit_none)
        is Fruit.FruitSome -> {
            "$name: $tastyPercentScore%"
        }
    }
}
