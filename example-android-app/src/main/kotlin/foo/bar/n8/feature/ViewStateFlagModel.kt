package foo.bar.n8.feature

import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.ObservableImp
import kotlinx.serialization.Serializable

/**
 * This class probably looks weird to you, explanation is
 * here: https://erdo.github.io/android-fore/
 *
 * You can implement this kind of thing however you wish for your own app
 *
 * (i.e. it's not related to navigation or N8)
 */
class ViewStateFlagModel : ReadableState<ViewStateFlagModelState>, Observable by ObservableImp() {

    override var state = ViewStateFlagModelState()
        private set

    fun toggle() {
        state = ViewStateFlagModelState(!state.showState)
        notifyObservers()
    }
}

@Serializable
data class ViewStateFlagModelState(
    val showState: Boolean = false,
) : State
