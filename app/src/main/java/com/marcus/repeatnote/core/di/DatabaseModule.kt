package com.marcus.repeatnote.core.di

import android.content.Context
import androidx.room.Room
import com.marcus.repeatnote.data.local.AppDatabase
import com.marcus.repeatnote.data.local.dao.MemoryNoteDao
import com.marcus.repeatnote.data.local.dao.WidgetConfigDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "repeat_note.db"
        ).build()

    @Provides
    fun provideMemoryNoteDao(db: AppDatabase): MemoryNoteDao = db.memoryNoteDao()

    @Provides
    fun provideWidgetConfigDao(db: AppDatabase): WidgetConfigDao = db.widgetConfigDao()
}
