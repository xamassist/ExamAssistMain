package com.ravi.examassistmain.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ravi.examassistmain.R
import com.ravi.examassistmain.databinding.FragmentUserPreferenceBinding
import java.io.Serializable

private const val PREFERENCE_TITLE = "pref_title"
private const val ITEM_LIST = "item_list"

class UserPreferenceFragment : Fragment() {
    private var preferenceTitle: String? = null
    private var itemList: List<String>? = null

    private lateinit var bindingSignIn: FragmentUserPreferenceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            preferenceTitle = it.getString(PREFERENCE_TITLE)
            itemList = it.getSerializable(ITEM_LIST) as List<String>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingSignIn = FragmentUserPreferenceBinding.inflate(inflater, container, false)
        return bindingSignIn.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      bindingSignIn.tvTitle.text = preferenceTitle


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
}