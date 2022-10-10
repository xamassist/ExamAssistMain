package com.ravi.examassistmain.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.ravi.examassistmain.data.Repository
import com.ravi.examassistmain.models.entity.Document
import com.ravi.examassistmain.models.entity.PdfDownloads
import com.ravi.examassistmain.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    /** ROOM DATABASE */

    var readDownloadedFiles = repository.local.readPdfs().asLiveData()
    var downloadedPdf :MutableLiveData<PdfDownloads?> = MutableLiveData()


     fun insertPdf(pdfDownloads: PdfDownloads) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertPdf(pdfDownloads)
        }

    fun getPdf(documentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            downloadedPdf.postValue(repository.local.getPdf(documentId))
        }
    }

    private fun insertDocumentList(documentList: List<Document>) =
        viewModelScope.launch(Dispatchers.IO) {
            for (doc in documentList) {
                repository.local.insertDocument(doc)
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