package com.ravi.examassistmain.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ravi.examassistmain.adapters.UserPreferenceAdapter
import com.ravi.examassistmain.databinding.FragmentUserPreferenceBinding
import com.ravi.examassistmain.view.UserPreferenceActivity
import java.io.Serializable

private const val PREFERENCE_TITLE = "pref_title"
private const val ITEM_LIST = "item_list"

class UserPreferenceFragment : Fragment(), PreferenceSelectionListener {
    private var preferenceTitle: String? = null
    private var itemList = mutableListOf<String>()
    private val adapter by lazy { UserPreferenceAdapter(itemList, this) }

    private lateinit var binding: FragmentUserPreferenceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            preferenceTitle = it.getString(PREFERENCE_TITLE)
            itemList = it.getSerializable(ITEM_LIST) as MutableList<String>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserPreferenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = preferenceTitle
        binding.rvUserPreference.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvUserPreference.adapter = adapter
        val distinctList = itemList.distinct()
        adapter.setData(distinctList)
        binding.tvTitle
    }

    private fun getPreferenceData() {
        //preferenceViewModel.getPreference(PreferenceType.UNIVERSITY)
    }

    companion object {

        @JvmStatic
        fun newInstance(title: String, itemList: List<String>) =
            UserPreferenceFragment().apply {
                arguments = Bundle().apply {
                    putString(PREFERENCE_TITLE, title)
                    putSerializable(ITEM_LIST, itemList as Serializable)

                }
            }
    }

    override fun selectedIndex(index: Int) {
        adapter.notifyDataSetChanged()
        var fragIndex = 0
        if (preferenceTitle.equals("University")) {
            fragIndex = 0
        }
        if (preferenceTitle.equals("Branch")) {
            fragIndex = 1
        }
        if (preferenceTitle.equals("Semester")) {
            fragIndex = 2
        }
        (activity as UserPreferenceActivity?)?.selectedArray?.set(fragIndex, index)
        (activity as UserPreferenceActivity?)?.currentFragment = fragIndex
        (activity as UserPreferenceActivity?)?.binding?.userPrefVP?.currentItem = if(fragIndex<2) {fragIndex+1}else{fragIndex}
        (activity as UserPreferenceActivity?)?.updateBottomBars()
    }

}

interface PreferenceSelectionListener {
    fun selectedIndex(index: Int)
}