package com.marcus.repeatnote.widget

import com.marcus.repeatnote.domain.model.RotationMode

data class WidgetUiState(
    val widgetId: Int = 0,
    val noteTitle: String = "",
    val noteContent: String = "",
    val category: String = "",
    val rotationMode: RotationMode = RotationMode.SEQUENTIAL,
    val totalNotes: Int = 0,
    val lastUpdatedAt: Long = 0L,
    val isEmpty: Boolean = true,
    val emptyMessage: String = "No notes yet.\nOpen the app to add some.",
)
