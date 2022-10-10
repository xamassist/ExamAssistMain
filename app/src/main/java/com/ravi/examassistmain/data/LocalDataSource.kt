package com.ravi.examassistmain.data

import com.ravi.examassistmain.data.database.dao.DocumentDao
import com.ravi.examassistmain.data.database.dao.PDFDao
import com.ravi.examassistmain.data.database.dao.SubjectDao
import com.ravi.examassistmain.data.database.dao.UserDao
import com.ravi.examassistmain.models.entity.Document
import com.ravi.examassistmain.models.entity.EAUsers
import com.ravi.examassistmain.models.Subjects
import com.ravi.examassistmain.models.entity.PdfDownloads
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val documentDao: DocumentDao,
    private val userDao: UserDao,
    private val subjectDao: SubjectDao,
    private val pdfDao: PDFDao,
) {

    fun readDocument(): Flow<List<Document>> {
        return documentDao.readDocument()
    }

    fun readSubjects(): Flow<List<Subjects>> {
        return subjectDao.readSubject()
    }

    fun insertDocument(documentEntity: Document) {
        documentDao.insertDocument(documentEntity)
    }
    fun getDocument(documentId: String): Document {
        return documentDao.getDocument(documentId)
    }
    fun getDocumentFromDocType(docType: Int): Flow<List<Document>> {
        return documentDao.getDocumentFromDocType(docType)
    }

    suspend fun deleteAllDocuments() {
        documentDao.deleteAllDocuments()
    }
    fun insertSubjects(subject: Subjects) {
        subjectDao.insertSubject(subject)
    }

    fun insertUser(documentEntity: EAUsers) {
        userDao.insert(documentEntity)
    }
    fun updateUser(documentEntity: EAUsers) {
        userDao.update(documentEntity)
    }
     fun getEAUser(): Flow<List<EAUsers?>>{
        return userDao.getUser()
    }
    fun insertPdf(pdfDownloads: PdfDownloads) {
        pdfDao.insertPdf(pdfDownloads)
    }

    fun readPdfs(): Flow<List<PdfDownloads>> {
        return pdfDao.readDownloadedPdfs()
    }
    fun getPdf(documentId: String): PdfDownloads {
        return pdfDao.getPdf(documentId)
    }
}