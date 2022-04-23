package com.ravi.examassistmain.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.google.firebase.firestore.Query
import com.ravi.examassistmain.data.Repository
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.models.Documents
import com.ravi.examassistmain.utils.NetworkResult

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    /** ROOM DATABASE */

    //val readDocument: LiveData<List<DocumentEntity>> = repository.local.readDocument().asLiveData()

    private fun insertDocument(document: Document) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(document)
        }

    private fun insertDocumentList(documentList: List<Document>) =
        viewModelScope.launch(Dispatchers.IO) {
            for (doc in documentList) {
                repository.local.insertRecipes(doc)
            }
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun getAllDocuments(docType: Int = 0) = viewModelScope.launch {
        getDocumentSaveCall(docType)
    }


    private fun formDummyQuery() {
        // val query: Query =
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getDocumentSaveCall(docType: Int = 0) {
        documentResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val docList = mutableListOf<Document>()
                val response = repository.remote.getAllDocuments(docType)
                response?.get()?.addOnSuccessListener { snapshot ->
                    snapshot.documents.forEach {
                    val doc = it.toObject(Document::class.java)
                    doc?.documentId = it.id
                    if (doc != null) {
                        docList.add(doc)
                    }
                }
                    documentResponse.value = handleDocumentResponse(docList)
                }?.addOnFailureListener {
                    documentResponse.value = NetworkResult.Error("Something went wrong")
                }

                val doc = documentResponse.value?.data
                if (doc != null) {
                    offlineCacheDocuments(doc)
                } else {
                    documentResponse.value = NetworkResult.Error("Documents data was null.")
                }
            } catch (e: Exception) {
                documentResponse.value = NetworkResult.Error("Documents not found.")
            }
        } else {
            documentResponse.value = NetworkResult.Error("No Internet Connection.")
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


    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}
