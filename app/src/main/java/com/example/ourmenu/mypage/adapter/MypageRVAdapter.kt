package com.example.ourmenu.mypage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.community.CommunityResponseData
import com.example.ourmenu.databinding.ItemPostBinding
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MypageRVAdapter(
    var items: ArrayList<CommunityResponseData>,
    val context: Context,
    val itemClickListener: (CommunityResponseData) -> Unit,
) : RecyclerView.Adapter<MypageRVAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemPostBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommunityResponseData) {
            if (!item.userImgUrl.isNullOrBlank()) {
                Glide
                    .with(context)
                    .load(item.userImgUrl)
                    .into(binding.sivItemPostProfile)
            }
            if (!item.articleThumbnail.isNullOrBlank()) {
                Glide
                    .with(context)
                    .load(item.articleThumbnail)
                    .into(binding.sivItemPostThumbnail)
            } else {
                binding.sivItemPostThumbnail.setImageResource(R.drawable.default_image)
            }
            binding.tvItemPostTitle.text = item.articleTitle
            binding.tvItemPostContent.text = item.articleContent
            binding.tvItemPostUsername.text = item.userNickname

            // createdBy를 UTC에서 KST로 변환하고 n분전, n시간전, n일전 계산
            val createdByUtc = LocalDateTime.parse(item.createdBy, DateTimeFormatter.ISO_DATE_TIME)
            val createdByKst =
                ZonedDateTime
                    .of(createdByUtc, ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime()

            val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
            val minutesDiff = ChronoUnit.MINUTES.between(createdByKst, now)
            val timeAgo =
                when {
                    minutesDiff < 1 -> "Just now"
                    minutesDiff < 60 -> "${minutesDiff}m ago"
                    minutesDiff < 1440 -> "${minutesDiff / 60}h ago"
                    else -> "${minutesDiff / 1440}day ago"
                }
            binding.tvItemPostTime.text = timeAgo

            binding.tvItemPostViewCount.text = item.articleViews.toString()
            binding.tvItemPostCount.text = item.menusCount.toString()
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
