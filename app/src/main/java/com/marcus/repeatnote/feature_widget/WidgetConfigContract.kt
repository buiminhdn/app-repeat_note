package com.marcus.repeatnote.feature_widget

import com.marcus.repeatnote.domain.model.Category
import com.marcus.repeatnote.domain.model.RotationMode

interface WidgetConfigContract {

    data class State(
        val widgetId: Int = 0,
        val selectedCategory: String? = null,
        val rotationMode: RotationMode = RotationMode.SEQUENTIAL,
        val categories: List<Category> = Category.entries,
        val isSaving: Boolean = false,
    )

    sealed interface Event {
        data class CategorySelected(val category: String?) : Event
        data class RotationModeSelected(val mode: RotationMode) : Event
        data object ConfirmClicked : Event
    }

    sealed interface Effect {
        data object ConfigSaved : Effect
        data class ShowError(val message: String) : Effect
    }
}
