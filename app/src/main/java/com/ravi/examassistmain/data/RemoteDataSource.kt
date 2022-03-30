package com.ravi.examassistmain.data

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ravi.examassistmain.data.remote.DocumentFirebaseCall
import com.ravi.examassistmain.di.DatabaseModule
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
   private val docCollection: CollectionReference
) {

    suspend fun getAllDocuments(): List<Document> {
        return try {
            docCollection.get().await().toObjects(Document::class.java)
        } catch(e: Exception) {
            emptyList()
        }
    }
}
/*
  val db = Firebase.firestore
        db.collection("doc")
            .get()
            .addOnSuccessListener { result ->

                if(!result.isEmpty){

                    for (document in result) {
                        Log.d("TestActivity", "${document.id} => ${document.data}")
                        val doc: Document = document.toObject(Document::class.java)
                        documentArray.add(doc)
                    }
                    setAdapter(documentArray)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TestActivity", "Error getting documents.", exception)
            }
 */