package com.marcus.repeatnote.feature_notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcus.repeatnote.domain.model.Category
import com.marcus.repeatnote.domain.model.Note
import com.marcus.repeatnote.domain.usecase.DeleteNoteUseCase
import com.marcus.repeatnote.domain.usecase.GetNotesUseCase
import com.marcus.repeatnote.domain.usecase.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getNotes: GetNotesUseCase,
    private val saveNote: SaveNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesContract.State())
    private val _effects = Channel<NotesContract.Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    // Derive the note list reactively from category + search query changes
    private val notesFlow = combine(
        _uiState,
        _uiState
    ) { state, _ ->
        state
    }.flatMapLatest { state ->
        when {
            state.searchQuery.isNotBlank() -> getNotes.search(state.searchQuery)
            state.selectedCategory != null -> getNotes.getByCategory(state.selectedCategory)
            else -> getNotes.getAllNotes()
        }
    }

    val state: StateFlow<NotesContract.State> = combine(
        _uiState,
        notesFlow
    ) { uiState, notes ->
        uiState.copy(notes = notes, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesContract.State(isLoading = true),
    )

    fun onEvent(event: NotesContract.Event) {
        when (event) {
            is NotesContract.Event.SearchQueryChanged -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }

            is NotesContract.Event.CategorySelected -> {
                _uiState.value = _uiState.value.copy(
                    selectedCategory = event.category,
                    searchQuery = "",
                )
            }

            is NotesContract.Event.NoteClicked -> {
                _uiState.value = _uiState.value.copy(
                    isBottomSheetOpen = true,
                    editingNote = event.note,
                )
            }

            is NotesContract.Event.AddNoteClicked -> {
                _uiState.value = _uiState.value.copy(
                    isBottomSheetOpen = true,
                    editingNote = null,
                )
            }

            is NotesContract.Event.BottomSheetDismissed -> {
                _uiState.value = _uiState.value.copy(
                    isBottomSheetOpen = false,
                    editingNote = null,
                )
            }

            is NotesContract.Event.NoteSaved -> {
                viewModelScope.launch {
                    try {
                        val existing = _uiState.value.editingNote
                        val note = Note(
                            id = existing?.id ?: 0L,
                            title = event.title,
                            content = event.content,
                            category = event.category,
                            createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                        )
                        saveNote(note)
                        _uiState.value = _uiState.value.copy(
                            isBottomSheetOpen = false,
                            editingNote = null,
                        )
                    } catch (e: Exception) {
                        _effects.send(
                            NotesContract.Effect.ShowError(
                                e.message ?: "Failed to save note"
                            )
                        )
                    }
                }
            }

            is NotesContract.Event.NoteDeleted -> {
                viewModelScope.launch {
                    try {
                        deleteNote(event.note)
                        _uiState.value = _uiState.value.copy(
                            isBottomSheetOpen = false,
                            editingNote = null,
                        )
                    } catch (e: Exception) {
                        _effects.send(
                            NotesContract.Effect.ShowError(
                                e.message ?: "Failed to delete note"
                            )
                        )
                    }
                }
            }
        }
    }
}
