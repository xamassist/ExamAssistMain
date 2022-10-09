package com.ravi.examassistmain.view.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ravi.examassistmain.R
import com.ravi.examassistmain.adapters.NotesAdapter
import com.ravi.examassistmain.databinding.FragmentNotesBinding
import com.ravi.examassistmain.models.entity.Document
import com.ravi.examassistmain.utils.NetworkListener
import com.ravi.examassistmain.utils.NetworkResult
import com.ravi.examassistmain.utils.lv
import com.ravi.examassistmain.utils.observeOnce
import com.ravi.examassistmain.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class NotesFragment : Fragment() {

    private lateinit var binding:FragmentNotesBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var networkListener: NetworkListener

    private val mAdapter by lazy { NotesAdapter() }
    var subjectCode = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            subjectCode = it.getString("subject_code") ?:""
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNotesBinding.inflate(layoutInflater)
        //val view =  inflater.inflate(R.layout.fragment_notes, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setAdapter()
        lv("checkk","onCreateView-notes")
        return  binding.root
    }

    override fun onResume() {
        super.onResume()
        setData()
    }


    private fun setData(){
        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                networkListener.checkNetworkAvailability(requireContext()).collect {
                    readDatabase()
                }
            }
        }
    }
    private fun requestApiData(){
     //  mainViewModel.getAllDocuments(0)
        Log.d("requestApiData", "requestApiData:$subjectCode ")
       mainViewModel.getDocuments(0,subjectCode)
        mainViewModel.documentResponse.observe(viewLifecycleOwner) { response ->

            response?.let { res ->
                Log.v("NotesAdapter", "got something ${res.data.toString()}")

                when (res) {

                    is NetworkResult.Success -> {
                        if (!res.data.isNullOrEmpty()) {
                            val filteredList = res.data.filter { it.documentType==0 }
                            mAdapter.setData(filteredList)
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
    private fun readDatabase() {
        mainViewModel.getDocumentByDocType(0)
            mainViewModel.readDocs.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    val filteredList = database.filter { it.documentType==0 && it.subject_code?.contains(subjectCode) == true }

                    mAdapter.setData(filteredList)
                } else {
                    requestApiData()
                }
            }

    }
    private fun setAdapter(){
        binding.notesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
    }
    companion object {

        @JvmStatic
        fun newInstance(subjectCode:String) =
            NotesFragment().apply {
                arguments = Bundle().apply {
                    putString("subject_code", subjectCode)
                }
            }
    }

}