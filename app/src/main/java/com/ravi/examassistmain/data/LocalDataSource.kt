package com.ravi.examassistmain.data

import com.ravi.examassistmain.data.database.dao.DocumentDao
import com.ravi.examassistmain.data.database.dao.SubjectDao
import com.ravi.examassistmain.data.database.dao.UserDao
import com.ravi.examassistmain.models.entity.Document
import com.ravi.examassistmain.models.entity.EAUsers
import com.ravi.examassistmain.models.Subjects
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val documentDao: DocumentDao,
    private val userDao: UserDao,
    private val subjectDao: SubjectDao
) {

    fun readDocument(): Flow<List<Document>> {
        return documentDao.readDocument()
    }

    fun readSubjects(): Flow<List<Subjects>> {
        return subjectDao.readSubject()
    }

    suspend fun insertDocument(documentEntity: Document) {
        documentDao.insertDocument(documentEntity)
    }
    suspend fun getDocument(documentId: String): Document {
        return documentDao.getDocument(documentId)
    }
    suspend fun getDocumentFromDocType(docType: Int): Flow<List<Document>> {
        return documentDao.getDocumentFromDocType(docType)
    }

    suspend fun deleteFavoriteRecipe(documentEntity: Document) {
        documentDao.deleteDocument(documentEntity)
    }

    suspend fun deleteAllDocuments() {
        documentDao.deleteAllDocuments()
    }
    suspend fun insertSubjects(subject: Subjects) {
        subjectDao.insertSubject(subject)
    }

    suspend fun insertUser(documentEntity: EAUsers) {
        userDao.insert(documentEntity)
    }
    suspend fun updateUser(documentEntity: EAUsers) {
        userDao.update(documentEntity)
    }

     fun getEAUser(): Flow<List<EAUsers?>>{
        return userDao.getUser()
    }
}