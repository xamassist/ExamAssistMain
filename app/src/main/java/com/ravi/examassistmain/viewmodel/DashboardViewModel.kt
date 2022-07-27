package com.ravi.examassistmain.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.ravi.examassistmain.data.Repository
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.models.EAUsers
import com.ravi.examassistmain.models.Subjects
import com.ravi.examassistmain.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: Repository
)
    : ViewModel() {

    var readUser = repository.local.getEAUser().asLiveData()
    val readSubjects: LiveData<List<Subjects>> = repository.local.readSubjects().asLiveData()

    var subjectResponse: MutableLiveData<NetworkResult<MutableList<Subjects>>> = MutableLiveData()

     fun getSubjectList(branch:String,semester:Int,university:Int) = viewModelScope.launch {
        getSubjectsSafeCall(branch,semester,university)
    }
    private suspend fun getSubjectsSafeCall(branch:String,semester:Int,university:Int) {
        subjectResponse.value = NetworkResult.Loading()
        try {
            val eaSubjectList = mutableListOf<Subjects>()
            val response = repository.remote.getSubjects("ECE",0,0)
            response?.get()?.addOnSuccessListener { snapshot ->
                Log.v("getSubjects",snapshot?.toString()?:"xxx")

                snapshot.documents.forEach {

                    val doc = it.toObject(Subjects::class.java)

                    if (doc != null) {
                        Log.v("getSubjects", doc.branch ?: "bbb")
                        eaSubjectList.add(doc)
                    }
                     }
                if (eaSubjectList!=null) {
                    offlineSubjectCache(eaSubjectList)
                }
                subjectResponse.value = handleSubjectResponse(eaSubjectList)

            }?.addOnFailureListener {
                Log.v("getSubjects",it.localizedMessage)

                subjectResponse.value = NetworkResult.Error("Something went wrong")
            }

        } catch (e: Exception) {
            Log.v("getSubjects","error")

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

}