package com.example.ourmenu.menu.menuFolder.post.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.addMenu.adapter.AddMenuPlaceMenuRVAdapter
import com.example.ourmenu.addMenu.adapter.AddMenuPlaceMenuRVAdapter.Companion
import com.example.ourmenu.data.DummyMenuData
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.databinding.ItemAddMenuBtnBinding
import com.example.ourmenu.databinding.ItemAddMenuDefaultBinding
import com.example.ourmenu.databinding.ItemAddMenuPlaceMenuBinding
import com.example.ourmenu.databinding.ItemMenuFolderDetailMenuBinding
import com.example.ourmenu.util.Utils.toWon

class PostMenuFolderRVAdapter(
    private val items: ArrayList<MenuData>, val context: Context,
    val onButtonClicked: () -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var groupId = ArrayList<Int>()

    init {
        for (i in 0 until items.size) {
            groupId.add(items[0].groupId)
        }
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_BUTTON = 1
    }

    inner class MenuViewHolder(val binding: ItemMenuFolderDetailMenuBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuData) {
            binding.tvItemMfdMenuName.text = item.menuTitle
            binding.tvItemMfdMenuPlace.text = item.placeTitle
            binding.tvItemMfdMenuAddress.text = item.placeAddress
            binding.tvItemMfdPrice.text = toWon(item.menuPrice)

            Glide.with(context)
                .load(item.menuImgUrl)
                .into(binding.sivItemMfdMenuImage)
            // 안보이게
            binding.ivItemMfdExtraButton.visibility = View.INVISIBLE

        }
    }

    inner class ButtonViewHolder(
        private val binding: ItemAddMenuBtnBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnAddMenuAddMenu.text = "메뉴 가져오기"
            binding.btnAddMenuAddMenu.setOnClickListener {
                onButtonClicked()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == PostMenuFolderRVAdapter.VIEW_TYPE_ITEM) {
            val binding = ItemMenuFolderDetailMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MenuViewHolder(binding)
        } else {
            val binding = ItemAddMenuBtnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ButtonViewHolder(binding)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MenuViewHolder) holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size + 1

    override fun getItemViewType(position: Int): Int =
        if (position == items.size) PostMenuFolderRVAdapter.VIEW_TYPE_BUTTON else PostMenuFolderRVAdapter.VIEW_TYPE_ITEM

}
