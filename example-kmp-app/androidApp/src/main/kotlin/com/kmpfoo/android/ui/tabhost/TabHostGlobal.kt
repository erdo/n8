package com.kmpfoo.android.ui.tabhost

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.early.n8.NavigationModel
import com.kmpfoo.ui.navigation.Location
import com.kmpfoo.ui.navigation.TabHostId
import com.kmpfoo.ui.navigation.tabHostSpecGlobal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabHostGlobal(
    selectedTabIndex: Int,
    navModel: NavigationModel<Location, TabHostId>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    val tabs = listOf("Global", "Europe")

    Column(
        modifier = modifier
    ) {
        PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { navModel.switchTab(tabHostSpecGlobal, index) }
                )
            }
        }
        content()
    }
}
