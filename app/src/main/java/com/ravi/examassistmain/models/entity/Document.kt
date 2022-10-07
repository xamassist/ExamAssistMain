package com.ravi.examassistmain.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import com.ravi.examassistmain.utils.Constants
import java.io.Serializable

@Entity(tableName = Constants.DOCUMENT_TABLE)
data class Document(
    @PrimaryKey
    @SerializedName("doc_id")
    var documentId: String = "",
    @SerializedName("avg_rating")
    var averageRating: Float? = 0f,
    @SerializedName("doc_type")
    var documentType: Int? = 0,
     @SerializedName("is_premium")
    var isPremium: Int? = 0,
    @SerializedName("paper_year")
    var paperYear: Int? = 0,
    @SerializedName("rating_count")
    var ratingCount: Float? = 0.0f,
    @SerializedName("code_aktu")
    var subject_code_aktu: String? = "",
    @SerializedName("code_mjpru")
    var subject_code_mjpru: String? = "",
    @SerializedName("tag")
    var documentTags: String? = "",
    @SerializedName("doc_title")
    var documentTitle: String? = "",
    @SerializedName("uploader_id")
    var uploaderId: String? = "",
    @SerializedName("uploader")
    var uploaderName: String? = "",
    @SerializedName("count")
    var viewCount: Int? = 0,
    @SerializedName("pdf_url")
    var pdfUrl: String? = "",
    var pdfPath: String? = ""
) : Serializable

