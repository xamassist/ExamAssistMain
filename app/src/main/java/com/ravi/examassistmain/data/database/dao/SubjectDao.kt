package com.ravi.examassistmain.data.database.dao

import androidx.room.*
import com.ravi.examassistmain.models.Subjects
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertSubject(document: Subjects)
    @Query("SELECT * FROM subject_table")
    fun readSubject(): Flow<List<Subjects>>
}
