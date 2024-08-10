package com.example.ourmenu.menu.menuFolder.post.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.databinding.ItemMenuFolderDetailMenuBinding
import com.example.ourmenu.util.Utils.toWon

class PostMenuFolderGetDetailRVAdapter(private val items: ArrayList<MenuData>, val context: Context) :
    RecyclerView.Adapter<PostMenuFolderGetDetailRVAdapter.ViewHolder>() {

    var checkedItems = ArrayList<MenuData>()

    private lateinit var itemClickListener: () -> Unit

    fun setOnItemClickListener(onItemClickListener: () -> Unit) {
        itemClickListener = onItemClickListener
    }

    inner class ViewHolder(val binding: ItemMenuFolderDetailMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isChecked = false

        fun bind(item: MenuData) {
            binding.tvItemMfdMenuName.text = item.menuTitle
            binding.tvItemMfdMenuPlace.text = item.placeTitle
            binding.tvItemMfdMenuAddress.text = item.placeAddress
            binding.tvItemMfdPrice.text = toWon(item.menuPrice)
            binding.ivItemMfdExtraButton.setImageResource(R.drawable.ic_add_menu_stroked)

            if (item.menuImgUrl != "") {
                Glide.with(context)
                    .load(item.menuImgUrl)
                    .into(binding.sivItemMfdMenuImage)
            }
            // 클릭할 때마다 바뀌기
            binding.ivItemMfdExtraButton.setOnClickListener {
                if (this.isChecked) {
                    this.isChecked = false
                    binding.ivItemMfdExtraButton.setImageResource(R.drawable.ic_add_menu_stroked)

                    // check 아이템에서 삭제
                    checkedItems.remove(item)
                } else {
                    this.isChecked = true
                    binding.ivItemMfdExtraButton.setImageResource(R.drawable.ic_add_menu_checked)

                    // check 아이템에 추가
                    checkedItems.add(item)
                }
                itemClickListener()
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostMenuFolderGetDetailRVAdapter.ViewHolder {
        val binding = ItemMenuFolderDetailMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostMenuFolderGetDetailRVAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

}
