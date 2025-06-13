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

private val name = "Rhine"
private val nextLocation = Location.EuropeanLocation.Seine
private val navigationModel by lazy {
    com.kmpfoo.android.OG[NavigationModel::class.java] as NavigationModel<Location, TabHostId>
}

@Composable
fun ScreenRhine(
    modifier: Modifier = Modifier,
) {

    Fore.i("${name}Screen")

    RhineView(
        modifier = modifier,
        navigateToNext = { navigationModel.navigateTo(nextLocation) },
        navigateBack = { navigationModel.navigateBack() },
    )
}

@Composable
fun RhineView(
    modifier: Modifier,
    navigateToNext: () -> Unit = {},
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
            onClick = navigateBack
        ) {
            Text("Go Back")
        }

    }
}
