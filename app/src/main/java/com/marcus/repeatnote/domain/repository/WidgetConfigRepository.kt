package com.marcus.repeatnote.domain.repository

import com.marcus.repeatnote.domain.model.WidgetConfig

interface WidgetConfigRepository {
    suspend fun getWidgetConfig(widgetId: Int): WidgetConfig?
    suspend fun getAllWidgetConfigs(): List<WidgetConfig>
    suspend fun insertWidgetConfig(config: WidgetConfig)
    suspend fun updateWidgetConfig(config: WidgetConfig)
    suspend fun deleteWidgetConfig(widgetId: Int)
}
