package com.example.ourmenu.menu.menuInfo.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.menu.data.MenuImgUrl
import com.example.ourmenu.databinding.ItemMenuInfoImageBinding
import com.example.ourmenu.util.Utils.isNotNull

class MenuInfoVPAdapter(
    val items: ArrayList<MenuImgUrl>,
    val context: Context,
) : RecyclerView.Adapter<MenuInfoVPAdapter.ViewHolder>() {
    inner class ViewHolder(
        val binding: ItemMenuInfoImageBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuImgUrl) {
            // TODO Glide 추가

            if (item.menuImgUrl.isNotNull()) {
                Glide
                    .with(context)
                    .load(item.menuImgUrl)
                    .centerCrop()
                    .into(binding.ivItemMenuInfoImage)
            } else {
                binding.ivItemMenuInfoImage.setImageResource(R.drawable.default_image)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemMenuInfoImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
