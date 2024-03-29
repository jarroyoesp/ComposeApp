package com.jarroyo.feature.home.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.jarroyo.composeapp.library.network.api.graphql.fragment.LaunchFragment
import com.jarroyo.feature.home.shared.di.FeatureHomeKoinComponent
import com.jarroyo.feature.home.shared.ui.HomeContract.Effect
import com.jarroyo.feature.home.shared.ui.HomeContract.Event
import com.jarroyo.feature.home.shared.ui.HomeContract.State
import com.jarroyo.feature.home.shared.ui.launchdetail.getPlaceholderData
import com.jarroyo.library.navigation.di.NavigationKoinComponent
import com.jarroyo.library.ui.shared.theme.Spacing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = moe.tlaster.precompose.viewmodel.viewModel(modelClass = HomeViewModel::class) {
        HomeViewModel(
            NavigationKoinComponent().appNavigator,
            FeatureHomeKoinComponent().getFavoritesInteractor,
            FeatureHomeKoinComponent().getLaunchesInteractor,
        )
    },
) {
    LaunchedEffect(viewModel) {
        viewModel.onUiEvent(Event.OnViewAttached)
    }
    HomeScreen(
        state = viewModel.viewState.value,
        sendEvent = { viewModel.onUiEvent(it) },
        effectFlow = viewModel.effect,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HomeScreen(
    state: State,
    effectFlow: Flow<Effect>,
    sendEvent: (event: Event) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(effectFlow) {
        effectFlow.onEach { effect ->
            when (effect) {
                is Effect.ShowSnackbar -> launch {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }.collect()
    }
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scrollState = rememberLazyListState()
    Scaffold(
        topBar = {
            Box(modifier = Modifier.background(MaterialTheme.colors.primary)) {
                TopAppBar(
                    modifier = Modifier.padding(
                        start = SafeArea.current.value.calculateStartPadding(LayoutDirection.Ltr),
                        top = SafeArea.current.value.calculateTopPadding(),
                        end = SafeArea.current.value.calculateEndPadding(LayoutDirection.Ltr),
                    ),
                    title = { Text("Space X launches") },
                )
            }
        },
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { scaffoldPadding ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = state.loading,
            onRefresh = {
                sendEvent(Event.OnSwipeToRefresh)
            },
        )
        Box(
            modifier = Modifier
                .padding(scaffoldPadding)
                .pullRefresh(pullRefreshState),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                state = scrollState,
                contentPadding = PaddingValues(Spacing.x02),
                verticalArrangement = Arrangement.spacedBy(Spacing.x01),
            ) {
                if (!state.rocketList.isNullOrEmpty() || state.loading) {
                    if (state.rocketList.isNullOrEmpty() && state.loading) {
                        rocketList(getLaunchListPlaceholderData(), sendEvent, placeholder = true)
                    } else {
                        state.rocketList?.let { rockets ->
                            rocketList(
                                data = rockets,
                                sendEvent = sendEvent,
                                favoritesList = state.favoritesList,
                            )
                        }
                    }
                }
            }
            PullRefreshIndicator(
                state.loading,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter),
            )
        }
    }
}

private fun LazyListScope.rocketList(
    data: List<LaunchFragment>,
    sendEvent: (event: Event) -> Unit,
    favoritesList: List<String>? = null,
    placeholder: Boolean = false,
) {
    items(data.size) { index ->
        HomeItem(
            item = data[index],
            sendEvent = sendEvent,
            favoritesList = favoritesList,
            placeholder = placeholder,
        )
    }
}

private fun getLaunchListPlaceholderData(): List<LaunchFragment> = List(6) {
    getPlaceholderData()
}
