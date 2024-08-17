package com.example.ourmenu.community.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.community.ArticleMenuData
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.databinding.ItemCommunityPostMenuBinding

class CommunityPostRVAdapter(
    var items: ArrayList<ArticleMenuData>,
    val context: Context,
    val onSaveClick: (MenuData) -> Unit,
    val onDeleteClick: (MenuData) -> Unit
) : RecyclerView.Adapter<CommunityPostRVAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemCommunityPostMenuBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArticleMenuData, position: Int) {
            binding.tvItemCpmMenu.text = item.menuTitle
            if(!item.menuImgUrl.isNullOrBlank()){
                Glide.with(context)
                    .load(item.menuImgUrl)
                    .into(binding.sivItemCpmImage)
            }else {
                //todo 통일된 기본 이미지로 변경하기
                binding.sivItemCpmImage.setImageResource(R.drawable.menu_sample2)
            }
            binding.tvItemCpmNumber.text = (items.indexOf(item)+1).toString()+"/"+items.size.toString()
            binding.ivItemCpmDelete.setOnClickListener {
//                onDeleteClick(item)
                removeItem(position)
            }
            binding.ivItemCpmSave.setOnClickListener {
//                onSaveClick(item)
            }

        }
    }

    fun removeItem(position: Int) {
        // 아이템 삭제
        items.removeAt(position)
        // 어댑터에 변경 사항 알리기
        notifyItemRemoved(position)
        // 선택 사항: 아이템 삭제 후 갱신된 데이터 알림
        notifyItemRangeChanged(position, items.size)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CommunityPostRVAdapter.ViewHolder {
        val binding = ItemCommunityPostMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int = 2000

    override fun onBindViewHolder(
        holder: CommunityPostRVAdapter.ViewHolder,
        position: Int,
    ) {
        val dividePos = position % items.size
        holder.bind(items[dividePos], dividePos)

    }


}
