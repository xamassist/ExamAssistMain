package com.ravi.examassistmain.data.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.utils.Constants
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DocumentFirebaseCall{
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val docCollection: CollectionReference = fireStore.collection(Constants.DOCUMENT_COLLECTION)


//    suspend fun getAllDocuments(): List<Document> {
//        return try {
//            docCollection.get().await().toObjects(Document::class.java)
//        } catch(e: Exception) {
//            emptyList()
//        }
//    }
}