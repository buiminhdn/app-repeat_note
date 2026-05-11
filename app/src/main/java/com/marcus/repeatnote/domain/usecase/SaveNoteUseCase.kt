package com.marcus.repeatnote.domain.usecase

import com.marcus.repeatnote.domain.model.Note
import com.marcus.repeatnote.domain.repository.NoteRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(note: Note): Long {
        return if (note.id == 0L) {
            repository.insertNote(note)
        } else {
            repository.updateNote(note)
            note.id
        }
    }
}
