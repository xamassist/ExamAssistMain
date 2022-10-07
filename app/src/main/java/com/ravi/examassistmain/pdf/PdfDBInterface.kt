package com.ravi.examassistmain.pdf

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ravi.examassistmain.models.entity.Document

@Dao
interface PdfDBInterface {

    @Query("SELECT * FROM document_table")
    fun getAllDocs(): List<Document>

    @Insert
    fun addDocument(document: Document)

//    @Query("UPDATE document_table SET order_amount = :amount, price = :price WHERE order_id =:id")
//    fun update(amount: Float?, price: Float?, id: Int)

    @Query("DELETE FROM document_table")
    fun deleteAllDocs()

    @Query("DELETE FROM document_table WHERE documentId = :pdfId")
    fun deleteByPdfId(pdfId: String)

    @Query("SELECT * FROM document_table WHERE documentId = :pdfId")
    fun isDataExist(pdfId: String): Int


}