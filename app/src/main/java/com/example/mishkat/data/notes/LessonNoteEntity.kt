package com.example.mishkat.data.notes

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * كيان الملاحظات والفوائد التحريرية التي يدونها الطالب لكل درس من دروس عاصم
 */
@Entity(tableName = "lesson_notes")
data class LessonNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lessonId: String,
    val lessonTitle: String,
    val noteContent: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
