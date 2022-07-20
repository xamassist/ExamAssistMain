package com.ravi.examassistmain.data.database

import androidx.room.*
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.models.EAUsers
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM document_table")
    fun getUser(): List<EAUsers>

    @Insert
    fun insert(users: EAUsers)
}
