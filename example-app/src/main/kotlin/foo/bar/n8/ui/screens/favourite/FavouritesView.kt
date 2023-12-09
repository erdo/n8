package foo.bar.n8.ui.screens.favourite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import foo.bar.clean.domain.features.favourites.FavouritesState
import foo.bar.clean.domain.features.favourites.Feature
import foo.bar.clean.ui.R
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.n8.ui.common.components.StateWrapperView
import foo.bar.n8.ui.common.components.ViewTemplate
import foo.bar.n8.ui.common.components.elements.Btn
import foo.bar.n8.ui.common.components.elements.BtnSpec
import foo.bar.n8.ui.common.components.elements.SH2
import foo.bar.n8.ui.common.components.elements.SW2
import foo.bar.n8.ui.common.components.elements.Txt
import foo.bar.n8.ui.common.components.extraPaddingForHideUiBtn

@Composable
fun FavouritesView(
    favouritesState: FavouritesState,
    perform: (Act) -> Unit = {},
    size: WindowSize = LocalWindowSize.current,
) {
    StateWrapperView(
        state = favouritesState,
        size = size,
    ) {
        ViewTemplate(modifier = Modifier.fillMaxSize()) {
            Favourites(
                favouritesState = favouritesState,
                size = size,
                clearAllBtnClicked = { perform(Act.ScreenFavourites.ClearAllFavourites) },
                clearBtnClicked = { fav -> perform(Act.ScreenFavourites.ClearFavourite(fav)) },
                addToFavouritesBtnClicked = { perform(Act.Global.ToggleFavourite(feature = Feature.Favourites)) },
            )
        }
    }
}

@Composable
fun ColumnScope.Favourites(
    favouritesState: FavouritesState,
    size: WindowSize = LocalWindowSize.current,
    clearAllBtnClicked: () -> Unit = {},
    clearBtnClicked: (Feature) -> Unit = {},
    addToFavouritesBtnClicked: () -> Unit = {},
) {

    Fore.i("Favourites View")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = SH2(size))
    ) {

        if (favouritesState.favourites.isEmpty()) {
            Column(
                modifier = Modifier.align(Center),
                horizontalAlignment =  Alignment.CenterHorizontally
            ) {
                Txt(
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.favourites_description)
                )
                IconToggleButton(
                    enabled = true,
                    checked = favouritesState.isFavourite(Feature.Favourites),
                    onCheckedChange = { addToFavouritesBtnClicked() },
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = stringResource(id = R.string.favourites)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = extraPaddingForHideUiBtn(size),
                        bottom = extraPaddingForHideUiBtn(size),
                    )
            ) {
                this.items(
                    items = favouritesState.favourites,
                    key = { feature -> feature.toString() }
                ) { feature ->
                    Row(verticalAlignment =  CenterVertically) {
                        Btn(
                            spec = BtnSpec(
                                label = stringResource(id = R.string.btn_clear),
                                clicked = { clearBtnClicked(feature) }
                            ),
                        )
                        Spacer(modifier = Modifier.width(SW2(size)))
                        Txt(text = feature.toString())
                    }
                }
            }
        }

        Btn(
            spec = BtnSpec(
                label = stringResource(id = R.string.favourites_clear_all),
                clicked = clearAllBtnClicked
            ),
            modifier = Modifier.align(BottomCenter),
            enabled = favouritesState.favourites.isNotEmpty()
        )
    }
}
