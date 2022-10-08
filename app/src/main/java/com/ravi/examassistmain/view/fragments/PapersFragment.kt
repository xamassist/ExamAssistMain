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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ravi.examassistmain.R
import com.ravi.examassistmain.adapters.PapersAdapter
import com.ravi.examassistmain.models.entity.Document
import com.ravi.examassistmain.utils.*
import com.ravi.examassistmain.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PapersFragment : Fragment() {

    var notesRecyclerView: RecyclerView? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var networkListener: NetworkListener
    private val mAdapter by lazy { PapersAdapter() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_paper, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        notesRecyclerView = view.findViewById(R.id.rb_paperRecyclerView)
        swipeRefreshLayout = view.findViewById(R.id.papersRefreshLayout)
        setAdapter()
        setData()
        setListeners()
        return view
    }

    private fun setListeners() {
        swipeRefreshLayout?.setOnRefreshListener {
        }
    }

    private fun setData() {
        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                networkListener.checkNetworkAvailability(requireContext()).collect {
                    requestApiData()
                }
            }
        }
    }
    private fun requestApiData(){
        mainViewModel.getAllDocuments(DocumentType.PAPERS.value)
        mainViewModel.documentResponse.observe(viewLifecycleOwner) { response ->
            response?.let { res ->
                Log.v("papersAdapterres", "got something ${res.data.toString()}")

                when (res) {

                    is NetworkResult.Success -> {
                        LoadingUtils.hideDialog()
                        if (!res.data.isNullOrEmpty()) {
                            val filteredList = res.data.filter { it.documentType == 1 }
                            mAdapter.setData(filteredList)
                            res.data.forEachIndexed { i, v ->
                                Log.v("NotesAdapter$i", "data received!!! ${v.documentTitle} n $v")
                            }
                        } else {
                            Log.v(
                                "NotesAdapter",
                                "hot was successful but no data document received"
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        LoadingUtils.hideDialog()
                        Log.v("NotesAdapter", "Firestore call error")
                    }
                    is NetworkResult.Loading -> {
                        Log.v("NotesAdapter", "still loading  \uD83E\uDD74")
                        LoadingUtils.showDialog(requireContext())

                    }
                }
            }
        }
    }
    private fun readDatabase() {
        mainViewModel.getDocumentByDocType(1)
        lifecycleScope.launch {
            mainViewModel.readDocs.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    val filteredList = database.filter { it.documentType==1 }

                    mAdapter.setData(filteredList)
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