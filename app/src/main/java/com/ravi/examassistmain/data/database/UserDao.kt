package com.ravi.examassistmain.data.database

import androidx.room.*
import com.ravi.examassistmain.models.entity.EAUsers
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table")
     fun getUser(): Flow<List<EAUsers>>

    @Insert
    fun insert(users: EAUsers)
    @Update
    fun update(users: EAUsers)
}
