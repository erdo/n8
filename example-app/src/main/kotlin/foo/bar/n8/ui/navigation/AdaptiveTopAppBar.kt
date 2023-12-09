package foo.bar.n8.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.MinDimBasedComposable
import foo.bar.clean.ui.R
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.clean.ui.actionhandlers.ActionHandler
import foo.bar.n8.ui.navigation.NavigationItemType.*

/**
 * top app bar of the app, includes the burger menu icon, the title and any actions
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Act> AdaptiveTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    titleText: String,
    startDrawerOpenRequested: () -> Unit,
    actionItems: List<NavigationItem> = emptyList(),
    userActionHandler: ActionHandler<T>,
): MinDimBasedComposable {

    Fore.d("AdaptiveTopAppBar()")

    return MinDimBasedComposable(
        s = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { TitleContent(titleText) },
                modifier = Modifier,
                navigationIcon = { NavigationIcon(startDrawerOpenRequested) },
                actions = {
                    Actions(
                        actionItems = actionItems,
                        userActionHandler = userActionHandler
                    )
                },
                windowInsets = TopAppBarDefaults.windowInsets,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
            )
        },
        l = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = { TitleContent(titleText) },
                modifier = Modifier,
                navigationIcon = { NavigationIcon(startDrawerOpenRequested) },
                actions = {
                    Actions(
                        actionItems = actionItems,
                        userActionHandler = userActionHandler
                    )
                },
                windowInsets = TopAppBarDefaults.windowInsets,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
            )
        },
    )
}

@Composable
private fun <T : Act> RowScope.Actions(
    actionItems: List<NavigationItem>,
    userActionHandler: ActionHandler<T>,
) {
    actionItems.forEach {
        when (it.type) {
            ICON -> {
                IconButton(
                    enabled = it.enabled,
                    onClick = { userActionHandler.handle(it.action) }
                ) {
                    it.view()
                }
            }

            ICON_TOGGLE -> {
                IconToggleButton(
                    enabled = it.enabled,
                    checked = it.checked,
                    onCheckedChange = { _ -> userActionHandler.handle(it.action) },
                ) {
                    it.view()
                }
            }

            SWITCH -> {
                Switch(
                    enabled = it.enabled,
                    checked = it.checked,
                    onCheckedChange = { _ -> userActionHandler.handle(it.action) },
                )
            }
        }
    }
}

@Composable
private fun NavigationIcon(navigationItemClick: () -> Unit) =
    IconButton(onClick = navigationItemClick) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = stringResource(id = R.string.menu)
        )
    }

@Composable
private fun TitleContent(titleText: String) = Text(
    modifier = Modifier,
    text = titleText,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)
