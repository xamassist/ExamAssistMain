package com.ravi.examassistmain.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.models.EAUsers

@Database(
    entities = [Document::class,EAUsers::class],
    version = 1,
    exportSchema = false
)
abstract class DocumentDatabase: RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun userDao(): UserDao
}