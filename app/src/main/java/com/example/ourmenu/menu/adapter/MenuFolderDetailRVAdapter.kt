package com.example.ourmenu.menu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.addMenu.adapter.AddMenuPlaceMenuRVAdapter.MenuViewHolder
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.databinding.ItemAddMenuBtnBinding
import com.example.ourmenu.databinding.ItemAddMenuPlaceMenuBinding
import com.example.ourmenu.databinding.ItemMenuFolderDetailMenuBinding
import com.example.ourmenu.menu.callback.DiffUtilCallback
import com.example.ourmenu.menu.iteminterface.MenuItemClickListener
import com.example.ourmenu.util.Utils.dpToPx
import com.example.ourmenu.util.Utils.toWon

// TODO 데이터 종류 수정
class MenuFolderDetailRVAdapter(
    val items: ArrayList<MenuData>, val context: Context,
    val onButtonClicked: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_BUTTON = 1
    }


    lateinit var itemClickListener: MenuItemClickListener

    fun setOnItemClickListener(onItemClickListener: MenuItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    inner class MenuViewHolder(val binding: ItemMenuFolderDetailMenuBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuData) {
            binding.tvItemMfdMenuName.text = item.menuTitle
            binding.tvItemMfdMenuPlace.text = item.placeTitle
            binding.tvItemMfdMenuAddress.text = item.placeAddress
            binding.tvItemMfdPrice.text = toWon(item.menuPrice)

            if (item.menuImgUrl == "") {

            } else {
                Glide.with(context)
                    .load(item.menuImgUrl)
                    .into(binding.sivItemMfdMenuImage)
            }

            binding.root.setOnClickListener {
                itemClickListener.onMenuClick(item.groupId)
            }

            binding.root.setOnClickListener {
                itemClickListener.onMapClick(item.groupId)
            }
        }
    }

    inner class ButtonViewHolder(
        private val binding: ItemAddMenuBtnBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.layoutParams.height = dpToPx(context, 117)
            binding.btnAddMenuAddMenu.setOnClickListener {
                onButtonClicked()
            }
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemMenuFolderDetailMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MenuViewHolder(binding)
        } else {
            val binding = ItemAddMenuBtnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ButtonViewHolder(binding)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is MenuViewHolder) {
            holder.bind(items[position])
        }
    }

    override fun getItemCount(): Int = items.size + 1 // 버튼때문에 1 추가

    override fun getItemViewType(position: Int): Int =
        if (position == items.size) VIEW_TYPE_BUTTON else VIEW_TYPE_ITEM
}
