package com.marcus.repeatnote.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marcus.repeatnote.data.local.dao.MemoryNoteDao
import com.marcus.repeatnote.data.local.dao.WidgetConfigDao
import com.marcus.repeatnote.data.local.entity.MemoryNoteEntity
import com.marcus.repeatnote.data.local.entity.WidgetConfigEntity

@Database(
    entities = [MemoryNoteEntity::class, WidgetConfigEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memoryNoteDao(): MemoryNoteDao
    abstract fun widgetConfigDao(): WidgetConfigDao
}
