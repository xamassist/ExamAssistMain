package com.ravi.examassistmain.data.database.dao

import androidx.room.*
import com.ravi.examassistmain.models.entity.Document
import kotlinx.coroutines.flow.Flow
import javax.security.auth.Subject

@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDocument(document: Document)

    @Query("UPDATE document_table SET pdfPath=:pdfPath WHERE documentId = :docId")
    suspend fun updatemyDocument(docId: String, pdfPath: String)

    @Update(entity = Document::class)
    suspend fun updateDocument(document: Document)

    @Query("UPDATE document_table SET documentTitle=:pdfName WHERE documentId = :docId")
    suspend fun updateName(docId: String, pdfName: String)

    @Query("SELECT * FROM document_table ORDER BY documentId ASC")
    fun readDocument(): Flow<List<Document>>

    @Query("SELECT * FROM document_table WHERE documentId =:docId")
    fun getDocument(docId: String): Document

    @Query("SELECT * FROM document_table WHERE documentType =:docType")
    fun getDocumentFromDocType(docType: Int): Flow<List<Document>>

    @Delete
    suspend fun deleteDocument(entity: Document)

    @Query("DELETE FROM document_table")
    suspend fun deleteAllDocuments()

}
