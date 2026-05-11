package com.marcus.repeatnote.data.mapper

import com.marcus.repeatnote.data.local.entity.MemoryNoteEntity
import com.marcus.repeatnote.data.local.entity.WidgetConfigEntity
import com.marcus.repeatnote.domain.model.Category
import com.marcus.repeatnote.domain.model.Note
import com.marcus.repeatnote.domain.model.RotationMode
import com.marcus.repeatnote.domain.model.WidgetConfig

fun MemoryNoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    category = try {
        Category.valueOf(category)
    } catch (_: IllegalArgumentException) {
        Category.CUSTOM
    },
    createdAt = createdAt,
)

fun Note.toEntity(): MemoryNoteEntity = MemoryNoteEntity(
    id = id,
    title = title,
    content = content,
    category = category.name,
    createdAt = createdAt,
)

fun WidgetConfigEntity.toDomain(): WidgetConfig = WidgetConfig(
    widgetId = widgetId,
    categoryFilter = categoryFilter,
    rotationMode = try {
        RotationMode.valueOf(rotationMode)
    } catch (_: IllegalArgumentException) {
        RotationMode.SEQUENTIAL
    },
    currentIndex = currentIndex,
)

fun WidgetConfig.toEntity(): WidgetConfigEntity = WidgetConfigEntity(
    widgetId = widgetId,
    categoryFilter = categoryFilter,
    rotationMode = rotationMode.name,
    currentIndex = currentIndex,
)
