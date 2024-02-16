package com.jarroyo.feature.home.shared.ui.rocketdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jarroyo.feature.home.shared.di.FeatureHomeKoinComponent
import com.jarroyo.feature.home.shared.ui.rocketdetail.RocketDetailContract.Effect
import com.jarroyo.feature.home.shared.ui.rocketdetail.RocketDetailContract.Event
import com.jarroyo.feature.home.shared.ui.rocketdetail.RocketDetailContract.State
import com.jarroyo.library.navigation.di.NavigationKoinComponent
import com.jarroyo.library.ui.shared.component.placeholder
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModel

@Composable
fun RocketDetailScreen(
    arguments: Map<String, String>? = null,
    viewModel: RocketDetailViewModel = viewModel {
        RocketDetailViewModel(
            NavigationKoinComponent().appNavigator,
            FeatureHomeKoinComponent().getLaunchDetailInteractor,
        )
    },
) {
    LaunchedEffect(Unit) {
        arguments?.get("id")?.let {
            viewModel.onUiEvent(Event.OnViewAttached(it))
        }
    }

    RocketDetailScreen(
        effectFlow = viewModel.effect,
        sendEvent = { viewModel.onUiEvent(it) },
        state = viewModel.viewState.value,
    )
}

@Composable
private fun RocketDetailScreen(
    effectFlow: Flow<Effect>,
    sendEvent: (event: Event) -> Unit,
    state: State,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(effectFlow) {
        effectFlow.onEach { effect ->
            when (effect) {
                is Effect.ShowSnackbar -> launch {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Indefinite,
                    ).also { snackbarResult ->
                        if (snackbarResult == SnackbarResult.ActionPerformed) {
                            snackbarHostState.currentSnackbarData?.dismiss()
                        }
                    }
                }
            }
        }.collect()
    }
    Scaffold(topBar = { TopAppBar(sendEvent, state) }) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = state.launch?.details.orEmpty(),
                modifier = Modifier.placeholder(state.loading),
            )
            Text(
                text = state.launch?.links?.article_link.orEmpty(),
                modifier = Modifier.placeholder(state.loading),
            )
            KamelImage(
                resource = asyncPainterResource(
                    state.launch?.links?.flickr_images?.firstOrNull().orEmpty(),
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().placeholder(state.loading),
                alignment = Alignment.TopCenter,
                onLoading = { CircularProgressIndicator() },
            )
        }
    }
}

@Composable
private fun TopAppBar(
    sendEvent: (event: Event) -> Unit,
    state: State,
) {
    TopAppBar(
        title = { Text(state.launch?.mission_name.orEmpty()) },
        navigationIcon =
        {
            IconButton(
                onClick = { sendEvent(Event.OnUpButtonClicked) },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        },
    )
}
