package com.ravi.examassistmain.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.models.Documents

class RoomConverters {

//    @TypeConverter
//    fun saveDocumentList(listOfString: List<Document?>?): String? {
//        return Gson().toJson(listOfString)
//    }
//
//    @TypeConverter
//    fun getDocumentList(listOfString: String?): List<Document?>? {
//        return Gson().fromJson(
//            listOfString,
//            object : TypeToken<List<String?>?>() {}.type
//        )
//    }

    var gson = Gson()

    @TypeConverter
    fun documentToString(questions: Documents): String {
        return gson.toJson(questions)
    }

    @TypeConverter
    fun stringToDocument(data: String): Documents {
        val listType = object : TypeToken<Documents>() {}.type
        return gson.fromJson(data, listType)
    }


}
