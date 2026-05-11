package com.marcus.repeatnote.feature_notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marcus.repeatnote.feature_notes.components.AddEditNoteSheet
import com.marcus.repeatnote.feature_notes.components.FilterChipRow
import com.marcus.repeatnote.feature_notes.components.NoteCard
import com.marcus.repeatnote.feature_notes.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Collect effects
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NotesContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(NotesContract.Event.AddNoteClicked) },
                shape = MaterialTheme.shapes.large,
                containerColor = Color(0xFF111111),
                contentColor = Color.White,
                modifier = Modifier.size(56.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add note",
                )
            }
        },
        containerColor = Color(0xFFF9F8F6),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // App bar area
            Text(
                text = "Repeat Note",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            )

            // Search bar
            SearchBar(
                query = state.searchQuery,
                onQueryChanged = {
                    viewModel.onEvent(NotesContract.Event.SearchQueryChanged(it))
                },
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter chips
            FilterChipRow(
                categories = state.categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = {
                    viewModel.onEvent(NotesContract.Event.CategorySelected(it))
                },
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section header
            Text(
                text = "LIST NOTES",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )

            // Content
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                }

                state.notes.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No notes yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap + to create your first note",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        itemsIndexed(
                            items = state.notes,
                            key = { _, note -> note.id },
                        ) { index, note ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 200,
                                        delayMillis = index * 30,
                                    )
                                ) + slideInVertically(
                                    animationSpec = tween(
                                        durationMillis = 200,
                                        delayMillis = index * 30,
                                    ),
                                    initialOffsetY = { it / 4 },
                                ),
                            ) {
                                NoteCard(
                                    note = note,
                                    onClick = {
                                        viewModel.onEvent(NotesContract.Event.NoteClicked(note))
                                    },
                                )
                            }
                        }

                        // Bottom spacing for FAB
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }

        // Bottom Sheet
        if (state.isBottomSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.onEvent(NotesContract.Event.BottomSheetDismissed)
                },
                sheetState = sheetState,
                modifier = Modifier.statusBarsPadding(),
                containerColor = Color(0xFFFFFFFF),
                shape = MaterialTheme.shapes.large,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .size(width = 32.dp, height = 4.dp)
                                .then(
                                    Modifier.padding(0.dp)
                                )
                        ) {
                            androidx.compose.material3.Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = MaterialTheme.shapes.small,
                                color = Color(0xFFDDDBD8),
                                content = {},
                            )
                        }
                    }
                },
            ) {
                AddEditNoteSheet(
                    editingNote = state.editingNote,
                    onSave = { title, content, category ->
                        viewModel.onEvent(
                            NotesContract.Event.NoteSaved(title, content, category)
                        )
                    },
                    onDelete = { note ->
                        viewModel.onEvent(NotesContract.Event.NoteDeleted(note))
                    },
                )
            }
        }
    }
}
