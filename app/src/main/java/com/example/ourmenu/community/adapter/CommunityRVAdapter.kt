package com.example.ourmenu.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.data.PostData
import com.example.ourmenu.databinding.ItemPostBinding

class CommunityRVAdapter(
    var items: ArrayList<PostData>,
    val itemClickListener: (PostData) -> Unit,
) : RecyclerView.Adapter<CommunityRVAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemPostBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostData) {
            binding.tvItemPostTitle.text = item.title
            binding.tvItemPostContent.text = item.content
            binding.sivItemPostProfile.setImageResource(item.profileImg)
            binding.tvItemPostUsername.text = item.username
            binding.tvItemPostTime.text = item.time
            binding.tvItemPostViewCount.text = item.viewCount.toString()
            binding.sivItemPostThumbnail.setImageResource(item.thumbnail)
            binding.tvItemPostCount.text = item.menuCount.toString()

            binding.root.setOnClickListener { itemClickListener(item) }
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
