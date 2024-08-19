package com.example.ourmenu.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.onboarding.data.OnboardingMenuData
import com.example.ourmenu.databinding.ItemHomeMenuMainBinding
import com.example.ourmenu.home.iteminterface.HomeItemClickListener
import com.example.ourmenu.util.Utils.showToast

class HomeMenuMainRVAdapter(
    val items: ArrayList<OnboardingMenuData>,
    val context: Context,
) : RecyclerView.Adapter<HomeMenuMainRVAdapter.ViewHolder>() {
    private lateinit var itemClickListener: HomeItemClickListener

    fun setOnItemClickListener(onItemClickListener: HomeItemClickListener) {
        itemClickListener = onItemClickListener
    }

    inner class ViewHolder(
        private val binding: ItemHomeMenuMainBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OnboardingMenuData) {
            // 아이템 클릭 리스너 추가
            binding.root.setOnClickListener {
                if (item.userOwned) {
                    itemClickListener.onItemClick(item)
                } else {
                    showToast(context, R.drawable.ic_error, "내 메뉴판에 있는 메뉴만 볼 수 있어요!")
                }
            }

            binding.tvItemMenuMain.text = item.menuTitle
            binding.tvItemStoreMain.text = item.placeName
//
            if (item.menuImgUrl != "null" && item.menuImgUrl != "" && item.menuImgUrl != null) {
                Log.d("glide", item.menuImgUrl)
                Glide
                    .with(context)
                    .load(item.menuImgUrl)
                    .into(binding.sivItemMenuImageMain)
                binding.sivItemMenuImageMain.layoutParams.width =
                    (context.resources.displayMetrics.widthPixels * 304 / 360).toInt()
            } else {
                binding.sivItemMenuImageMain.setImageResource(R.drawable.default_image)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemHomeMenuMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = 2000

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        if (items.size == 0) return
        holder.bind(items[position % items.size])
    }
}
