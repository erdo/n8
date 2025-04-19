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
import androidx.compose.ui.unit.sp
import co.early.fore.core.delegate.Fore
import co.early.fore.ui.size.WindowSize
import co.early.n8.NavigationModel
import com.kmpfoo.android.OG
import com.kmpfoo.ui.navigation.Location

@Composable
fun LAScreen(
    size: WindowSize = WindowSize(),
) {

    Fore.i("LAScreen $size")

    val navigationModel: NavigationModel<Location, Unit> = OG[NavigationModel::class.java] as NavigationModel<Location, Unit>

    LAView(
        size = size,
        navigateBack = { navigationModel.navigateBack() },
    )
}

@Composable
fun LAView(
    size: WindowSize,
    navigateBack: () -> Unit = {},
) {

    Fore.getLogger().i("LAView")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = "LA",
            style =  TextStyle(
                fontSize = 50.sp,
            ),
        )

        Button(
            onClick = navigateBack
        ) {
            Text("Go Back")
        }

    }
}
