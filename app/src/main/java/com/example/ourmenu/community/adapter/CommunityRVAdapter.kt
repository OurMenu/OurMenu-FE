package com.example.ourmenu.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.data.PostData
import com.example.ourmenu.data.community.CommunityResponseData
import com.example.ourmenu.databinding.ItemPostBinding

class CommunityRVAdapter(
    var items: ArrayList<CommunityResponseData>,
    val itemClickListener: (Int) -> Unit,
) : RecyclerView.Adapter<CommunityRVAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemPostBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommunityResponseData) {
            binding.tvItemPostTitle.text = item.articleTitle
            binding.tvItemPostContent.text = item.articleContent
//            binding.sivItemPostProfile.setImageResource(item.profileImg)
            binding.tvItemPostUsername.text = item.userNickname
            binding.tvItemPostTime.text = item.createdBy
            binding.tvItemPostViewCount.text = item.articleViews.toString()
//            binding.sivItemPostThumbnail.setImageResource(item.thumbnail)
            binding.tvItemPostCount.text = item.menusCount.toString()

            binding.root.setOnClickListener { itemClickListener(item.articleId) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }
}
