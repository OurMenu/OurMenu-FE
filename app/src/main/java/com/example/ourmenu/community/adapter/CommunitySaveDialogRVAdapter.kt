package com.example.ourmenu.community.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.R
import com.example.ourmenu.databinding.CommunitySaveDialogMenuFolderItemBinding


class CommunitySaveDialogRVAdapter(
    private val context: Context,
    private var items: ArrayList<String>,
) :
    RecyclerView.Adapter<CommunitySaveDialogRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CommunitySaveDialogMenuFolderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(item: String) {
            binding.itemCsdsiText.text = item
            if (binding.itemCsdsiCheckbox.isChecked) {
                binding.itemCsdsiText.setTextColor(R.color.Neutral_700)
                binding.root.setBackgroundResource(R.drawable.btn_bg_8_n300)
            } else {
                binding.itemCsdsiText.setTextColor(R.color.Neutral_500)
                binding.root.setBackgroundResource(R.drawable.btn_bg_8_n100)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunitySaveDialogRVAdapter.ViewHolder {
        val binding =
            CommunitySaveDialogMenuFolderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CommunitySaveDialogRVAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }


}

