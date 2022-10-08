package com.ravi.examassistmain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import com.ravi.examassistmain.utils.Constants
import java.io.Serializable
@Entity(tableName = Constants.SUBJECT_TABLE)
data class Subjects(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
   // var branch: String? = "",
    var semester: Int = 0,
    var subjectName: String? = "",
    var university: Int? = 0,
    var subjectCode: String? = ""
){
    constructor(): this(0)
}

