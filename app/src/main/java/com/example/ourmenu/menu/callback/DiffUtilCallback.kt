package com.example.ourmenu.menu.callback

import androidx.recyclerview.widget.DiffUtil
import com.example.ourmenu.data.menu.data.MenuData

// 데이터 업데이트용 콜백 클래스
class DiffUtilCallback(
    private val oldList: ArrayList<MenuData>,
    private val newList: ArrayList<MenuData>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.menuId == newItem.menuId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }
}
