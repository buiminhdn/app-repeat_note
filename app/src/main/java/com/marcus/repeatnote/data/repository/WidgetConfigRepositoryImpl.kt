package com.marcus.repeatnote.data.repository

import com.marcus.repeatnote.data.local.dao.WidgetConfigDao
import com.marcus.repeatnote.data.mapper.toDomain
import com.marcus.repeatnote.data.mapper.toEntity
import com.marcus.repeatnote.domain.model.WidgetConfig
import com.marcus.repeatnote.domain.repository.WidgetConfigRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetConfigRepositoryImpl @Inject constructor(
    private val dao: WidgetConfigDao,
) : WidgetConfigRepository {

    override suspend fun getWidgetConfig(widgetId: Int): WidgetConfig? =
        dao.getWidgetConfig(widgetId)?.toDomain()

    override suspend fun getAllWidgetConfigs(): List<WidgetConfig> =
        dao.getAllWidgetConfigs().map { it.toDomain() }

    override suspend fun insertWidgetConfig(config: WidgetConfig) =
        dao.insertWidgetConfig(config.toEntity())

    override suspend fun updateWidgetConfig(config: WidgetConfig) =
        dao.updateWidgetConfig(config.toEntity())

    override suspend fun deleteWidgetConfig(widgetId: Int) =
        dao.deleteWidgetConfig(widgetId)
}
