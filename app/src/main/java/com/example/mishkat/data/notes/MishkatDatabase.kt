package com.example.mishkat.data.notes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * قاعدة بيانات مشكاة المحلية المبنية بـ Room Database لحفظ ملاحظات الطلاب وفوائدهم التحريرية
 */
@Database(
    entities = [LessonNoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MishkatDatabase : RoomDatabase() {

    abstract fun lessonNoteDao(): LessonNoteDao

    companion object {
        @Volatile
        private var INSTANCE: MishkatDatabase? = null

        fun getDatabase(context: Context): MishkatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MishkatDatabase::class.java,
                    "mishkat_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
