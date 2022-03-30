package com.ravi.examassistmain.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ravi.examassistmain.R
import com.ravi.examassistmain.animation.animationtypes.AntiClockSpinTransformation
import com.ravi.examassistmain.databinding.ActivityOnBoardingScreenBinding

//@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity(){
    private lateinit var context: Context
    private var mAdapter: ViewsSliderAdapter? = null
    private lateinit var layouts: IntArray
    private lateinit var binding: ActivityOnBoardingScreenBinding

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingScreenBinding.inflate(layoutInflater)
        val view = binding.root
        context = this
        setContentView(view)
        init()
    }

    private fun init() {

        layouts = intArrayOf(R.layout.onboarding_one, R.layout.onboarding_two, R.layout.onboarding_three)
        mAdapter = ViewsSliderAdapter()
        binding.onBoardingVP.adapter = mAdapter
        binding.onBoardingVP.registerOnPageChangeCallback(pageChangeCallback)
        binding.btnNextScreen.setOnClickListener { _ ->
            val current: Int = getItem(+1)
            if (current < layouts.size) {
                binding.onBoardingVP.currentItem = current
            } else {
                launchHomeScreen()
            }
        }
        binding.wormDotsIndicator.setViewPager2(binding.onBoardingVP)
        binding.onBoardingVP.setPageTransformer(AntiClockSpinTransformation())
        binding.btnNextScreen.setOnClickListener{startActivity(Intent(this, DashboardActivity::class.java))}
    }
/*
Shortlisted animations
CubeInScalingTransformation
CubeInDepthTransformation 2
CubeInRotationTransformation 3
DepthPageTransformer 4
FidgetSpinTransformation 7
AntiClockSpinTransformation 1
DepthTransformation 5
FanTransformation 6
 */

    private fun getItem(i: Int): Int {
        return binding.onBoardingVP.currentItem + i
    }
    private fun launchHomeScreen() {}

    private var pageChangeCallback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
        }
    }


    inner class ViewsSliderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(viewType, parent, false)
            return SliderViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
        override fun getItemViewType(position: Int): Int {
            return layouts[position]
        }

        override fun getItemCount(): Int {
            return layouts.size
        }

        inner class SliderViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

        }
    }

}
