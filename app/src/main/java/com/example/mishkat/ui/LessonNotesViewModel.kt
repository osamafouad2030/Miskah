package com.example.mishkat.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mishkat.data.notes.LessonNoteEntity
import com.example.mishkat.data.notes.LessonNotesRepository
import com.example.mishkat.data.notes.MishkatDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * نموذج العرض لإدارة ملاحظات الدروس المدمج مع Room Database و StateFlow
 */
class LessonNotesViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MishkatDatabase.getDatabase(application)
    private val repository = LessonNotesRepository(database.lessonNoteDao())

    val allNotes: StateFlow<List<LessonNoteEntity>> = repository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addNote(lessonId: String, lessonTitle: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.addNote(lessonId, lessonTitle, content)
        }
    }

    fun updateNote(note: LessonNoteEntity, newContent: String) {
        if (newContent.isBlank()) return
        viewModelScope.launch {
            repository.updateNote(note, newContent)
        }
    }

    fun deleteNote(note: LessonNoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun deleteNoteById(id: Long) {
        viewModelScope.launch {
            repository.deleteNoteById(id)
        }
    }
}
