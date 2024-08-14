package com.example.ourmenu.menu.menuInfo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.data.menu.data.MenuImage
import com.example.ourmenu.databinding.ItemMenuInfoImageBinding


class MenuInfoVPAdapter(val items: ArrayList<MenuImage>, val context: Context) :
    RecyclerView.Adapter<MenuInfoVPAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMenuInfoImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuImage) {
            // TODO Glide 추가
            Glide.with(context)
                .load(item.menuImgUrl)
                .into(binding.ivItemMenuInfoImage)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuInfoImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
