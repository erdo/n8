package foo.bar.n8.ui.actionhandlers.screens

import co.early.fore.kt.core.delegate.Fore
import foo.bar.clean.domain.features.ticket.TicketModel
import foo.bar.n8.ui.actionhandlers.Act
import foo.bar.n8.ui.actionhandlers.GlobalActionHandler
import foo.bar.n8.ui.actionhandlers.koinInject

class ActionHandlerTicketScreen(
    private val ticketModel: TicketModel = koinInject(),
) : GlobalActionHandler<Act.ScreenTicket>() {

    override fun __handle(act: Act.ScreenTicket) {

        Fore.i("_handle ScreenTicket Action: $act")

        when (act) {
            Act.ScreenTicket.RequestTicket -> ticketModel.processTicket()
            Act.ScreenTicket.ClearData -> ticketModel.clearData()
            Act.ScreenTicket.ClearError -> ticketModel.clearError()
        }
    }
}
