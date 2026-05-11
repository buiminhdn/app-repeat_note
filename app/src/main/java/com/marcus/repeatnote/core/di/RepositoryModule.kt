package com.marcus.repeatnote.core.di

import com.marcus.repeatnote.data.repository.NoteRepositoryImpl
import com.marcus.repeatnote.data.repository.WidgetConfigRepositoryImpl
import com.marcus.repeatnote.domain.repository.NoteRepository
import com.marcus.repeatnote.domain.repository.WidgetConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindWidgetConfigRepository(impl: WidgetConfigRepositoryImpl): WidgetConfigRepository
}
