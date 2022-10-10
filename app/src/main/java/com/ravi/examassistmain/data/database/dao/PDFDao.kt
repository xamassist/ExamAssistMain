package com.ravi.examassistmain.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ravi.examassistmain.models.entity.Document
import com.ravi.examassistmain.models.entity.PdfDownloads
import kotlinx.coroutines.flow.Flow

@Dao
interface PDFDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPdf(pdfDownloads: PdfDownloads)
    @Query("SELECT * FROM pdf_downloads")
    fun readDownloadedPdfs(): Flow<List<PdfDownloads>>
    @Query("SELECT * FROM pdf_downloads WHERE documentId =:docId")
    fun getPdf(docId: String): PdfDownloads
}
