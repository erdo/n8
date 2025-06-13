package com.kmpfoo.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import co.early.fore.core.delegate.Fore
import co.early.n8.NavigationModel
import com.kmpfoo.android.OG
import com.kmpfoo.android.ui.theme.Dimens
import com.kmpfoo.ui.navigation.Location
import com.kmpfoo.ui.navigation.Location.EuropeanLocation.Milan
import com.kmpfoo.ui.navigation.TabHostId

private val name = "New York"
private val nextLocation = Location.Bangkok()
private val customNavigation = {
    navigationModel.switchTab(1)
    Unit
}
private val navigationModel by lazy {
    OG[NavigationModel::class.java] as NavigationModel<Location, TabHostId>
}

@Composable
fun ScreenNewYork(
    modifier: Modifier = Modifier,
) {

    Fore.i("${name}Screen")

    NewYorkView(
        modifier = modifier,
        navigateToNext = { navigationModel.navigateTo(nextLocation) },
        navigateCustom = customNavigation,
        navigateBack = { navigationModel.navigateBack() },
    )
}

@Composable
fun NewYorkView(
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
            Text("Switch to Europe")
        }
        Button(
            onClick = navigateBack
        ) {
            Text("Go Back")
        }

    }
}
