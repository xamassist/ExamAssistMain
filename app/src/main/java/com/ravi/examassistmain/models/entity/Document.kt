package com.ravi.examassistmain.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import com.ravi.examassistmain.utils.Constants
import java.io.Serializable

@Entity(tableName = Constants.DOCUMENT_TABLE)
data class Document(
    @get:PropertyName("avg_rating") @set:PropertyName("avg_rating")
    var averageRating: Float? = 0f,

    @PrimaryKey
    @get:PropertyName("doc_id") @set:PropertyName("doc_id")
    var documentId: String = "",

    @get:PropertyName("doc_type") @set:PropertyName("doc_type")
    var documentType: Int? = 0,

    @get:PropertyName("is_premium") @set:PropertyName("is_premium")
    var isPremium: Int? = 0,

    @get:PropertyName("paper_year") @set:PropertyName("paper_year")
    var paperYear: Int? = 0,

    @get:PropertyName("rating_count") @set:PropertyName("rating_count")
    var ratingCount: Float? = 0.0f,

    @get:PropertyName("sub_count_aktu") @set:PropertyName("sub_count_aktu")
    var subject_code_aktu: String? = "",

    @get:PropertyName("sub_count_mjpru") @set:PropertyName("sub_count_mjpru")
    var subject_code_mjpru: String? = "",

    @get:PropertyName("sub_count") @set:PropertyName("sub_count")
    var subject_code: String? = "",

    @get:PropertyName("tag") @set:PropertyName("tag")
    var documentTags: String? = "",

    @get:PropertyName("doc_title") @set:PropertyName("doc_title")
    var documentTitle: String? = "",

    @get:PropertyName("uploader_id") @set:PropertyName("uploader_id")
    var uploaderId: String? = "",

    @get:PropertyName("uploader") @set:PropertyName("uploader")
    var uploaderName: String? = "",

    @get:PropertyName("view_count") @set:PropertyName("view_count")
    var viewCount: Int? = 0,
    @get:PropertyName("pdf_url") @set:PropertyName("pdf_url")
    var pdfUrl: String? = "",
    @get:PropertyName("pdf_path") @set:PropertyName("pdf_path")
    var pdfPath: String? = ""
) : Serializable

