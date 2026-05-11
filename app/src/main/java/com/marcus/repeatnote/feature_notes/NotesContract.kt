package com.marcus.repeatnote.feature_notes

import com.marcus.repeatnote.domain.model.Category
import com.marcus.repeatnote.domain.model.Note

interface NotesContract {

    data class State(
        val notes: List<Note> = emptyList(),
        val categories: List<Category> = Category.entries,
        val selectedCategory: Category? = null,
        val searchQuery: String = "",
        val isBottomSheetOpen: Boolean = false,
        val editingNote: Note? = null,
        val isLoading: Boolean = false,
    )

    sealed interface Event {
        data class SearchQueryChanged(val query: String) : Event
        data class CategorySelected(val category: Category?) : Event
        data class NoteClicked(val note: Note) : Event
        data object AddNoteClicked : Event
        data object BottomSheetDismissed : Event
        data class NoteSaved(
            val title: String,
            val content: String,
            val category: Category,
        ) : Event
        data class NoteDeleted(val note: Note) : Event
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
    }
}
