package com.ravi.examassistmain.data

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.models.EAUsers
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
   private val docCollection: CollectionReference
) {

    suspend fun getAllDocuments(docType: Int): Query? {
        val docList = mutableListOf<Document>()

         try {
          //  val documents = docCollection.get(Source.SERVER).await().documents
            var docList = mutableListOf<Document>()
            val document = docCollection.get(Source.SERVER).await().documents
            val user = EAUsers("ECE","","ravimishra1017.rm@gmail.com","sdfwdfw","stan","8279965181",1,1,"MJPRU")
            val q : Query = docCollection.whereEqualTo("doc_type",docType)

return q
//            q.get().addOnCompleteListener() { documents ->
//
//                documents.result?.documents?.forEach {
//
//                    val doc = it.toObject(Document::class.java)
//                    doc?.documentId = it.id
//                    if (doc != null) {
//                        docList.add(doc)
//                    }
//
//                }
//                docList
//                return@addOnCompleteListener
//
//            }.addOnFailureListener{
//                Log.v("++++++++++",it.localizedMessage)
//            }


        } catch(e: Exception) {
            Log.v("mmmmmmmm",e.localizedMessage)
           // emptyList()
             return null
        }


    }

    suspend fun getAllDocument(docType: Int): List<Document> {
        return try {
            val documents = docCollection.get(Source.SERVER).await().documents
            val document = docCollection.get(Source.SERVER).await().documents
            val user = EAUsers("ECE","","ravimishra1017.rm@gmail.com","sdfwdfw","stan","8279965181",1,1,"MJPRU")
            val q : Query = docCollection.whereEqualTo("doc_type",docType)

            q.get().addOnSuccessListener { documents->
                for(doc in documents){
                    Log.v("++++++++++",doc.data.values.toString())
                }
            }.addOnFailureListener{
                    Log.v("++++++++++",it.localizedMessage)
                }
            val docList = mutableListOf<Document>()
            documents.forEach{
                val doc = it.toObject(Document::class.java)
                doc?.documentId = it.id
                if (doc != null) {
                    docList.add(doc)
                }
            }
            docList
        } catch(e: Exception) {
            Log.v("mmmmmmmm",e.localizedMessage)
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