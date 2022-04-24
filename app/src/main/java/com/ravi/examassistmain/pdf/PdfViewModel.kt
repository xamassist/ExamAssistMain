package com.ravi.examassistmain.pdf

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ravi.examassistmain.data.Repository
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class PdfViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    /** ROOM DATABASE */
  var getPdfDocument: MutableLiveData<Document> = MutableLiveData()

      fun getDoc(documentId: String) {
    viewModelScope.launch(Dispatchers.IO) {
        getPdfDocument.postValue(repository.local.getDocument(documentId))
   }
}
}