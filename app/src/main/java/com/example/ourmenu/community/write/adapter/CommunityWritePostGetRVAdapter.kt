package com.example.ourmenu.community.write.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.data.DummyMenuData
import com.example.ourmenu.databinding.ItemMenuFolderBinding
import com.example.ourmenu.menu.iteminterface.MenuFolderItemClickListener

class CommunityWritePostGetRVAdapter(private val items: ArrayList<DummyMenuData>) :
    RecyclerView.Adapter<CommunityWritePostGetRVAdapter.ViewHolder>() {

    lateinit var itemClickListener: MenuFolderItemClickListener

    fun setOnItemClickListener(onItemListener: MenuFolderItemClickListener) {
        itemClickListener = onItemListener
    }

    inner class ViewHolder(val binding: ItemMenuFolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DummyMenuData, position: Int) {
            binding.ivItemMenuFolderImage
            binding.tvItemMenuFolderMenuCount.text = item.menuCount.toString()
            binding.tvItemMenuFolderTitle.text = item.title


            binding.ivItemMenuFolderImage.setOnClickListener {
                itemClickListener.onMenuClick(position)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityWritePostGetRVAdapter.ViewHolder {
        val binding = ItemMenuFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityWritePostGetRVAdapter.ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

}
