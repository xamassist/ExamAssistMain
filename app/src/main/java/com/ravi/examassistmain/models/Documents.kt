package com.ravi.examassistmain.models

import com.google.gson.annotations.SerializedName

data class Documents(
    @SerializedName("documents")
    val docData: List<Document>
)