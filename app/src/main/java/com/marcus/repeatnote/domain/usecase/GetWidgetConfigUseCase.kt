package com.marcus.repeatnote.domain.usecase

import com.marcus.repeatnote.domain.model.WidgetConfig
import com.marcus.repeatnote.domain.repository.WidgetConfigRepository
import javax.inject.Inject

class GetWidgetConfigUseCase @Inject constructor(
    private val repository: WidgetConfigRepository,
) {
    suspend fun getById(widgetId: Int): WidgetConfig? = repository.getWidgetConfig(widgetId)
    suspend fun getAll(): List<WidgetConfig> = repository.getAllWidgetConfigs()
}
