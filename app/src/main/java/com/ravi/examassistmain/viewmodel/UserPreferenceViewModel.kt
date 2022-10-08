package com.ravi.examassistmain.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.ravi.examassistmain.data.Repository
import com.ravi.examassistmain.models.entity.Document
import com.ravi.examassistmain.models.entity.EAUsers
import com.ravi.examassistmain.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPreferenceViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    var readUser: LiveData<List<EAUsers?>> = repository.local.getEAUser().asLiveData()
    fun updateUserInRoomDB(user: EAUsers) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.updateUser(user)
        }
    /** Firestore */
    var preferenceResponse: MutableLiveData<NetworkResult<HashMap<*,*>>> = MutableLiveData()
    var userSaveResponse: MutableLiveData<NetworkResult<Boolean>> = MutableLiveData()

    @RequiresApi(Build.VERSION_CODES.M)
    fun getPreference() = viewModelScope.launch {
        getPreferenceSafeCall()
    }

    fun getUserDataFromRoom() {
        readUser = repository.local.getEAUser().asLiveData()
    }


    private fun formDummyQuery() {
        // val query: Query =
    }
    /** firebase Data 1. save user data*/
    fun saveUserDataToServer(eaUser: EAUsers) = viewModelScope.launch(Dispatchers.IO) {
        repository.remote.saveUserData(eaUser)?.addOnSuccessListener {
            userSaveResponse.postValue(NetworkResult.Success(true))
        }?.addOnFailureListener{
            userSaveResponse.postValue(NetworkResult.Error("Unable to save user's data"))
        }
    }
    private suspend fun getPreferenceSafeCall() {

        try {
            var prefList = mutableListOf<String>()
            val response = repository.remote.getPreference()
            response.get().addOnSuccessListener {
                val values = it.data as HashMap<*,*>
                preferenceResponse.postValue(NetworkResult.Success(values))
            }
        } catch (e: Exception) {
            preferenceResponse.postValue(NetworkResult.Error("universities not found."))
        }

    }

    private fun offlineCacheDocuments(docList: List<Document>) {
        //insertDocumentList(docList)
    }


    private fun handleDocumentResponse(response: List<Document>?): NetworkResult<List<Document>> {
        response?.let { docList ->
            return if (!docList.isNullOrEmpty()) {
                NetworkResult.Success(docList)
            } else {
                NetworkResult.Error("No files found")
            }

        }?.run {
            return NetworkResult.Error("response not found")
        }
        return NetworkResult.Error("No files found")
    }

}
