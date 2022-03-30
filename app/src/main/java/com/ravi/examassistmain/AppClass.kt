package com.ravi.examassistmain

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class
 */
@HiltAndroidApp
class AppClass : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this);
    }
}