package com.ravi.examassistmain.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson

object ListConverter {

    @TypeConverter
    fun listToJsonString(value: List<String>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonStringToList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()
}