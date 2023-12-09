package foo.bar.n8.ui.actionhandlers

import co.early.fore.kt.core.delegate.Fore

abstract class ActionHandler<T : Act> : ActHandler<T> {
    override fun handle(act: Act) {
        try {
            val subAct = act as? T
            when {
                act is Act.Global -> {
                    _handle(act)
                }
                subAct != null -> {
                    __handle(act)
                }
            }
        } catch (cce: java.lang.ClassCastException) {
            Fore.e("Unexpected Action type, is the Screen correct?: $act")
        }
    }

    protected abstract fun _handle(act: Act.Global)
    protected abstract fun __handle(act: T)
}

interface ActHandler<T : Act> {
    fun handle(act: Act)
}
