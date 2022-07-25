package com.ravi.examassistmain.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ravi.examassistmain.R
import com.ravi.examassistmain.databinding.NotesListBinding
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.pdf.PdfActivity
import com.ravi.examassistmain.utils.DocumentDiffUtil
import com.ravi.examassistmain.utils.ViewUtils

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    private var doc = emptyList<Document>()
    var cxt: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = NotesListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return doc.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(doc[position],position)
    }

    inner class ViewHolder(val binding: NotesListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(document: Document,position: Int) {
            binding.docName.text = document.documentTitle
            if (document.documentTitle?.isNotBlank() == true) {
                val firstLetter = document.documentTitle?.substring(0, 1)
                binding.tvNameIcon.text = firstLetter
            }

                binding.llNotesIcon.background = ViewUtils.instance.drawCircle(
                    ContextCompat.getColor(
                        binding.llNotesIcon.context,
                        ViewUtils.instance.colorGenerator(position)
                    )
                )

            binding.mainCardView.setOnClickListener {

                    val intent = Intent(binding.mainCardView.context, PdfActivity::class.java)
                    intent.putExtra("document", document)
                binding.mainCardView.context.startActivity(intent)

            }
        }
    }

    fun setData(newData: List<Document>) {
        val recipesDiffUtil =
            DocumentDiffUtil(doc, newData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        doc = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }
}