package com.example.ourmenu.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.map.data.MapInfoDetailData
import com.example.ourmenu.data.map.data.MenuImgsUrl
import com.example.ourmenu.data.map.data.MenuTag
import com.example.ourmenu.databinding.ChipCustomBinding
import com.example.ourmenu.databinding.ChipDefaultBinding
import com.example.ourmenu.databinding.ItemMapMenuInfoBinding

class MapBottomSheetRVAdapter(
    var items: ArrayList<MapInfoDetailData>,
    val itemClickListener: (MapInfoDetailData) -> Unit,
) : RecyclerView.Adapter<MapBottomSheetRVAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemMapMenuInfoBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MapInfoDetailData) {
            binding.tvMapBsMenu.text = item.menuTitle
            binding.tvMapBsPrice.text = item.menuPrice.toString()
            binding.tvMapBsPlace.text = item.placeTitle
//            binding.ivMapFolderChipIcon.setImageResource() //TODO: 아이콘 이미지 반영
            binding.tvMapFolderChipText.text = item.menuFolder.menuFolderTitle // TODO: menuFolderCount가 1 이상이면 +n으로 반영

            setMenuImages(item.menuImgsUrl, R.drawable.menu_sample)
            setChips(item.menuTags)
        }

        private fun setMenuImages(
            imgUrls: ArrayList<MenuImgsUrl>,
            defaultImgRes: Int,
        ) {
            val imageViews =
                arrayListOf(
                    binding.sivMapBsImg1,
                    binding.sivMapBsImg2,
                    binding.sivMapBsImg3,
                )

            for (i in imageViews.indices) {
                if (i < imgUrls.size) {
                    Glide
                        .with(binding.root.context)
                        .load(imgUrls[i].menuImgUrl)
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
