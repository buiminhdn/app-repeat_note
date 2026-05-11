package com.marcus.repeatnote.domain.usecase

import com.marcus.repeatnote.domain.model.WidgetConfig
import com.marcus.repeatnote.domain.repository.WidgetConfigRepository
import javax.inject.Inject

class SaveWidgetConfigUseCase @Inject constructor(
    private val repository: WidgetConfigRepository,
) {
    suspend operator fun invoke(config: WidgetConfig) {
        val existing = repository.getWidgetConfig(config.widgetId)
        if (existing != null) {
            repository.updateWidgetConfig(config)
        } else {
            repository.insertWidgetConfig(config)
        }
    }
}
