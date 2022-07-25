package com.ravi.examassistmain.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ravi.examassistmain.databinding.ActivitySplashBinding
import com.ravi.examassistmain.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    private val loginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel.readUser.observe(this){
            if(it.isNullOrEmpty()){
                Handler(Looper.getMainLooper()).postDelayed ({
                    startActivity(Intent(this, OnBoardingActivity::class.java))
                }, 1200)
            }else{
                Handler(Looper.getMainLooper()).postDelayed ({
                    startActivity(Intent(this, UserPreferenceActivity::class.java))
                }, 1200)
            }
        }



    }
}