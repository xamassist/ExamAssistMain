package com.ravi.examassistmain.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ravi.examassistmain.adapters.PaperAdapter
import com.ravi.examassistmain.R
import com.ravi.examassistmain.models.PaperResponse

class PaperFragment : Fragment() {

    var rb_paperRecyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_paper, container, false)

        rb_paperRecyclerView = view.findViewById(R.id.rb_paperRecyclerView)

        val myListData: Array<PaperResponse> = arrayOf<PaperResponse>(

            PaperResponse("The Cornot Cycle"),
            PaperResponse("Lows and Thermodynamics"),
            PaperResponse("The Cornot Cycle"),
            PaperResponse("Lows and Thermodynamics"),
            PaperResponse("The Cornot Cycle"),
            PaperResponse("Lows and Thermodynamics"),
            PaperResponse("The Cornot Cycle"),
            PaperResponse("Lows and Thermodynamics"),
            PaperResponse("The Cornot Cycle"),
            PaperResponse("Lows and Thermodynamics"),
            PaperResponse("The Cornot Cycle"),
            PaperResponse("Lows and Thermodynamics"),
            PaperResponse("The Cornot Cycle")
        )

        val adapter = activity?.let { PaperAdapter(it, myListData) }
        rb_paperRecyclerView?.setHasFixedSize(true)
        rb_paperRecyclerView?.layoutManager = LinearLayoutManager(activity)
        rb_paperRecyclerView?.adapter = adapter


        return view
    }
}