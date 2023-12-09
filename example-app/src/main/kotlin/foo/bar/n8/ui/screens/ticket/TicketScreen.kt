package foo.bar.n8.ui.screens.ticket

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import foo.bar.clean.domain.features.ReadableState
import foo.bar.clean.domain.features.State
import foo.bar.clean.domain.features.network.NetworkModel
import foo.bar.clean.domain.features.network.NetworkState
import foo.bar.clean.domain.features.ticket.TicketModel
import foo.bar.clean.domain.features.ticket.TicketState
import foo.bar.clean.ui.actionhandlers.screens.ActionHandlerTicketScreen
import foo.bar.n8.ui.common.toState
import org.koin.compose.koinInject

@Composable
fun TicketScreen(
    ticketStateProvider: ReadableState<TicketState> = (koinInject() as TicketModel),
    networkStateProvider: ReadableState<NetworkState> = (koinInject() as NetworkModel),
    actionHandler: ActionHandlerTicketScreen = koinInject(),
) {

    val ticketState by ticketStateProvider.toState()
    val networkState by networkStateProvider.toState()

    TicketView(
        ticketViewState = TicketViewState(ticketState, networkState),
        perform = { action -> actionHandler.handle(action) },
    )
}

/**
 * This viewState is completely optional. It's ephemeral, exists only in the UI layer and is
 * derived from states that exist in the domain layer. The important part is this: our
 * source of truth is still the models in the domain layer
 */
data class TicketViewState(
    val ticketState: TicketState = TicketState(),
    val networkState: NetworkState = NetworkState(),
    val color: Int? = null,
) : State
