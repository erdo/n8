package foo.bar.n8.ui.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.clean.ui.actionhandlers.ActionHandler

/**
 * layout for the bottom navigation bar
 */
@Composable
fun <T : Act> BottomNavBar(
    bottomBarItems: List<NavigationItem>,
    userActionHandler: ActionHandler<T>,
) {
    if (bottomBarItems.isNotEmpty()) {
        NavigationBar {
            bottomBarItems.forEach { item ->
                NavigationBarItem(
                    icon = item.view,
                    label = {
                        Text(
                            text = item.localisedDescription,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false,
                            maxLines = 1,
                        )
                    },
                    selected = !item.enabled,
                    onClick = {
                        if (item.enabled) {
                            userActionHandler.handle(item.action)
                        }
                    }
                )
            }
        }
    }
}
