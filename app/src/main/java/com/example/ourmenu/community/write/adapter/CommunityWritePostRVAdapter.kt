package com.example.ourmenu.community.write.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.DummyMenuData
import com.example.ourmenu.data.HomeMenuData
import com.example.ourmenu.data.community.ArticleRequestData
import com.example.ourmenu.databinding.ItemAddMenuDefaultBinding
import com.example.ourmenu.databinding.ItemHomeMenuMainBinding

class CommunityWritePostRVAdapter(
    var items: ArrayList<ArticleRequestData>,
    val context: Context,
    val onDefaultClicked: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_DEFAULT = 0
        private const val VIEW_TYPE_IMAGE = 1
    }

    inner class DefaultViewHolder(
        private val binding: ItemAddMenuDefaultBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemAddMenuDefaultContainer.setOnClickListener {
                onDefaultClicked()
            }
        }
    }

    inner class ImageViewHolder(
        private val binding: ItemHomeMenuMainBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArticleRequestData) {
//            binding.sivItemMenuImageMain.setImageResource(item.imageUrl)
            binding.tvItemMenuMain.text = item.menuTitle
            binding.tvItemStoreMain.text = item.placeTitle
            Glide.with(context)
                .load(item.menuImgUrl)
                .into(binding.sivItemMenuImageMain)

            if(item.menuImgUrl == ""){
                binding.sivItemMenuImageMain.setBackgroundResource(R.drawable.menu_sample)

            }

            // 화면 자석 효과
            if (adapterPosition == 0 || adapterPosition == items.size) {
                binding.layoutItemHomeMenuMain.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

                val displayMetrics = context.resources.displayMetrics
                val screenWidth = displayMetrics.widthPixels
                var mLayoutParam: RecyclerView.LayoutParams =
                    binding.layoutItemHomeMenuMain.layoutParams as RecyclerView.LayoutParams
                if (adapterPosition == 0)
                    mLayoutParam.leftMargin = (screenWidth - binding.layoutItemHomeMenuMain.measuredWidthAndState) / 2
                else
                    mLayoutParam.rightMargin = (screenWidth - binding.layoutItemHomeMenuMain.measuredWidthAndState) / 2
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        if (viewType == VIEW_TYPE_DEFAULT) {
            val binding = ItemAddMenuDefaultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DefaultViewHolder(binding)
        } else {
            val binding = ItemHomeMenuMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ImageViewHolder(binding)
        }


    override fun getItemCount(): Int = items.size + 1

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is ImageViewHolder) holder.bind(items[position - 1])
    }

    override fun getItemViewType(position: Int): Int =
        if (position == 0 || items.size == 0) VIEW_TYPE_DEFAULT else VIEW_TYPE_IMAGE

}
