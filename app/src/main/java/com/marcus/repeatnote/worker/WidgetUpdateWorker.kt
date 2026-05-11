package com.marcus.repeatnote.worker

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.marcus.repeatnote.data.local.RecentlyShownStore
import com.marcus.repeatnote.domain.model.Note
import com.marcus.repeatnote.domain.model.RotationMode
import com.marcus.repeatnote.domain.repository.NoteRepository
import com.marcus.repeatnote.domain.repository.WidgetConfigRepository
import com.marcus.repeatnote.widget.MemoryWidget
import com.marcus.repeatnote.widget.WidgetStateKeys
import com.marcus.repeatnote.widget.WidgetStateSerializer
import com.marcus.repeatnote.widget.WidgetUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val widgetConfigRepo: WidgetConfigRepository,
    private val noteRepo: NoteRepository,
    private val recentlyShownStore: RecentlyShownStore,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val configs = widgetConfigRepo.getAllWidgetConfigs()
            val manager = GlanceAppWidgetManager(appContext)
            val glanceIds = manager.getGlanceIds(MemoryWidget::class.java)

            for (config in configs) {
                val notes = noteRepo.getNotesByCategoryList(config.categoryFilter)

                val uiState = if (notes.isEmpty()) {
                    val message = if (config.categoryFilter != null) {
                        "No notes in this category."
                    } else {
                        "No notes yet.\nOpen the app to add some."
                    }
                    WidgetUiState(
                        widgetId = config.widgetId,
                        isEmpty = true,
                        emptyMessage = message,
                    )
                } else {
                    val selectedNote = selectNote(config.widgetId, notes, config.rotationMode)
                    WidgetUiState(
                        widgetId = config.widgetId,
                        noteTitle = selectedNote.title,
                        noteContent = selectedNote.content,
                        category = selectedNote.category.name,
                        rotationMode = config.rotationMode,
                        totalNotes = notes.size,
                        lastUpdatedAt = System.currentTimeMillis(),
                        isEmpty = false,
                    )
                }

                // Update state for all Glance IDs
                for (glanceId in glanceIds) {
                    updateAppWidgetState(appContext, glanceId) { prefs ->
                        val mutablePrefs = prefs as MutablePreferences
                        mutablePrefs[WidgetStateKeys.stateJson(config.widgetId)] =
                            WidgetStateSerializer.serialize(uiState)
                        // Also store in generic key for simpler reading
                        mutablePrefs[WidgetStateKeys.stateJson(0)] =
                            WidgetStateSerializer.serialize(uiState)
                    }
                }

                // Update sequential index
                if (config.rotationMode == RotationMode.SEQUENTIAL && notes.isNotEmpty()) {
                    val newIndex = (config.currentIndex + 1) % notes.size
                    widgetConfigRepo.updateWidgetConfig(config.copy(currentIndex = newIndex))
                }
            }

            // Trigger Glance recomposition
            MemoryWidget().updateAll(appContext)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun selectNote(
        widgetId: Int,
        notes: List<Note>,
        mode: RotationMode,
    ): Note {
        if (notes.size == 1) return notes.first()

        return when (mode) {
            RotationMode.SEQUENTIAL -> {
                val config = widgetConfigRepo.getWidgetConfig(widgetId)
                val index = (config?.currentIndex ?: 0).coerceIn(0, notes.size - 1)
                notes[index]
            }

            RotationMode.RANDOM -> {
                val recentlyShown = recentlyShownStore.getRecentlyShown(widgetId)
                val candidates = notes.filter { it.id !in recentlyShown }

                val selected = if (candidates.isEmpty()) {
                    // All notes shown recently — reset and pick from full set
                    recentlyShownStore.clearRecentlyShown(widgetId)
                    notes.random()
                } else {
                    candidates.random()
                }

                recentlyShownStore.addRecentlyShown(widgetId, selected.id)
                selected
            }
        }
    }
}
