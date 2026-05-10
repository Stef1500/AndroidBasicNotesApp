package com.zybooks.semesterproject.ui.theme.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zybooks.semesterproject.DSLogic
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SemesterViewModel : ViewModel() {

    private var dataStoreLogic: DSLogic? = null

    private val _selectedNote = MutableStateFlow("")
    val selectedNote: StateFlow<String> = _selectedNote.asStateFlow()

    private val _noteText = MutableStateFlow("")
    val noteText: StateFlow<String> = _noteText.asStateFlow()

    private val _notes = MutableStateFlow(listOf(""))
    val notes: StateFlow<List<String>> = _notes.asStateFlow()

    private val _noteImage = MutableStateFlow<String?>(null)
    val noteImage: StateFlow<String?> = _noteImage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true) // default dark mode
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private var noteJob: Job? = null

    fun setDataStore(ds: DSLogic) {
        dataStoreLogic = ds

        viewModelScope.launch {
            ds.getNotesListFlow().collect { list ->
                _notes.value = list
                if (!_notes.value.contains(_selectedNote.value) && _notes.value.isNotEmpty()) {
                    loadNote(_notes.value.first())
                }
            }
        }

        loadNote(_selectedNote.value)
    }

    fun loadNote(name: String) {
        _selectedNote.value = name

        noteJob?.cancel()
        noteJob = viewModelScope.launch {
            dataStoreLogic?.getNoteFlow(name)?.collect { text ->
                _noteText.value = text
            }
        }
        //loads image ONLY to this note
        viewModelScope.launch {
            dataStoreLogic?.getNoteFlow("${name}_image")?.collect { uri ->
                _noteImage.value = if (uri.isNotBlank()) uri else null
            }
        }
    }

    fun onNoteChange(newText: String) {
        _noteText.value = newText
        viewModelScope.launch {
            dataStoreLogic?.saveNote(_selectedNote.value, newText)
        }
    }

    fun addNote(name: String) {
        val updated = _notes.value + name
        _notes.value = updated
        viewModelScope.launch {
            dataStoreLogic?.saveNotesList(updated)
        }
        loadNote(name)
    }

    fun deleteNote(name: String) {
        val updated = _notes.value.filter { it != name }
        _notes.value = updated
        viewModelScope.launch {
            dataStoreLogic?.deleteNote(name)
            dataStoreLogic?.saveNotesList(updated)
        }
        //switches to another note when the current one is deleted
        if (_selectedNote.value == name && updated.isNotEmpty()) {
            loadNote(updated.first())
        } else if (updated.isEmpty()) {
            _noteText.value = ""
        }
    }
    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun onImageSelected(uri: String) {
        _noteImage.value = uri
        viewModelScope.launch {
            dataStoreLogic?.saveNote("${_selectedNote.value}_image", uri)
        }
    }
}