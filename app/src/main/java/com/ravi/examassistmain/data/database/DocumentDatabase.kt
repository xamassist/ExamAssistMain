package com.ravi.examassistmain.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ravi.examassistmain.models.Document

@Database(
    entities = [Document::class],
    version = 1,
    exportSchema = false
)
abstract class DocumentDatabase: RoomDatabase() {

    abstract fun documentDao(): DocumentDao

}