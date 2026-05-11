package com.marcus.repeatnote.feature_widget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcus.repeatnote.domain.model.RotationMode
import com.marcus.repeatnote.domain.model.WidgetConfig
import com.marcus.repeatnote.domain.usecase.SaveWidgetConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetConfigViewModel @Inject constructor(
    private val saveWidgetConfig: SaveWidgetConfigUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(WidgetConfigContract.State())
    val state: StateFlow<WidgetConfigContract.State> = _state.asStateFlow()

    private val _effects = Channel<WidgetConfigContract.Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun setWidgetId(widgetId: Int) {
        _state.value = _state.value.copy(widgetId = widgetId)
    }

    fun onEvent(event: WidgetConfigContract.Event) {
        when (event) {
            is WidgetConfigContract.Event.CategorySelected -> {
                _state.value = _state.value.copy(selectedCategory = event.category)
            }

            is WidgetConfigContract.Event.RotationModeSelected -> {
                _state.value = _state.value.copy(rotationMode = event.mode)
            }

            is WidgetConfigContract.Event.ConfirmClicked -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isSaving = true)
                    try {
                        val config = WidgetConfig(
                            widgetId = _state.value.widgetId,
                            categoryFilter = _state.value.selectedCategory,
                            rotationMode = _state.value.rotationMode,
                            currentIndex = 0,
                        )
                        saveWidgetConfig(config)
                        _effects.send(WidgetConfigContract.Effect.ConfigSaved)
                    } catch (e: Exception) {
                        _effects.send(
                            WidgetConfigContract.Effect.ShowError(
                                e.message ?: "Failed to save config"
                            )
                        )
                    } finally {
                        _state.value = _state.value.copy(isSaving = false)
                    }
                }
            }
        }
    }
}
