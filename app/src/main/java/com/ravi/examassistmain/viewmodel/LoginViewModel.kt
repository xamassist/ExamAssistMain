package com.ravi.examassistmain.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.ravi.examassistmain.data.Repository
import com.ravi.examassistmain.models.EAUsers
import com.ravi.examassistmain.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository)
    : ViewModel() {
    var readUser = repository.local.getEAUser().asLiveData()

    /** Room Data*/
     fun insertUserDataInRoom(user: EAUsers) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertUser(user)
        }

    private fun getUserDataFromRoom() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.getEAUser()
        }

    var userResponse: MutableLiveData<NetworkResult<EAUsers>> = MutableLiveData()
    var userSaveResponse: MutableLiveData<NetworkResult<Boolean>> = MutableLiveData()

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
                val user = eaUserList.firstOrNull()
                user?.let {
                    offlineCacheUser(eaUserList.first())
                }
                userResponse.value = handleDocumentResponse(eaUserList)
            }?.addOnFailureListener {
                userResponse.value = NetworkResult.Error("Something went wrong")
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

    private fun offlineCacheUser(docList: EAUsers) {
        insertUserDataInRoom(docList)
    }

    /** firebase Data 1. save user data*/
     fun saveUserDataToServer(eaUser: EAUsers) = viewModelScope.launch(Dispatchers.IO) {
        repository.remote.saveUserData(eaUser)?.addOnSuccessListener {
            userSaveResponse.postValue(NetworkResult.Success(true))
          val response =   insertUserDataInRoom(eaUser)
            if(response.isCompleted){
                Log.v("FirstTimeUser", "Save to room")
            }else if(response.isCancelled) {
                Log.v("FirstTimeUser", "failed toSave to room")
            }

        }?.addOnFailureListener{
            userSaveResponse.postValue(NetworkResult.Error("Unable to save user's data"))
        }
    }

}
