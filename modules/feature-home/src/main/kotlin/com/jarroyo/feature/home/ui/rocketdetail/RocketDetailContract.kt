package com.jarroyo.feature.home.ui.rocketdetail

import androidx.compose.runtime.Immutable
import com.jarroyo.library.ui.shared.ViewEffect
import com.jarroyo.library.ui.shared.ViewEvent
import com.jarroyo.library.ui.shared.ViewState

@Immutable
object RocketDetailContract {
    data class State(
        val loading: Boolean = false,
    ) : ViewState

    sealed class Event : ViewEvent

    sealed class Effect : ViewEffect {
        data class ShowSnackbar(val message: String) : Effect()
    }
}
