package foo.bar.n8.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.clean.ui.actionhandlers.ActionHandler

/**
 * Content scaffold for the main content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Act> ContentScaffold(
    size: WindowSize = LocalWindowSize.current,
    bottomBarItems: List<NavigationItem> = emptyList(),
    title: String,
    mainContent: @Composable (PaddingValues) -> Unit,
    startDrawerOpenRequested: () -> Unit,
    actionItems: List<NavigationItem> = emptyList(),
    userActionHandler: ActionHandler<T>,
) {

    Fore.d("ContentScaffold()")

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AdaptiveTopAppBar(
                scrollBehavior = scrollBehavior,
                titleText = title,
                startDrawerOpenRequested = startDrawerOpenRequested,
                actionItems = actionItems,
                userActionHandler = userActionHandler
            )(size)
        },
        content = { scaffoldPadding ->
            val passingValues = PaddingValues(
                end = scaffoldPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp, // https://stackoverflow.com/a/76029778
                start = scaffoldPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                top = scaffoldPadding.calculateTopPadding() + 8.dp,
                bottom = scaffoldPadding.calculateBottomPadding()
            )
            mainContent(passingValues)
        },
        bottomBar = {
            BottomNavBar(
                bottomBarItems = bottomBarItems,
                userActionHandler = userActionHandler,
            )
        }
    )
}
