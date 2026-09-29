package com.example.mishkat.data.notes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * كائن الوصول لبيانات ملاحظات الدروس في قاعدة بيانات Room
 */
@Dao
interface LessonNoteDao {

    @Query("SELECT * FROM lesson_notes WHERE lessonId = :lessonId ORDER BY updatedAt DESC")
    fun getNotesForLesson(lessonId: String): Flow<List<LessonNoteEntity>>

    @Query("SELECT * FROM lesson_notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<LessonNoteEntity>>

    @Query("SELECT COUNT(*) FROM lesson_notes WHERE lessonId = :lessonId")
    fun getNotesCountForLesson(lessonId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: LessonNoteEntity): Long

    @Update
    suspend fun updateNote(note: LessonNoteEntity)

    @Delete
    suspend fun deleteNote(note: LessonNoteEntity)

    @Query("DELETE FROM lesson_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)
}
