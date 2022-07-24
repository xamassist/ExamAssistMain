package com.ravi.examassistmain.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.ravi.examassistmain.data.Repository
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.models.EAUsers
import com.ravi.examassistmain.utils.NetworkResult
import com.ravi.examassistmain.utils.PreferenceType

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class UserPreferenceViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val readUser: LiveData<List<EAUsers?>> = repository.local.getEAUser().asLiveData()
    fun updateUserInRoomDB(user: EAUsers) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.updateUser(user)
        }
    /** Firestore */
    var preferenceResponse: MutableLiveData<NetworkResult<HashMap<*,*>>> = MutableLiveData()

    @RequiresApi(Build.VERSION_CODES.M)
    fun getPreference() = viewModelScope.launch {
        getPreferenceSafeCall()
    }
    fun getUserDataFromRoom() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.getEAUser()
        }


    private fun formDummyQuery() {
        // val query: Query =
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
