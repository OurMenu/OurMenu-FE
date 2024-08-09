package com.example.ourmenu.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.BaseResponse
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.data.menuFolder.data.MenuFolderData
import com.example.ourmenu.databinding.ItemMenuFolderBinding
import com.example.ourmenu.menu.callback.DiffUtilCallback
import com.example.ourmenu.menu.callback.SwipeItemTouchHelperCallback
import com.example.ourmenu.menu.iteminterface.MenuFolderItemClickListener
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuFolderService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuFolderRVAdapter(
    val items: ArrayList<MenuFolderData>, val context: Context,
    val swipeItemTouchHelperCallback: SwipeItemTouchHelperCallback
) : RecyclerView.Adapter<MenuFolderRVAdapter.ViewHolder>() {

    private lateinit var itemClickListener: MenuFolderItemClickListener
    private val retrofit = RetrofitObject.retrofit
    private val menuFolderService = retrofit.create(MenuFolderService::class.java)

    fun setOnItemClickListener(onItemClickListener: MenuFolderItemClickListener) {
        itemClickListener = onItemClickListener
    }

    inner class ViewHolder(val binding: ItemMenuFolderBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: MenuFolderData, position: Int) {
            // 왼쪽으로 swipe 된 상태, 수정버튼 삭제버튼 누를 수 있음.
            if (item.menuFolderImgUrl != null) {
                Glide.with(context)
                    .load(item.menuFolderImgUrl)
                    .into(binding.ivItemMenuFolderImage)
            } else {
                binding.ivItemMenuFolderImage.setBackgroundDrawable(
                    ContextCompat.getDrawable(context, R.drawable.sample_menu_image)
                )
            }

            Glide.with(context)
                .load(item.menuFolderIcon)
                .into(binding.ivItemMenuFolderIcon)

            binding.tvItemMenuFolderTitle.text = item.menuFolderTitle
            binding.tvItemMenuFolderMenuCount.text = "메뉴 ${item.menuCount}개"


            binding.ivItemMenuFolderImage.setOnClickListener {
                if (!swipeItemTouchHelperCallback.isEditable()) {

                    itemClickListener.onMenuClick(item.menuFolderId)
                }
            }

            binding.clItemMenuFolderEdit.setOnClickListener {
                if (swipeItemTouchHelperCallback.isEditable()) {

                    // TODO 이벤트리스너 작성 ( 인터페이스로 )
                    // TODO API 설정
                    itemClickListener.onEditClick()
                }
            }

            binding.clItemMenuFolderDelete.setOnClickListener {
                if (swipeItemTouchHelperCallback.isEditable()) {
                    itemClickListener.onDeleteClick(item.menuFolderId, position)
                    // TODO API 설정
                }
            }

            binding.ivItemMenuFolderImage.setOnClickListener {
                if (!swipeItemTouchHelperCallback.isEditable()) {

                    itemClickListener.onMenuClick(item.menuFolderId)
                }
            }


        }
    }

    // 드래그 앤 드롭시 교환하는 함수
    fun moveItem(fromPosition: Int, toPosition: Int) {
        val item = items.removeAt(fromPosition)
        items.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)

        // TODO 순서 변경 API
        val menuFolderId = items[fromPosition].menuFolderId
        menuFolderService.patchPriority(
            menuFolderId = menuFolderId, newPriority = toPosition + 1
        )
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("patchPriority", result.toString())
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.d("patchPriority", t.toString())
                }

            })
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuFolderRVAdapter.ViewHolder {
        val binding = ItemMenuFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuFolderRVAdapter.ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}
