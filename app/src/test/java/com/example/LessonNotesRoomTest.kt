package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.mishkat.data.notes.LessonNoteDao
import com.example.mishkat.data.notes.LessonNotesRepository
import com.example.mishkat.data.notes.MishkatDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LessonNotesRoomTest {

    private lateinit var database: MishkatDatabase
    private lateinit var dao: LessonNoteDao
    private lateinit var repository: LessonNotesRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MishkatDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.lessonNoteDao()
        repository = LessonNotesRepository(dao)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testInsertAndRetrieveNotesForLesson() = runBlocking {
        repository.addNote(
            lessonId = "sanad_general",
            lessonTitle = "مقدمة وأسانيد الإمام عاصم",
            content = "فائدة: طريق الفيل وطريق زرعان هما الطريقان المعتمدان في قصر المنفصل."
        )

        val notes = repository.getNotesForLesson("sanad_general").first()
        assertEquals(1, notes.size)
        assertEquals("sanad_general", notes[0].lessonId)
        assertTrue(notes[0].noteContent.contains("طريق الفيل"))
    }

    @Test
    fun testUpdateAndDeleteNote() = runBlocking {
        repository.addNote(
            lessonId = "madd_munfasil_rules",
            lessonTitle = "أحكام المد المنفصل",
            content = "ملاحظة مبدئية"
        )

        val notesBefore = repository.getNotesForLesson("madd_munfasil_rules").first()
        val originalNote = notesBefore[0]

        repository.updateNote(originalNote, "ملاحظة منقحة: يمتنع السكت مع قصر المنفصل لحفص من الطيبة")
        val notesAfterUpdate = repository.getNotesForLesson("madd_munfasil_rules").first()
        assertEquals("ملاحظة منقحة: يمتنع السكت مع قصر المنفصل لحفص من الطيبة", notesAfterUpdate[0].noteContent)

        repository.deleteNote(notesAfterUpdate[0])
        val notesAfterDelete = repository.getNotesForLesson("madd_munfasil_rules").first()
        assertEquals(0, notesAfterDelete.size)
    }
}
