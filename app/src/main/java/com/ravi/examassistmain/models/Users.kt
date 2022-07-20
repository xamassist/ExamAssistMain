package com.ravi.examassistmain.models

import com.google.gson.annotations.SerializedName

data class Users(
    @SerializedName("branch")
    val branch: String?=null,

    @SerializedName("doj")
    val dateOfJoining: String?=null,

    @SerializedName("email")
    val email: String?=null,

    @SerializedName("name")
    val userName: String?=null,

    @SerializedName("phone")
    val phone: String?=null,

    @SerializedName("semester")
    val semester: Int?=null,

    @SerializedName("subscription_type")
    val subscriptionType: Int?=null,

    @SerializedName("university")
    val university: String?=null,
    @SerializedName("userId")
    val userId: String,

    @SerializedName("userAvatar")
    val userAvatar: String?=null,
    )
