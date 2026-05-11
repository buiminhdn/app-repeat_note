package com.marcus.repeatnote.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.state.GlanceStateDefinition
import com.google.gson.Gson
import com.marcus.repeatnote.domain.model.RotationMode
import java.io.File

private val Context.widgetDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "widget_states"
)

object WidgetStateKeys {
    fun noteTitle(widgetId: Int) = stringPreferencesKey("title_$widgetId")
    fun noteContent(widgetId: Int) = stringPreferencesKey("content_$widgetId")
    fun category(widgetId: Int) = stringPreferencesKey("category_$widgetId")
    fun rotationMode(widgetId: Int) = stringPreferencesKey("rotation_$widgetId")
    fun totalNotes(widgetId: Int) = stringPreferencesKey("total_$widgetId")
    fun lastUpdated(widgetId: Int) = stringPreferencesKey("updated_$widgetId")
    fun isEmpty(widgetId: Int) = stringPreferencesKey("empty_$widgetId")
    fun emptyMessage(widgetId: Int) = stringPreferencesKey("empty_msg_$widgetId")
    fun stateJson(widgetId: Int) = stringPreferencesKey("state_$widgetId")
}

object WidgetStateSerializer {
    private val gson = Gson()

    fun serialize(state: WidgetUiState): String = gson.toJson(state)

    fun deserialize(json: String): WidgetUiState = try {
        gson.fromJson(json, WidgetUiState::class.java) ?: defaultState()
    } catch (e: Exception) {
        defaultState()
    }

    fun defaultState() = WidgetUiState()
}
