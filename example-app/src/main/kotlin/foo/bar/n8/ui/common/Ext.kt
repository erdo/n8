package foo.bar.n8.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.DpSize
import co.early.fore.compose.observeAsState
import co.early.fore.ui.size.WindowSize
import co.early.fore.ui.size.toWindowSize
import foo.bar.clean.domain.DomainError
import foo.bar.clean.domain.features.ReadableState
import foo.bar.clean.ui.R
import foo.bar.n8.ui.theme.AppTheme

/**
 * convenience function to convert a fore Observable into a Compose State
 */
@Composable
fun <T: foo.bar.clean.domain.features.State> ReadableState<T>.toState(): State<T> = this.observeAsState { state }

@Composable
fun PreviewWithWindowSize(content: @Composable (size: WindowSize) -> Unit) {
    AppTheme {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content(DpSize(maxWidth, maxHeight).toWindowSize())
        }
    }
}

@StringRes
fun DomainError.mapToUserMessage(): Int {
    return when (this) {
        DomainError.RetryLater -> R.string.err_retry_later
        DomainError.RetryAfterNetworkCheck -> R.string.err_network
        DomainError.RetryAfterLogin -> R.string.err_session
        DomainError.NoError -> R.string.msg_empty_message
        DomainError.CheckAccount -> R.string.err_account
        DomainError.UpgradeForce -> R.string.err_upgrade_force
        DomainError.UpgradeNag -> R.string.err_upgrade_nag
        DomainError.BlankField -> R.string.err_blank_field
        DomainError.NoData -> R.string.err_no_data
    }
}

// https://gist.github.com/Mayankmkh/92084bdf2b59288d3e74c3735cccbf9f
fun Any.prettyPrint(): String {

    var indentLevel = 0
    val indentWidth = 4

    fun padding() = "".padStart(indentLevel * indentWidth)

    val toString = toString()//.replace("foo.bar.clean.domain.features.", "")

    val stringBuilder = StringBuilder(toString.length)

    var i = 0
    while (i < toString.length) {
        when (val char = toString[i]) {
            '(', '[', '{' -> {
                indentLevel++
                stringBuilder.appendLine(char).append(padding())
            }

            ')', ']', '}' -> {
                indentLevel--
                stringBuilder.appendLine().append(padding()).append(char)
            }

            ',' -> {
                stringBuilder.appendLine(char).append(padding())
                // ignore space after comma as we have added a newline
                val nextChar = toString.getOrElse(i + 1) { char }
                if (nextChar == ' ') i++
            }

            else -> {
                stringBuilder.append(char)
            }
        }
        i++
    }

    return stringBuilder.toString().replace("=", " = ")
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun showKeyboard(){
    // sometimes even in Compose we need to write asynchronous theatre code in the UI layer :/
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
            keyboardController?.show()
//        }
//    }
}

///**
// * lets us access koin's inject() function from a default parameter
// */
//inline fun <reified T : Any> koinInject(
//    qualifier: Qualifier? = null,
//    mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode(),
//    noinline parameters: ParametersDefinition? = null
//): T = KoinPlatformTools.defaultContext().get().inject<T>().value
