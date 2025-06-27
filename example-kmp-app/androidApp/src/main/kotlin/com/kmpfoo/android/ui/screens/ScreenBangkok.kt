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

private val name = "Bangkok"
private val nextLocation = Location.Dakar
private val navigationModel by lazy {
    OG[NavigationModel::class] as NavigationModel<Location, TabHostId>
}

@Composable
fun ScreenBangkok(
    location: Location.Bangkok,
    modifier: Modifier = Modifier,
) {

    Fore.i("${name}Screen")

    BangkokView(
        location = location,
        modifier = modifier,
        navigateToNext = { navigationModel.navigateTo(nextLocation) },
        navigateBack = { navigationModel.navigateBack() },
    )
}

@Composable
fun BangkokView(
    location: Location.Bangkok,
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
            onClick = navigateBack
        ) {
            Text("Go Back")
        }

    }
}
