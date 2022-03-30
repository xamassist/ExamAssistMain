package com.ravi.examassistmain.viewmodel
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.ravi.examassistmain.data.Repository
import com.ravi.examassistmain.data.database.DocumentEntity
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

    val readDocument: LiveData<List<DocumentEntity>> = repository.local.readDocument().asLiveData()

    private fun insertDocument (documentEntity: DocumentEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(documentEntity)
        }

    fun deleteDocument(documentEntity: DocumentEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(documentEntity)
        }

    fun deleteAllDocument() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllDocuments()
        }

    /** Firestore */
    var documentResponse: MutableLiveData<NetworkResult<List<Document>>> = MutableLiveData()

    @RequiresApi(Build.VERSION_CODES.M)
    fun getAllDocuments() = viewModelScope.launch {
        getDocumentSaveCall()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getDocumentSaveCall() {
        documentResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getAllDocuments()
                documentResponse.value = handleDocumentResponse(response)

                val doc = documentResponse.value?.data
                if(doc != null) {
                    offlineCacheDocuments(doc)
                }else{
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
        val documents = Documents(docList)
        val recipesEntity = DocumentEntity(documents)
        insertDocument(recipesEntity)
    }


    private fun handleDocumentResponse(response: List<Document>?): NetworkResult<List<Document>> {
        response?.let { docList ->
            return if(!docList.isNullOrEmpty()){
                NetworkResult.Success(docList)
            }else{
                NetworkResult.Error("No files found")
            }

        }?.run{
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
