package com.ravi.examassistmain.data

import com.ravi.examassistmain.data.database.DocumentDao
import com.ravi.examassistmain.models.Document
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val documentDoa: DocumentDao
) {

    fun readDocument(): Flow<List<Document>> {
        return documentDoa.readDocument()
    }

    suspend fun insertRecipes(documentEntity: Document) {
        documentDoa.insertDocument(documentEntity)
    }
    suspend fun getDocument(documentId: String): Document {
        return documentDoa.getDocument(documentId)
    }

    suspend fun deleteFavoriteRecipe(documentEntity: Document) {
        documentDoa.deleteDocument(documentEntity)
    }

    suspend fun deleteAllDocuments() {
        documentDoa.deleteAllDocuments()
    }
}