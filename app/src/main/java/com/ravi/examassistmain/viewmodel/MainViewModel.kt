package com.ravi.examassistmain.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.ravi.examassistmain.data.Repository
import com.ravi.examassistmain.models.entity.Document
import com.ravi.examassistmain.utils.NetworkResult

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    /** ROOM DATABASE */

    //val readDocument: LiveData<List<DocumentEntity>> = repository.local.readDocument().asLiveData()
    var readDocs: LiveData<List<Document>> = repository.local.readDocument().asLiveData()
    var cachedResponse: LiveData<List<Document>> = MutableLiveData()
    private fun insertDocument(document: Document) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertDocument(document)
        }

    private fun insertDocumentList(documentList: List<Document>) =
        viewModelScope.launch(Dispatchers.IO) {
            for (doc in documentList) {
                repository.local.insertDocument(doc)
            }
        }
     fun getDocumentByDocType(documentType: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            readDocs = repository.local.getDocumentFromDocType(documentType).asLiveData()
        }

    fun deleteDocument(document: Document) =
        viewModelScope.launch(Dispatchers.IO) {
            // repository.local.deleteFavoriteRecipe(documentEntity)
        }

    fun deleteAllDocument() =
        viewModelScope.launch(Dispatchers.IO) {
            //repository.local.deleteAllDocuments()
        }

    /** Firestore */
    var documentResponse: MutableLiveData<NetworkResult<List<Document>>> = MutableLiveData()

    fun getAllDocuments(docType: Int = 0) = viewModelScope.launch {
        getDocumentSaveCall(docType)
    }
    fun getDocuments(docType: Int = 0, subjectCode:String) = viewModelScope.launch {
        getDocumentSaveCall(docType,false,subjectCode)
    }


    private fun formDummyQuery() {
        // val query: Query =
    }

    private suspend fun getDocumentSaveCall(docType: Int = 0,getAllDocs:Boolean=true, subjectCode: String?=null) {
        documentResponse.value = NetworkResult.Loading()
            try {
                val docList = mutableListOf<Document>()
                val response = if(getAllDocs){
                    repository.remote.getAllDocuments(docType)
                }else{
                    subjectCode?.let {
                        repository.remote.getDocuments(docType,subjectCode)
                    }
                }
                response?.get()?.addOnSuccessListener { snapshot ->
                    snapshot.documents.forEach {
                    val doc = it.toObject(Document::class.java)
                    doc?.documentId = it.id
                    if (doc != null) {
                        docList.add(doc)
                    }
                }
                    documentResponse.postValue(handleDocumentResponse(docList))
                    offlineCacheDocuments(docList)
                }?.addOnFailureListener {
                    documentResponse.value = NetworkResult.Error("Something went wrong")
                }

            } catch (e: Exception) {
                documentResponse.value = NetworkResult.Error("Documents not found.")
            }

    }

    private fun offlineCacheDocuments(docList: List<Document>) {
        insertDocumentList(docList)
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
