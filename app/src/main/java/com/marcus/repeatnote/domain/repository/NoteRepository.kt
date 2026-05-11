package com.marcus.repeatnote.domain.repository

import com.marcus.repeatnote.domain.model.Category
import com.marcus.repeatnote.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getNotesByCategory(category: Category): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun getNotesByCategoryList(category: String?): List<Note>
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
}
