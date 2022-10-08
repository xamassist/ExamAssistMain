package com.ravi.examassistmain.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ravi.examassistmain.models.entity.EAUsers
import com.ravi.examassistmain.utils.Constants.Companion.DOCUMENT_COLLECTION
import com.ravi.examassistmain.utils.Constants.Companion.PREFERENCE
import com.ravi.examassistmain.utils.Constants.Companion.SUBJECT_COLLECTION
import com.ravi.examassistmain.utils.Constants.Companion.USER_COLLECTION
import com.ravi.examassistmain.utils.Constants.Companion.USER_PREFERENCE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class RemoteDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun getPreference(): DocumentReference {
        return fireStore.collection(USER_PREFERENCE).document(PREFERENCE)
    }

    suspend fun getAllDocuments(docType: Int): Query? {
        return try {
            withContext(Dispatchers.IO) {
                fireStore.collection(DOCUMENT_COLLECTION).whereEqualTo("doc_type", docType)
            }
        } catch (e: Exception) {
            null
        }
    }
    suspend fun getDocuments(docType: Int,subjectCode:String): Query? {
        return try {
            withContext(Dispatchers.IO) {
                fireStore.collection(DOCUMENT_COLLECTION).whereEqualTo("doc_type", docType)
                    .whereArrayContains("subject_code",subjectCode.trim())
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUsers(userId: String): Query? {
        return try {
            withContext(Dispatchers.IO) {
                fireStore.collection(USER_COLLECTION).whereEqualTo("userId", userId)
            }
        } catch (e: Exception) {
            null
        }
    }
//    suspend fun logoutUsers(userId: String): Query? {
//        return try {
//
//        } catch (e: Exception) {
//            null
//        }
//    }

    suspend fun getSubjects(branch: String, semester: Int, university: Int): Query? {
        return try {
            withContext(Dispatchers.IO) {
                fireStore.collection(SUBJECT_COLLECTION)
                    .whereArrayContains("branch", branch)
                    .whereEqualTo("semester", semester)
                    .whereEqualTo("university", university)
            }

        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserData(eaUser: EAUsers): Task<*>? {
        return try {
            withContext(Dispatchers.IO) {
                fireStore.collection(USER_COLLECTION).document(eaUser.userId).set(eaUser)
            }
        } catch (e: Exception) {
            null
        }
    }
}