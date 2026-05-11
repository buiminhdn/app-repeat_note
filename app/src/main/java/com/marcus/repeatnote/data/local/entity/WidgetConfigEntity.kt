package com.marcus.repeatnote.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widget_configs")
data class WidgetConfigEntity(
    @PrimaryKey val widgetId: Int,
    val categoryFilter: String?,
    val rotationMode: String,  // "SEQUENTIAL" or "RANDOM"
    val currentIndex: Int = 0,
)
