package foo.bar.n8.ui.common.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.early.fore.kt.core.delegate.Fore
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import foo.bar.clean.domain.DomainError
import foo.bar.clean.ui.R
import foo.bar.n8.ui.common.components.AlertOneButton
import foo.bar.n8.ui.common.components.AnimatedLinearProgressIndicator
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.common.components.elements.BtnType
import foo.bar.n8.ui.common.mapToUserMessage
import foo.bar.n8.ui.theme.LocalAppColors
import kotlinx.coroutines.delay

@Composable
fun SplashLoading(
    progress: Float,
) {

    Fore.i("SplashLoading progress:$progress")

    Background {

        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.orange_water_lottiefiles_com_sanjibpaul)
        )

        LottieAnimation(
            composition = composition,
            speed = 2f,
            iterations = LottieConstants.IterateForever,
        )
        var showProgress by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(true) {
            delay(1000)
            showProgress = true
        }

        Box(modifier = Modifier.height(10.dp)) {
            AnimatedLinearProgressIndicator(
                newProgress = progress,
                show = showProgress,
            )
        }
    }
}

@Composable
fun SplashError(
    title: String,
    error: DomainError,
    dismissed: () -> Unit = {},
) {
    Background {
        AlertOneButton(
            title = title,
            text = stringResource(id = error.mapToUserMessage()),
            confirmBtnSpec = BtnSpec(
                label = stringResource(id = R.string.btn_ok),
                clicked = dismissed,
                type = BtnType.Positive
            )
        )
    }
}

@Composable
fun Background(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = LocalAppColors.current.paper),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}
