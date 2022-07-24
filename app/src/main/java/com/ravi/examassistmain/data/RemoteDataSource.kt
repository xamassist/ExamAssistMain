package com.ravi.examassistmain.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ravi.examassistmain.models.EAUsers
import com.ravi.examassistmain.utils.Constants.Companion.BRANCH
import com.ravi.examassistmain.utils.Constants.Companion.DOCUMENT_COLLECTION
import com.ravi.examassistmain.utils.Constants.Companion.PREFERENCE
import com.ravi.examassistmain.utils.Constants.Companion.SEMESTER
import com.ravi.examassistmain.utils.Constants.Companion.UNIVERSITY
import com.ravi.examassistmain.utils.Constants.Companion.USER_COLLECTION
import com.ravi.examassistmain.utils.Constants.Companion.USER_PREFERENCE
import com.ravi.examassistmain.utils.PreferenceType
import javax.inject.Inject


class RemoteDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun getPreference(): DocumentReference {
        return fireStore.collection(USER_PREFERENCE).document(PREFERENCE)
    }

    fun getAllDocuments(docType: Int): Query? {
        return try {
            fireStore.collection(DOCUMENT_COLLECTION).whereEqualTo("doc_type", docType)
        } catch (e: Exception) {
            null
        }
    }

    fun getUsers(userId: String): Query? {
        return try {
            fireStore.collection(USER_COLLECTION).whereEqualTo("doc_type", userId)

        } catch (e: Exception) {
            null
        }
    }

    fun saveUserData(eaUser: EAUsers): Task<*>? {
        return try {
            fireStore.collection(USER_COLLECTION).document(eaUser.userId).set(eaUser)
        } catch (e: Exception) {
            null
        }
    }
}