package com.ravi.examassistmain.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.ravi.examassistmain.R
import com.ravi.examassistmain.databinding.ActivityUserPreferenceBinding
import com.ravi.examassistmain.models.entity.EAUsers
import com.ravi.examassistmain.utils.LoadingUtils
import com.ravi.examassistmain.utils.NetworkResult
import com.ravi.examassistmain.utils.disappear
import com.ravi.examassistmain.utils.show
import com.ravi.examassistmain.view.fragments.UserPreferenceFragment
import com.ravi.examassistmain.viewmodel.UserPreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UserPreferenceActivity : AppCompatActivity() {
    private val preferenceViewModel by lazy { ViewModelProvider(this)[UserPreferenceViewModel::class.java] }

     lateinit var binding: ActivityUserPreferenceBinding
    private var prefUniversityList = listOf<String>()
    private var prefBranchList = listOf<String>()
    private var prefSemesterList = listOf<String>()
    private var valueLoadCount = MutableLiveData(0)
    private var userList: List<EAUsers?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPreferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchData()
        preferenceViewModel.getUserDataFromRoom()
        binding.btnContinue.setOnClickListener {
            if (areAllSelected) {
                saveUserPreference()
                return@setOnClickListener
            }
            val currentItem = binding.userPrefVP.currentItem
            if (currentItem >= 2) {
                return@setOnClickListener
            }
            binding.userPrefVP.currentItem = currentItem + 1

        }
        initObservers()
        // loader = EALoader(binding.root.context)
    }

    var selectedArray = mutableListOf(-1, -1, -1)
    var currentFragment = 0

    private fun fetchData() {

        lifecycleScope.launch {
            preferenceViewModel.getPreference()
        }
    }

    var areAllSelected = false

    fun updateBottomBars() {
        when (currentFragment) {
            0 -> {

                binding.bar1.background =
                    ContextCompat.getDrawable(this, R.drawable.solid_orange_circle_bg)
                binding.bar2.background =
                    ContextCompat.getDrawable(this, R.drawable.solid_gray_circle_bg)
                binding.bar3.background =
                    ContextCompat.getDrawable(this, R.drawable.solid_gray_circle_bg)

                binding.bar1.show()
                binding.bar2.show()
                binding.bar3.show()
                binding.barIV1.disappear()
                binding.barIV2.disappear()
                binding.barIV3.disappear()

            }
            1 -> {
                binding.barIV1.background =
                    ContextCompat.getDrawable(this, R.drawable.solid_purple_circle_bg)
                binding.bar2.background =
                    ContextCompat.getDrawable(this, R.drawable.solid_orange_circle_bg)
                binding.bar3.background =
                    ContextCompat.getDrawable(this, R.drawable.solid_gray_circle_bg)
                binding.bar1.disappear()
                binding.bar2.show()
                binding.bar3.show()
                binding.barIV1.show()
                binding.barIV2.disappear()
                binding.barIV3.disappear()
            }
            2 -> {
                binding.barIV1.background =
                    ContextCompat.getDrawable(this, R.drawable.solid_purple_circle_bg)
                binding.barIV2.background =
                    ContextCompat.getDrawable(this, R.drawable.solid_purple_circle_bg)
                binding.bar3.background =
                    ContextCompat.getDrawable(this, R.drawable.solid_orange_circle_bg)

                binding.bar1.disappear()
                binding.bar2.disappear()
                binding.bar3.show()
                binding.barIV1.show()
                binding.barIV2.show()
                binding.barIV3.disappear()
            }
        }
        areAllSelected = checkIfAllItemSelected()
        if (areAllSelected) {
            binding.btnContinue.text = "Let's Start!"
            binding.barIV3.show()
            binding.bar3.disappear()
            binding.barIV3.background =
                ContextCompat.getDrawable(this, R.drawable.solid_purple_circle_bg)
            binding.barIV1.background =
                ContextCompat.getDrawable(this, R.drawable.solid_purple_circle_bg)
            binding.barIV2.background =
                ContextCompat.getDrawable(this, R.drawable.solid_purple_circle_bg)

            binding.bar1.disappear()
            binding.bar2.disappear()
            binding.bar3.disappear()
            binding.barIV1.show()
            binding.barIV2.show()
            binding.barIV3.showContextMenu()
        }
    }

    private fun saveUserPreference() {
        if (!selectedArray.contains(-1)) {
            userList?.first()?.let { user ->
                user.apply {
                    university = selectedArray[0].toString()
                    branch = getBranchCodeFromIndex(selectedArray[1])
                    semester = selectedArray[2] + 1
                }
                preferenceViewModel.updateUserInRoomDB(user)
                preferenceViewModel.saveUserDataToServer(user)
                startActivity(Intent(this@UserPreferenceActivity, DashboardActivity::class.java))
            }
        }
    }

    fun checkIfAllItemSelected(): Boolean {
        if (selectedArray.first() != -1) {
            if (selectedArray[1] != -1) {
                if (selectedArray[2] != -1) {
                    return true
                }
            }
        }
        return false
    }

    private fun initObservers() {
        LoadingUtils.showDialog(this,false)
        preferenceViewModel.preferenceResponse.observe(this) { list ->
            list?.let { result ->
                when (list) {
                    is NetworkResult.Success -> {
                        LoadingUtils.hideDialog()
                        result.data?.let {
                            val preferenceData =
                                formPreferenceData(it as HashMap<String, List<String>>)


                            binding.userPrefVP.adapter = preferenceData?.let { it1 ->
                                PageAdapter(
                                    supportFragmentManager,
                                    it1
                                )
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        LoadingUtils.hideDialog()
                    }
                    is NetworkResult.Loading -> {
                        LoadingUtils.showDialog(this, false)
                    }
                }
            }
        }
        preferenceViewModel.readUser.observe(this) { userList = it }

        binding.userPrefVP.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (currentFragment !== position) {

                    currentFragment = position
                    updateBottomBars()
                }
            }

            override fun onPageSelected(position: Int) {}
        })
    }

    fun getBranchCodeFromIndex(index: Int): String {
        return when (index) {
            0 -> "EE"
            1 -> "ECE"
            2 -> "CSE"
            3 -> "CE"
            4 -> "ME"
            5 -> "EIE"
            else -> ""
        }
    }

    private fun formPreferenceData(preferenceMap: HashMap<String, List<String>>): UserPreference? {
        val universityList = preferenceMap["university"]
        val branchList = preferenceMap["branch"]
        val semesterList = preferenceMap["semester"]

        val prefTitleList = listOf("University", "Branch", "Semester")
        val prefDataList = listOf(universityList, branchList, semesterList)
        return UserPreference(prefTitleList, prefDataList as List<List<String>>)
    }

    class PageAdapter(
        fm: FragmentManager,
        private val prefData: UserPreference,
    ) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return prefData.nameList.size
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    UserPreferenceFragment.newInstance(
                        prefData.nameList.first(),
                        prefData.preList.first()
                    )
                }
                1 -> {
                    UserPreferenceFragment.newInstance(prefData.nameList[1], prefData.preList[1])
                }
                2 -> {
                    UserPreferenceFragment.newInstance(prefData.nameList[2], prefData.preList[2])
                }
                else -> {
                    UserPreferenceFragment.newInstance(
                        prefData.nameList.first(),
                        prefData.preList.first()
                    )
                }
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
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

data class UserPreference(val nameList: List<String>, val preList: List<List<String>>)