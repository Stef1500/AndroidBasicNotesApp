package com.zybooks.semesterproject

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DSLogic(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore("notes_prefs")
        val NOTES_LIST_KEY = stringPreferencesKey("notes_list")
    }

    //saves notes to datastore
    suspend fun saveNote(key: String, note: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = note
        }
    }

    fun getNoteFlow(key: String): Flow<String> {
        return context.dataStore.data
            .map { prefs -> prefs[stringPreferencesKey(key)] ?: "" }
    }


    suspend fun saveNotesList(notes: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[NOTES_LIST_KEY] = notes.joinToString(",")
        }
    }

    fun getNotesListFlow(): Flow<List<String>> {
        return context.dataStore.data.map { prefs ->
            prefs[NOTES_LIST_KEY]?.split(",")?.filter { it.isNotBlank() } ?: listOf()
        }
    }
    suspend fun deleteNote(key: String) {
        context.dataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey(key))
        }
    }
}
