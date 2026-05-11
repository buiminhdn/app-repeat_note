package com.marcus.repeatnote.domain.usecase

import com.marcus.repeatnote.domain.model.Category
import com.marcus.repeatnote.domain.model.Note
import com.marcus.repeatnote.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    fun getAllNotes(): Flow<List<Note>> = repository.getAllNotes()

    fun getByCategory(category: Category): Flow<List<Note>> =
        repository.getNotesByCategory(category)

    fun search(query: String): Flow<List<Note>> = repository.searchNotes(query)

    suspend fun getByCategoryName(categoryName: String?): List<Note> =
        repository.getNotesByCategoryList(categoryName)
}
