package com.ravi.examassistmain.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ravi.examassistmain.utils.Constants

@Entity(tableName = Constants.PDF_TABLE)
data class PdfDownloads(@PrimaryKey val documentID: String, val pdfPath: String)
