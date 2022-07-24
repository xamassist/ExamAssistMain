package com.ravi.examassistmain.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ravi.examassistmain.databinding.PreferenceOptionRowBinding
import com.ravi.examassistmain.utils.*

class UserPreferenceAdapter(var list: List<String>?,val userPrefListener :PreferenceSelectionListener?=null
) :
    RecyclerView.Adapter<UserPreferenceAdapter.ViewHolder>() {
    var selectedIndex = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PreferenceOptionRowBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(list?.get(position) ?: "", position)
    }

    inner class ViewHolder(val binding: PreferenceOptionRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(name: String, position: Int) {
            binding.apply {
                tvFullName.text = name
                tvShortName.text = getInitials(name)
                tvShortName.setBackgroundColor(ViewUtils.instance.colorGenerator(position))
                tvShortNameIV.setBackgroundColor(ViewUtils.instance.colorGenerator(position))
                if (selectedIndex == position) {
                    tvShortName.hide()
                    tvShortNameIV.show()
                } else {
                    tvShortName.show()
                    tvShortNameIV.hide()
                }
                itemView.setOnClickListener {
                    selectedIndex = position
                    userPrefListener?.selectedIndex(selectedIndex)
                    if (tvShortName.isVisible) {
                        tvShortName.hide()
                        tvShortNameIV.show()
                    } else {
                        tvShortName.show()
                        tvShortNameIV.hide()
                    }

                }
            }
        }
    }

    private fun getInitials(name: String): String {
        var nameInitials = ""
        val nameList = name.toCharArray()
        if (nameList.isNotEmpty()) {
            for (item in nameList) {
                if (item.isUpperCase()) {
                    nameInitials += item.toString()
                }
            }
        }
        return nameInitials
    }

    fun setData(newData: List<String>) {

        val recipesDiffUtil =
            DocumentDiffUtil(list, newData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        list = newData
        diffUtilResult.dispatchUpdatesTo(this)

    }

    fun setSelection(index: Int) {


    }
}
