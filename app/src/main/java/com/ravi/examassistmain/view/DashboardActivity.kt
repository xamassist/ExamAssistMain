package com.ravi.examassistmain.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ravi.examassistmain.R
import com.ravi.examassistmain.databinding.ActivityDashboardBinding
import com.ravi.examassistmain.databinding.BaseLayoutBinding
import com.ravi.examassistmain.models.EAUsers
import com.ravi.examassistmain.utils.NetworkListener
import com.ravi.examassistmain.utils.NetworkResult
import com.ravi.examassistmain.utils.observeOnce
import com.ravi.examassistmain.view.fragments.NotesFragment
import com.ravi.examassistmain.view.fragments.PapersFragment
import com.ravi.examassistmain.view.fragments.SyllabusFragment
import com.ravi.examassistmain.viewmodel.DashboardViewModel
import com.ravi.examassistmain.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var bindingBase: BaseLayoutBinding
    private lateinit var context: Context
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private val viewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    var subjectList = mutableListOf("Electrical Engineering", "Electrical Machine")
    var userData: EAUsers?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        bindingBase = binding.baseLayout
        context = this
        setContentView(view)

        setSetSideNavigation()
        loadFragments(NotesFragment())
        setData()
    }
    private fun setData(){
        viewModel.readUser.observeOnce(this){
            userData = it.first()
            val branch = it.first()?.branch ?:""
            val university:Int = 0//it.first()?.university?:0
            val semester = it.first()?.semester?:0
            lifecycleScope.launchWhenStarted {
                readDatabase(branch,semester,university)
            }
        }

    }
    private fun readDatabase(branch:String,semester:Int,university:Int) {

        lifecycleScope.launch {
            viewModel.readSubjects.observeOnce(this@DashboardActivity) { subjects ->
                if (subjects.isNotEmpty()) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    //mAdapter.setData(database)
                    val subjectList = subjects.map { it.subjectName }
                    setSpinner(subjectList)
                } else {
                    requestApiData(branch,semester,university)
                }
            }
        }
    }

    private  fun requestApiData(branch:String, semester:Int, university:Int){
        viewModel.getSubjectList(branch,semester,university)
        viewModel.subjectResponse.observe(this) { response ->

            response?.let { res ->
                Log.v("NotesAdapter", "got something ${res.data.toString()}")

                when (res) {

                    is NetworkResult.Success -> {
                        if (!res.data.isNullOrEmpty()) {
                           // mAdapter.setData(res.data)
                            val subjectList = res.data.map { it.subjectName }
                            setSpinner(subjectList)
                            Log.v("NotesAdapter", "data received!!! ${res.data.first()}")
                        } else {
                            Log.v(
                                "NotesAdapter",
                                "hot was successful but no data document received"
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        Log.v("NotesAdapter", "Firestore call error")
                    }
                    is NetworkResult.Loading -> {
                        Log.v("NotesAdapter", "still loading  \uD83E\uDD74")

                    }
                }
            }
        }
    }

    private fun setSetSideNavigation() {
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
    }

    fun getUserData() {
      viewModel.readUser.observe(this){
      val branch = it.first()?.branch
      val sem = it.first()?.semester
      val university = it.first()?.university

      }
    }

    private fun setSpinner(listCat: List<String?>) {
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