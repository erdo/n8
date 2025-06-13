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
import com.kmpfoo.ui.navigation.TabHostId

private val name = "Welcome"
private val nextLocation = Location.Home
private val navigationModel by lazy {
    OG[NavigationModel::class.java] as NavigationModel<Location, TabHostId>
}

@Composable
fun ScreenWelcome(
    modifier: Modifier = Modifier,
) {

    Fore.i("${name}Screen")

    WelcomeView(
        modifier = modifier,
        navigateToNext = { navigationModel.navigateTo(nextLocation) },
    )
}

@Composable
fun WelcomeView(
    modifier: Modifier,
    navigateToNext: () -> Unit = {},
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
            text = "Welcome screen",
            style = TextStyle(
                fontSize = Dimens.fontSizeM,
            ),
        )

        Text(
            text = "(one time view)",
            style = TextStyle(
                fontSize = Dimens.fontSizeS,
            ),
        )

        Button(
            onClick = navigateToNext,
            modifier = modifier.padding(10.dp)
        ) {
            Text("Go To Home")
        }
    }
}
