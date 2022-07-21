package com.ravi.examassistmain.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ravi.examassistmain.models.EAUsers
import com.ravi.examassistmain.utils.Constants.Companion.DOCUMENT_COLLECTION
import com.ravi.examassistmain.utils.Constants.Companion.USER_COLLECTION
import dagger.Module
import javax.inject.Inject
import javax.inject.Singleton


class RemoteDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {

     fun getAllDocuments(docType: Int): Query? {
        return try {
            fireStore.collection(DOCUMENT_COLLECTION).whereEqualTo("doc_type", docType)
        } catch (e: Exception) {
            null
        }
    }

    fun getUsers(userId: String):Query? {
        return try {
             fireStore.collection(USER_COLLECTION).whereEqualTo("doc_type", userId)

         } catch (e: Exception) {
            null
        }
    }
    fun saveUserData(eaUser: EAUsers):Task<*>?{
        return try {
            fireStore.collection(USER_COLLECTION).document(eaUser.userId).set(eaUser)
        } catch (e: Exception) {
            null
        }
    }
}