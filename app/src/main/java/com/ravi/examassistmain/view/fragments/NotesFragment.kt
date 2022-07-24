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
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.utils.LoadingUtils
import com.ravi.examassistmain.utils.NetworkListener
import com.ravi.examassistmain.utils.NetworkResult
import com.ravi.examassistmain.utils.observeOnce
import com.ravi.examassistmain.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NotesFragment : Fragment() {

    var notesRecyclerView: RecyclerView? = null
    private var documentArray: MutableList<Document> = mutableListOf()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var networkListener: NetworkListener

    private val mAdapter by lazy { NotesAdapter() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_notes, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        notesRecyclerView = view.findViewById(R.id.rb_paperRecyclerView)
        setAdapter()
        setData()
        return  view
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
       mainViewModel.getAllDocuments(0)
        mainViewModel.documentResponse.observe(viewLifecycleOwner) { response ->

            response?.let { res ->
                Log.v("NotesAdapter", "got something ${res.data.toString()}")

                when (res) {

                    is NetworkResult.Success -> {
                        if (!res.data.isNullOrEmpty()) {
                            mAdapter.setData(res.data)
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

        lifecycleScope.launch {
            mainViewModel.readDocs.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    mAdapter.setData(database)
                } else {
                    requestApiData()
                }
            }
        }
    }
    private fun setAdapter(){
        notesRecyclerView?.setHasFixedSize(true)
        notesRecyclerView?.layoutManager = LinearLayoutManager(activity)
        notesRecyclerView?.adapter = mAdapter
    }

}