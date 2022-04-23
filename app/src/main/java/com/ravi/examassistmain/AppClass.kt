package com.ravi.examassistmain

import android.app.Application
import com.google.firebase.FirebaseApp
import com.jaredrummler.cyanea.Cyanea
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class
 */
@HiltAndroidApp
class AppClass : Application(){
    override fun onCreate() {
        super.onCreate()
        Cyanea.init(this, resources);
        FirebaseApp.initializeApp(this);
    }
}