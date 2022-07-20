package com.ravi.examassistmain.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ravi.examassistmain.models.EAUsers

@Database(
    entities = [EAUsers::class],
    version = 1,
    exportSchema = false
)
abstract class UserDatabase: RoomDatabase() {


}