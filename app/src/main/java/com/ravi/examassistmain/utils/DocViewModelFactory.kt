package com.ravi.examassistmain.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ravi.examassistmain.viewmodel.MainViewModel
import com.ravi.examassistmain.data.Repository

class DocViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory{


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }


}
