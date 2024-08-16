package com.example.ourmenu.menu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.databinding.ItemMenuFolderDetailMenuBinding
import com.example.ourmenu.menu.callback.DiffUtilCallback
import com.example.ourmenu.util.Utils.toWon

// TODO 데이터 종류 수정
class MenuFolderDetailAllRVAdapter(
    val items: ArrayList<MenuData>, val context: Context,
    val onMenuClick : (MenuData) -> Unit,
    val onMapClick : (MenuData) -> Unit
) :
    RecyclerView.Adapter<MenuFolderDetailAllRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMenuFolderDetailMenuBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuData) {
            binding.tvItemMfdMenuName.text = item.menuTitle
            binding.tvItemMfdMenuPlace.text = item.placeTitle
            binding.tvItemMfdMenuAddress.text = item.placeAddress
            binding.tvItemMfdPrice.text = toWon(item.menuPrice)

            Glide.with(context)
                .load(item.menuImgUrl)
                .into(binding.sivItemMfdMenuImage)

        }
    }

    // 데이터 업데이트 함수
    fun updateList(sortedItems: ArrayList<MenuData>) {
        val diffCallback = DiffUtilCallback(items, sortedItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(sortedItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuFolderDetailAllRVAdapter.ViewHolder {
        val binding = ItemMenuFolderDetailMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuFolderDetailAllRVAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
