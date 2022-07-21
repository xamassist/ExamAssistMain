package com.ravi.examassistmain.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ravi.examassistmain.R
import com.ravi.examassistmain.databinding.ActivitySplashBinding
import com.ravi.examassistmain.databinding.ActivityUserPreferenceBinding
import com.ravi.examassistmain.utils.UserPreferenceFragment

class UserPreferenceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserPreferenceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPreferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.userPrefVP.adapter = PageAdapter(supportFragmentManager)
    }
    class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return 3
        }

        override fun getItem(position: Int): Fragment {
            when(position) {
                0 -> {
                    return UserPreferenceFragment()
                }
                1 -> {
                    return UserPreferenceFragment()
                }
                2 -> {
                    return UserPreferenceFragment()
                }
                else -> {
                    return UserPreferenceFragment()
                }
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position) {
                0 -> {
                    return "Tab 1"
                }
                1 -> {
                    return "Tab 2"
                }
                2 -> {
                    return "Tab 3"
                }
            }
            return super.getPageTitle(position)
        }

    }
}