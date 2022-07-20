package com.ravi.examassistmain.data.database

import androidx.room.*
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.models.EAUsers

@Dao
interface UserDao {

    @Query("SELECT * FROM document_table")
    fun getUser(docId: String): Document

    @Insert
    fun insert(users: EAUsers)
}
