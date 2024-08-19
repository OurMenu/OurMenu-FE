package com.example.ourmenu.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.menu.data.MenuImgUrl
import com.example.ourmenu.data.menu.data.MenuPlaceDetailData
import com.example.ourmenu.data.menu.data.MenuTag
import com.example.ourmenu.databinding.ChipCustomBinding
import com.example.ourmenu.databinding.ChipDefaultBinding
import com.example.ourmenu.databinding.ItemMapMenuInfoBinding
import com.example.ourmenu.util.FolderIconUtil.indexToFolderResourceId
import com.example.ourmenu.util.Utils.toWon

class MapBottomSheetRVAdapter(
    var items: ArrayList<MenuPlaceDetailData>,
    val itemClickListener: (MenuPlaceDetailData) -> Unit,
) : RecyclerView.Adapter<MapBottomSheetRVAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemMapMenuInfoBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuPlaceDetailData) {
            binding.tvMapBsMenu.text = item.menuTitle
            binding.tvMapBsPrice.text = toWon(item.menuPrice)
            binding.tvMapBsPlace.text = item.placeTitle
            binding.ivMapFolderChipIcon.setImageResource(
                indexToFolderResourceId(item.menuFolder.menuFolderIcon),
            )

            val folderText =
                if (item.menuFolder.menuFolderCount == 0) {
                    item.menuFolder.menuFolderTitle
                } else {
                    "${item.menuFolder.menuFolderTitle} +${item.menuFolder.menuFolderCount}"
                }
            binding.tvMapFolderChipText.text = folderText

            setMenuImages(item.menuImgUrl, R.drawable.default_image)
            setChips(item.menuTags)

            binding.root.setOnClickListener {
                itemClickListener(item)
            }
        }

        private fun setMenuImages(
            imgUrls: ArrayList<MenuImgUrl>,
            defaultImgRes: Int,
        ) {
            val imageViews =
                arrayListOf(
                    binding.sivMapBsImg1,
                    binding.sivMapBsImg2,
                    binding.sivMapBsImg3,
                )

            // imgUrls가 null일 경우 빈 리스트로 초기화
            val urls = imgUrls ?: arrayListOf()

            for (i in imageViews.indices) {
                if (i < urls.size) {
                    Glide
                        .with(binding.root.context)
                        .load(urls[i].menuImgUrl)
                        .into(imageViews[i])
                } else {
                    Glide
                        .with(binding.root.context)
                        .load(defaultImgRes)
                        .into(imageViews[i])
                }
            }
        }

        private fun setChips(tags: ArrayList<MenuTag>) {
            // ChipGroup의 기존 Chip들 제거
            binding.cgMapBsChipGroup.removeAllViews()

            // ChipGroup에 Chip 추가
            for (tag in tags) {
                val inflater = LayoutInflater.from(binding.root.context)
                val customTagBinding = ChipCustomBinding.inflate(inflater, binding.cgMapBsChipGroup, false)
                val defaultTagBinding = ChipDefaultBinding.inflate(inflater, binding.cgMapBsChipGroup, false)

                if (tag.custom) {
                    customTagBinding.tvTagDefaultTag.text = tag.tagTitle
                    binding.cgMapBsChipGroup.addView(customTagBinding.root)
                } else {
                    defaultTagBinding.tvTagDefaultTag.text = tag.tagTitle
                    binding.cgMapBsChipGroup.addView(defaultTagBinding.root)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemMapMenuInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
