package co.early.n8

import co.early.fore.core.delegate.Fore

fun <R> NavigationModel<*, *>.beforeAndAfterLog(action: () -> R): R {
    Fore.i("\n\n******** state BEFORE action ********\n")
    Fore.i(toString(diagnostics = true)) // state before
    val result: R = action()
    Fore.i("\n\n******** state AFTER action ********\n")
    Fore.i(toString(diagnostics = true)) // state after
    return result
}
