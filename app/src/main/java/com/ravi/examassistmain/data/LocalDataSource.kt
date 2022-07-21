package com.ravi.examassistmain.data

import com.ravi.examassistmain.data.database.DocumentDao
import com.ravi.examassistmain.data.database.UserDao
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.models.EAUsers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val documentDao: DocumentDao,
    private val userDao: UserDao
) {

    fun readDocument(): Flow<List<Document>> {
        return documentDao.readDocument()
    }

    suspend fun insertRecipes(documentEntity: Document) {
        documentDao.insertDocument(documentEntity)
    }
    suspend fun getDocument(documentId: String): Document {
        return documentDao.getDocument(documentId)
    }



    suspend fun deleteFavoriteRecipe(documentEntity: Document) {
        documentDao.deleteDocument(documentEntity)
    }

    suspend fun deleteAllDocuments() {
        documentDao.deleteAllDocuments()
    }

    suspend fun insertUser(documentEntity: EAUsers) {
        userDao.insert(documentEntity)
    }

     fun getEAUser(): Flow<List<EAUsers?>>{
        return userDao.getUser()

    }
}