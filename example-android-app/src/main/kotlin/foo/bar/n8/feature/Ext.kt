package foo.bar.n8.feature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import co.early.fore.compose.observeAsState
import co.early.fore.core.observer.Observable

/**
 * convenience functions to convert a fore Observable into a Compose State
 */
interface State

@Composable
fun <S: foo.bar.n8.feature.State> ReadableState<S>.toState(): State<S> = this.observeAsState { state }

interface ReadableState<S : foo.bar.n8.feature.State> : Observable {
    val state: S
}
