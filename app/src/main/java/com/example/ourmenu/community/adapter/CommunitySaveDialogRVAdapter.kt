package com.example.ourmenu.community.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.R
import com.example.ourmenu.data.menuFolder.data.MenuFolderData
import com.example.ourmenu.databinding.CommunitySaveDialogMenuFolderItemBinding
import com.example.ourmenu.databinding.ItemAddMenuFolderBinding


class CommunitySaveDialogRVAdapter(
    val items: ArrayList<MenuFolderData>,
    val onItemsSelected: (ArrayList<MenuFolderData>) -> Unit, // MenuFolderData 자체를 전달
) :
    RecyclerView.Adapter<CommunitySaveDialogRVAdapter.ViewHolder>() {
    private val selectedItems = ArrayList<MenuFolderData>() // MenuFolderData 목록으로 관리

    inner class ViewHolder(val binding: ItemAddMenuFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuFolderData) {
            binding.tvAddMenuFolder.text = item.menuFolderTitle

            // 체크박스 초기 상태 설정
            binding.cbAddMenuFolder.setOnCheckedChangeListener(null)
            binding.cbAddMenuFolder.isChecked = selectedItems.contains(item)

            // 체크박스 클릭 이벤트 처리
            binding.cbAddMenuFolder.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(item)
                } else {
                    selectedItems.remove(item)
                }
                onItemsSelected(selectedItems)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunitySaveDialogRVAdapter.ViewHolder {
        val binding =
            ItemAddMenuFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CommunitySaveDialogRVAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun getSelectedItems(): ArrayList<MenuFolderData> = selectedItems


}

