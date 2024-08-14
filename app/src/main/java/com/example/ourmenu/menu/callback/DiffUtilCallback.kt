package com.example.ourmenu.menu.callback

import androidx.recyclerview.widget.DiffUtil
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.data.menuFolder.data.MenuFolderData

// 데이터 업데이트용 콜백 클래스
class DiffUtilCallback(
    private val oldList: ArrayList<*>,
    private val newList: ArrayList<*>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem is MenuData && newItem is MenuData)
            return oldItem.groupId == newItem.groupId
        else if (oldItem is MenuFolderData && newItem is MenuFolderData)
            return oldItem.menuFolderId == newItem.menuFolderId
        else return false
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }
}
