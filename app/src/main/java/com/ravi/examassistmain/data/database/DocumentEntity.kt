package com.ravi.examassistmain.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ravi.examassistmain.models.Documents
import com.ravi.examassistmain.utils.Constants.Companion.DOCUMENT_TABLE

@Entity(tableName = DOCUMENT_TABLE)
class DocumentEntity(
    var document: Documents
){
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}
