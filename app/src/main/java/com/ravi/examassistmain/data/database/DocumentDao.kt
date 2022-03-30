package com.ravi.examassistmain.data.database

import androidx.room.*
import com.ravi.examassistmain.data.database.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

   @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(documentEntity: DocumentEntity)


    @Query("SELECT * FROM document_table ORDER BY id ASC")
    fun readDocument(): Flow<List<DocumentEntity>>

    @Delete
    suspend fun deleteDocument(favoritesEntity: DocumentEntity)

    @Query("DELETE FROM document_table")
    suspend fun deleteAllDocuments()



}