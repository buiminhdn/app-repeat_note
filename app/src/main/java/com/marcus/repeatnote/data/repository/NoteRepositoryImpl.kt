package com.marcus.repeatnote.data.repository

import com.marcus.repeatnote.data.local.dao.MemoryNoteDao
import com.marcus.repeatnote.data.mapper.toDomain
import com.marcus.repeatnote.data.mapper.toEntity
import com.marcus.repeatnote.domain.model.Category
import com.marcus.repeatnote.domain.model.Note
import com.marcus.repeatnote.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val dao: MemoryNoteDao,
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> =
        dao.getAllNotes().map { entities -> entities.map { it.toDomain() } }

    override fun getNotesByCategory(category: Category): Flow<List<Note>> =
        dao.getNotesByCategory(category.name).map { entities -> entities.map { it.toDomain() } }

    override fun searchNotes(query: String): Flow<List<Note>> =
        dao.searchNotes(query).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getNoteById(id: Long): Note? =
        dao.getNoteById(id)?.toDomain()

    override suspend fun getNotesByCategoryList(category: String?): List<Note> =
        if (category != null) {
            dao.getNotesByCategoryAsc(category).map { it.toDomain() }
        } else {
            dao.getAllNotesAsc().map { it.toDomain() }
        }

    override suspend fun insertNote(note: Note): Long =
        dao.insertNote(note.toEntity())

    override suspend fun updateNote(note: Note) =
        dao.updateNote(note.toEntity())

    override suspend fun deleteNote(note: Note) =
        dao.deleteNote(note.toEntity())
}
