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
import com.kmpfoo.android.ui.theme.Dimens
import com.kmpfoo.ui.navigation.Location
import com.kmpfoo.ui.navigation.Location.EuropeanLocation.Milan
import com.kmpfoo.ui.navigation.TabHostId
import com.kmpfoo.ui.navigation.tabHostSpecGlobal

private val name = "Milan"
private val nextLocation = Location.EuropeanLocation.Paris
private val customNavigation = {
    // the reason we switch tabs first here is because in the event that
    // the Bangkok location is not found in the back path, the location
    // Bangkok will be created in place and in that case we want it created
    // in the correct tabHost and on the correct tab
    navigationModel.switchTab(tabHostSpecGlobal, 0)
    navigationModel.navigateBackTo(Location.Bangkok("Message back to Bangkok from Milan"))
    Unit
}
private val navigationModel by lazy {
    com.kmpfoo.android.OG[NavigationModel::class.java] as NavigationModel<Location, TabHostId>
}

@Composable
fun ScreenMilan(
    location: Milan,
    modifier: Modifier = Modifier,
) {

    Fore.i("${name}Screen")

    MilanView(
        location = location,
        modifier = modifier,
        navigateToNext = { navigationModel.navigateTo(nextLocation) },
        navigateCustom = customNavigation,
        navigateBack = { navigationModel.navigateBack() },
    )
}

@Composable
fun MilanView(
    location: Milan,
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

        location.message?.let {
            Text(
                text = it,
                style = TextStyle(
                    fontSize = Dimens.fontSizeS,
                ),
                modifier = Modifier.padding(20.dp)
            )
        }

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
            Text("Message back to Bangkok")
        }
        Button(
            onClick = navigateBack
        ) {
            Text("Go Back")
        }

    }
}
