package com.marcus.repeatnote.domain.model

data class WidgetConfig(
    val widgetId: Int,
    val categoryFilter: String? = null,
    val rotationMode: RotationMode = RotationMode.SEQUENTIAL,
    val currentIndex: Int = 0,
)
