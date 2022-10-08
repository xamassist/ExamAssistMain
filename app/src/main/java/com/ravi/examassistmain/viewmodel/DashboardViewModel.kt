package com.ravi.examassistmain.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.ravi.examassistmain.data.Repository
import com.ravi.examassistmain.models.Subjects
import com.ravi.examassistmain.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {

    var readUser = repository.local.getEAUser().asLiveData()
    val readSubjects: LiveData<List<Subjects>> = repository.local.readSubjects().asLiveData()
    var subjectResponse: MutableLiveData<NetworkResult<MutableList<Subjects>>> = MutableLiveData()
     fun getSubjectList(branch:String,semester:Int,university:Int) = viewModelScope.launch(Dispatchers.IO) {
        getSubjectsSafeCall(branch,semester,university)
    }
    private suspend fun getSubjectsSafeCall(branch:String, semester:Int, university:Int) {
        subjectResponse.postValue(NetworkResult.Loading())
        try {

            val eaSubjectList = mutableListOf<Subjects>()
            val response = repository.remote.getSubjects(branch,semester,university)//("ECE",1,0)
            response?.get()?.addOnSuccessListener { snapshot ->

                snapshot.documents.forEach {

                    val doc = it.toObject(Subjects::class.java)

                    if (doc != null) {
                        eaSubjectList.add(doc)
                    }
                     }
                offlineSubjectCache(eaSubjectList)
                subjectResponse.value = handleSubjectResponse(eaSubjectList)

            }?.addOnFailureListener {
                subjectResponse.value = NetworkResult.Error("Something went wrong")
            }

        } catch (e: Exception) {
            subjectResponse.value = NetworkResult.Error("Documents not found.")
        }
    }
    private fun offlineSubjectCache(docList: MutableList<Subjects>?) {
        insertSubjectInRoom(docList)
    }
    fun insertSubjectInRoom(subjects: MutableList<Subjects>?) =
        viewModelScope.launch(Dispatchers.IO) {

            if (subjects != null) {
                for( subject in subjects){
                    repository.local.insertSubjects(subject)
                }
            }
        }

    private fun handleSubjectResponse(response: MutableList<Subjects>?): NetworkResult<MutableList<Subjects>>? {
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
    private fun logoutUser(){

    }

}