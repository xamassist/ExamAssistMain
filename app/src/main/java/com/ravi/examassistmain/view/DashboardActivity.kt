package com.ravi.examassistmain.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.ravi.examassistmain.R
import com.ravi.examassistmain.databinding.ActivityDashboardBinding
import com.ravi.examassistmain.databinding.BaseLayoutBinding
import com.ravi.examassistmain.view.fragments.NotesFragment
import com.ravi.examassistmain.view.fragments.PapersFragment
import com.ravi.examassistmain.view.fragments.SyllabusFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var bindingBase: BaseLayoutBinding
    private lateinit var context: Context
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    var subjectList = mutableListOf("Electrical Engineering", "Electrical Machine")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        bindingBase = binding.baseLayout
        context = this
        setContentView(view)

        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, binding.drawerLayout, R.string.nav_open, R.string.nav_close)

        actionBarDrawerToggle?.let {
            binding.drawerLayout.addDrawerListener(it)
        }
        actionBarDrawerToggle?.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        binding.baseLayout.ivMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        }
        setSpinner(subjectList)
        loadFragments(NotesFragment())
    }

    private fun setSpinner(listCat: List<String>) {
        val spinnerArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listCat
        )
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        binding.baseLayout.spSubjects?.apply {
            adapter = spinnerArrayAdapter

            setSelection(selectedCatIndex)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    selectedCatIndex = position
                }
            }
        }
    }
    var selectedCatIndex = -1

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