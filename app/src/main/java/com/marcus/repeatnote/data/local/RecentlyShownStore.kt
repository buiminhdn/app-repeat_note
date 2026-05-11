package com.marcus.repeatnote.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.recentlyShownDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "recently_shown"
)

@Singleton
class RecentlyShownStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val gson = Gson()
    private val listType = object : TypeToken<List<Long>>() {}.type

    private fun keyFor(widgetId: Int) = stringPreferencesKey("recent_ids_$widgetId")

    suspend fun getRecentlyShown(widgetId: Int): List<Long> {
        val key = keyFor(widgetId)
        val json = context.recentlyShownDataStore.data
            .map { prefs -> prefs[key] }
            .first()
        return if (json != null) {
            gson.fromJson(json, listType) ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun addRecentlyShown(widgetId: Int, noteId: Long) {
        val key = keyFor(widgetId)
        val current = getRecentlyShown(widgetId).toMutableList()
        current.add(0, noteId)
        // Trim to last 5 entries
        val trimmed = current.take(5)
        context.recentlyShownDataStore.edit { prefs ->
            prefs[key] = gson.toJson(trimmed)
        }
    }

    suspend fun clearRecentlyShown(widgetId: Int) {
        val key = keyFor(widgetId)
        context.recentlyShownDataStore.edit { prefs ->
            prefs.remove(key)
        }
    }
}
