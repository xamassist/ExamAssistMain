package com.ravi.examassistmain.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.ravi.examassistmain.databinding.ActivityTestBinding


class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding


    var thumb_up: LottieAnimationView? = null
    var thumb_down: LottieAnimationView? = null
    var toggle: LottieAnimationView? = null
    var flag = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
    private fun changeState() {
        flag = if (flag == 0) {
            toggle?.setMinAndMaxProgress(
                0f,
                0.43f
            ) //Here, calculation is done on the basis of start and stop frame divided by the total number of frames
            toggle?.playAnimation()
            1
            //---- Your code here------
        } else {
            toggle?.setMinAndMaxProgress(0.5f, 1f)
            toggle?.playAnimation()
            0
            //---- Your code here------
        }
    }
}