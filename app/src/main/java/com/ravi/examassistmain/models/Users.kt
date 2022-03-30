package com.ravi.examassistmain.models

import com.google.gson.annotations.SerializedName

data class Users(
    @SerializedName("branch")
    val branch: String,

    @SerializedName("doj")
    val dateOfJoining: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("id")
    val userId: String,

    @SerializedName("name")
    val userName: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("semester")
    val semester: Int,

    @SerializedName("subscription_type")
    val subscriptionType: Int,

    @SerializedName("university")
    val university: String,


    )
