package com.ravi.examassistmain.data.database

import androidx.room.*
import com.ravi.examassistmain.models.Document
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertDocument(document: Document)
   /*
    @Query("UPDATE rooms SET is_deleted=:is_deleted WHERE local_id = :local_id")
    suspend fun deleteRoomLocal(is_deleted: String?, local_id: Long) : Int
   */
    @Query("UPDATE document_table SET pdfPath=:pdfPath WHERE documentId = :docId")
    suspend fun updateDocument(docId: String, pdfPath: String)
    @Query("UPDATE document_table SET documentTitle=:pdfName WHERE documentId = :docId")
    suspend fun updateName(docId: String, pdfName: String)

    @Query("SELECT * FROM document_table ORDER BY documentId ASC")
    fun readDocument(): Flow<List<Document>>

    //@Query("SELECT offlineData FROM news_table WHERE id =:id")
    @Query("SELECT * FROM document_table WHERE documentId =:docId")
    fun getDocument(docId: String): Document
    @Delete
    suspend fun deleteDocument(entity: Document)

    @Query("DELETE FROM document_table")
    suspend fun deleteAllDocuments()

}
/*
 @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(documentEntity: DocumentEntity)


    @Query("SELECT * FROM document_table ORDER BY id ASC")
    fun readDocument(): Flow<List<DocumentEntity>>

    @Query("UPDATE document_table SET order_amount = :amount, price = :price WHERE order_id =:id")
    fun updateDocument(): Flow<List<DocumentEntity>>

    @Delete
    suspend fun deleteDocument(favoritesEntity: DocumentEntity)

    @Query("DELETE FROM document_table")
    suspend fun deleteAllDocuments()
 */