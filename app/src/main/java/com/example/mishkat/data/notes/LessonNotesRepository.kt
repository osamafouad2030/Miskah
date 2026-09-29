package com.example.mishkat.data.notes

import kotlinx.coroutines.flow.Flow

/**
 * مستودع إدارة عمليات ملاحظات وفوائد الدروس التحريرية وفق نمط المستودع (Repository Pattern)
 */
class LessonNotesRepository(private val noteDao: LessonNoteDao) {

    fun getNotesForLesson(lessonId: String): Flow<List<LessonNoteEntity>> =
        noteDao.getNotesForLesson(lessonId)

    fun getAllNotes(): Flow<List<LessonNoteEntity>> =
        noteDao.getAllNotes()

    suspend fun addNote(lessonId: String, lessonTitle: String, content: String): Long {
        val now = System.currentTimeMillis()
        val note = LessonNoteEntity(
            lessonId = lessonId,
            lessonTitle = lessonTitle,
            noteContent = content.trim(),
            createdAt = now,
            updatedAt = now
        )
        return noteDao.insertNote(note)
    }

    suspend fun updateNote(note: LessonNoteEntity, newContent: String) {
        val updated = note.copy(
            noteContent = newContent.trim(),
            updatedAt = System.currentTimeMillis()
        )
        noteDao.updateNote(updated)
    }

    suspend fun deleteNote(note: LessonNoteEntity) {
        noteDao.deleteNote(note)
    }

    suspend fun deleteNoteById(id: Long) {
        noteDao.deleteNoteById(id)
    }
}
