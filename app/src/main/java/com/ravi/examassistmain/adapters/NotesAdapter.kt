package com.ravi.examassistmain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ravi.examassistmain.R
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.utils.DocumentDiffUtil
import com.ravi.examassistmain.utils.ViewUtils

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    private var doc = emptyList<Document>()
    var cxt: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.notes_list, parent, false)
        cxt = parent.context
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return doc.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foodList: Document = doc[position]

        holder.docTitle.text = foodList.documentTitle
        val firstLetter = foodList.documentTitle.substring(0, 1)
        holder.docFirstName.text = firstLetter
        cxt?.let {
            holder.llNotes.background = ViewUtils.instance.drawCircle(
                ContextCompat.getColor(
                    it,
                    ViewUtils.instance.colorGenerator()
                )
            )
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val docTitle: TextView = itemView.findViewById(R.id.txt_name)
        val llNotes: LinearLayout = itemView.findViewById(R.id.ll_notes_icon)
        val docFirstName: TextView = itemView.findViewById(R.id.tv_name_icon)

    }

    fun setData(newData: List<Document>) {
        val recipesDiffUtil =
            DocumentDiffUtil(doc, newData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        doc = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }
}
