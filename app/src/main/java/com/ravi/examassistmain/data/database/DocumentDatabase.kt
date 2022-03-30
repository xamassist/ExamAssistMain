package com.ravi.examassistmain.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ravi.examassistmain.data.RoomConverters

@Database(
    entities = [DocumentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class DocumentDatabase: RoomDatabase() {

    abstract fun documentDao(): DocumentDao

}