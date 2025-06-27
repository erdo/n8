package com.kmpfoo.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import co.early.fore.core.delegate.Fore
import co.early.n8.NavigationModel
import com.kmpfoo.android.ui.theme.Dimens
import com.kmpfoo.ui.navigation.Location
import com.kmpfoo.ui.navigation.TabHostId
import com.kmpfoo.ui.navigation.tabHostSpecGlobal

private val name = "Paris"
private val nextLocation = Location.EuropeanLocation.London
private val customNavigation = {
    navigationModel.switchTab(tabHostSpecGlobal, 0)
    navigationModel.navigateTo(Location.Dakar)
}
private val navigationModel by lazy {
    com.kmpfoo.android.OG[NavigationModel::class] as NavigationModel<Location, TabHostId>
}

@Composable
fun ScreenParis(
    modifier: Modifier = Modifier,
) {

    Fore.i("${name}Screen")

    ParisView(
        modifier = modifier,
        navigateToNext = { navigationModel.navigateTo(nextLocation) },
        navigateCustom = customNavigation,
        navigateBack = { navigationModel.navigateBack() },
    )
}

@Composable
fun ParisView(
    modifier: Modifier,
    navigateToNext: () -> Unit = {},
    navigateCustom: () -> Unit = {},
    navigateBack: () -> Unit = {},
) {

    Fore.getLogger().i("${name}View")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .then(modifier),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = name,
            style = TextStyle(
                fontSize = Dimens.fontSizeM,
            ),
        )

        Button(
            onClick = navigateToNext
        ) {
            Text("Go To Next")
        }
        Button(
            onClick = navigateCustom
        ) {
            Text("Global Tab & Goto Dakar")
        }
        Button(
            onClick = navigateBack
        ) {
            Text("Go Back")
        }

    }
}
