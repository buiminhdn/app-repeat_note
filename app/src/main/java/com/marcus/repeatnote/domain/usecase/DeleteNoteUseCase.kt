package com.marcus.repeatnote.domain.usecase

import com.marcus.repeatnote.domain.model.Note
import com.marcus.repeatnote.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}
