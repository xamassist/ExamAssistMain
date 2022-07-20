package com.ravi.examassistmain.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ravi.examassistmain.data.Repository
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.models.EAUsers
import com.ravi.examassistmain.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {
    var userResponse: MutableLiveData<NetworkResult<EAUsers>> = MutableLiveData()
    var userSaveResponse: MutableLiveData<NetworkResult<Boolean>> = MutableLiveData()

    /** Room Data*/
    private fun insertUserDataInRoom(user: EAUsers) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertUser(user)
        }

    private fun getUserDataFromRoom() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.getUser()
        }

    /** firebase Data 1. get user data*/
    fun getUser(userId:String) = viewModelScope.launch {
        getUserSaveCall(userId)
    }

    private suspend fun getUserSaveCall(userId:String) {
        userResponse.value = NetworkResult.Loading()
        try {
            val eaUserList = mutableListOf<EAUsers>()
            val response = repository.remote.getUsers(userId)
            response?.get()?.addOnSuccessListener { snapshot ->
                snapshot.documents.forEach {
                    val doc = it.toObject(EAUsers::class.java)
                    if (doc != null) {
                        eaUserList.add(doc)
                    }
                }
                userResponse.value = handleDocumentResponse(eaUserList)
            }?.addOnFailureListener {
                userResponse.value = NetworkResult.Error("Something went wrong")
            }
            val doc = userResponse.value?.data
            if (doc!=null) {
                offlineCacheDocuments(doc)
            } else {
                userResponse.value = NetworkResult.Error("Documents data was null.")
            }
        } catch (e: Exception) {
            userResponse.value = NetworkResult.Error("Documents not found.")
        }
    }

    private fun handleDocumentResponse(response: MutableList<EAUsers>?): NetworkResult<EAUsers>? {
        response?.let { docList ->
            return if (!docList.isNullOrEmpty()) {
                NetworkResult.Success(docList.first())
            } else {
                NetworkResult.Error("No files found")
            }

        }?.run {
            return NetworkResult.Error("response not found")
        }
        return NetworkResult.Error("No files found")
    }

    private fun offlineCacheDocuments(docList: EAUsers) {
        insertUserDataInRoom(docList)
    }

    /** firebase Data 1. save user data*/
    private fun saveUserDataToServer(eaUser: EAUsers) = viewModelScope.launch(Dispatchers.IO) {
        repository.remote.saveUserData(eaUser)?.addOnSuccessListener {
            userSaveResponse.postValue(NetworkResult.Success(true))
        }?.addOnFailureListener{
            userSaveResponse.postValue(NetworkResult.Error("Unable to save user's data"))
        }
    }

}
