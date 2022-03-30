package com.ravi.examassistmain.models

import com.google.firebase.firestore.PropertyName
import com.google.j2objc.annotations.Property

data class Document(
    @get:PropertyName("avg_rating") @set:PropertyName("avg_rating")
    var averageRating: Double = 0.0,

//    @get:PropertyName("branch") @set:PropertyName("branch")
//    var branch: String = "",

    @get:PropertyName("doc_id") @set:PropertyName("doc_id")
    var documentId: String = "",

    @get:PropertyName("doc_type") @set:PropertyName("doc_type")
    var documentType: String="",

    @get:PropertyName("is_premium") @set:PropertyName("is_premium")
    var isPremium: Int = 0,

    @get:PropertyName("paper_year") @set:PropertyName("paper_year")
    var paperYear: String = "",

    @get:PropertyName("rating_count") @set:PropertyName("rating_count")
    var ratingCount: Double = 0.0,

    @get:PropertyName("sub_count_aktu") @set:PropertyName("sub_count_aktu")
    var subject_code_aktu: String = "",

    @get:PropertyName("sub_count_mjpru") @set:PropertyName("sub_count_mjpru")
    var subject_code_mjpru: String = "",

    @get:PropertyName("sub_count") @set:PropertyName("sub_count")
    var subject_code: String = "",

    @get:PropertyName("tag") @set:PropertyName("tag")
    var documentTags: String = "",

    @get:PropertyName("title") @set:PropertyName("title")
    var documentTitle: String="",

    @get:PropertyName("upload_date") @set:PropertyName("upload_date")
    var uploadDate: String = "",

    @get:PropertyName("uploader_id") @set:PropertyName("uploader_id")
    var uploaderId: String = "",

    @get:PropertyName("uploader_name") @set:PropertyName("uploader_name")
    var uploaderName: String = "",

    @get:PropertyName("view_count") @set:PropertyName("view_count")
    var viewCount: Long=0,
    )

