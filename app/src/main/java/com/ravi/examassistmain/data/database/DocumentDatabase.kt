package com.ravi.examassistmain.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ravi.examassistmain.data.database.dao.DocumentDao
import com.ravi.examassistmain.data.database.dao.SubjectDao
import com.ravi.examassistmain.data.database.dao.UserDao
import com.ravi.examassistmain.models.entity.Document
import com.ravi.examassistmain.models.entity.EAUsers
import com.ravi.examassistmain.models.entity.PdfDownloads
import com.ravi.examassistmain.models.Subjects

@Database(
    entities = [Document::class, EAUsers::class,Subjects::class, PdfDownloads::class],
    version = 1,
    exportSchema = false
)
abstract class DocumentDatabase: RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun userDao(): UserDao
    abstract fun subjectDao(): SubjectDao
}