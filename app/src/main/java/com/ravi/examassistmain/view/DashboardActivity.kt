package com.ravi.examassistmain.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ravi.examassistmain.view.fragments.NotesFragment
import com.ravi.examassistmain.view.fragments.SyllabusFragment
import com.ravi.examassistmain.R
import com.ravi.examassistmain.viewmodel.MainViewModel
import com.ravi.examassistmain.databinding.ActivityDashboardBinding
import com.ravi.examassistmain.databinding.BaseLayoutBinding
import com.ravi.examassistmain.view.fragments.PapersFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var bindingBase: BaseLayoutBinding
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        bindingBase = binding.baseLayout
        context = this
        setContentView(view)
        bindingBase.bottomNavigationView.background = null
        bindingBase.bottomNavigationView.menu.getItem(3).isEnabled = false

        bindingBase.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            var fragment: Fragment? = null
            when (menuItem.itemId) {
                R.id.navigation_notes -> fragment = NotesFragment()
                R.id.navigation_paper -> fragment = PapersFragment()
                R.id.navigation_syllabus -> fragment = SyllabusFragment()
            }
            loadFragments(fragment)
            return@setOnItemSelectedListener true
        }
        loadFragments(NotesFragment())
    }

    override fun onBackPressed() {
        if (bindingBase.bottomNavigationView.selectedItemId == R.id.navigation_notes) {
            super.onBackPressed()
            finish()
        } else {
            bindingBase.bottomNavigationView.selectedItemId = R.id.navigation_notes
        }

    }

    private fun loadFragments(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.mainFrameLayout, fragment)
                .commit()
        }
        return true
    }

}