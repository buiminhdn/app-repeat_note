package com.marcus.repeatnote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.marcus.repeatnote.data.local.entity.WidgetConfigEntity

@Dao
interface WidgetConfigDao {

    @Query("SELECT * FROM widget_configs WHERE widgetId = :widgetId")
    suspend fun getWidgetConfig(widgetId: Int): WidgetConfigEntity?

    @Query("SELECT * FROM widget_configs")
    suspend fun getAllWidgetConfigs(): List<WidgetConfigEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgetConfig(config: WidgetConfigEntity)

    @Update
    suspend fun updateWidgetConfig(config: WidgetConfigEntity)

    @Query("DELETE FROM widget_configs WHERE widgetId = :widgetId")
    suspend fun deleteWidgetConfig(widgetId: Int)
}
