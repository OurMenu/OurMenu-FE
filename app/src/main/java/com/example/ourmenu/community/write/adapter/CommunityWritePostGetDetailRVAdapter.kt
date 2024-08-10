package com.example.ourmenu.community.write.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.R
import com.example.ourmenu.data.DummyMenuData
import com.example.ourmenu.databinding.ItemMenuFolderDetailMenuBinding

class CommunityWritePostGetDetailRVAdapter(private val items: ArrayList<DummyMenuData>) :
    RecyclerView.Adapter<CommunityWritePostGetDetailRVAdapter.ViewHolder>() {

    var checkedItems = ArrayList<DummyMenuData>()

    private lateinit var itemClickListener: () -> Unit

    fun setOnItemClickListener(onItemClickListener: () -> Unit) {
        itemClickListener = onItemClickListener
    }

    inner class ViewHolder(val binding: ItemMenuFolderDetailMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isChecked = false

        fun bind(item: DummyMenuData) {
            binding.sivItemMfdMenuImage
            binding.tvItemMfdMenuName.text = item.menu
            binding.tvItemMfdMenuPlace.text = item.store
            binding.tvItemMfdMenuPlace.text = item.address
            binding.ivItemMfdExtraButton.setImageResource(R.drawable.ic_add_menu_stroked)


            // 클릭할 때마다 바뀌기
            binding.ivItemMfdExtraButton.setOnClickListener {
                if (this.isChecked) {
                    this.isChecked = false
                    binding.ivItemMfdExtraButton.setImageResource(R.drawable.ic_add_menu_stroked)

                    // check 아이템에서 삭제
                    checkedItems.remove(item)
                } else {
                    this.isChecked = true
                    binding.ivItemMfdExtraButton.setImageResource(R.drawable.ic_add_menu_checked)

                    // check 아이템에 추가
                    checkedItems.add(item)
                }
                itemClickListener()
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityWritePostGetDetailRVAdapter.ViewHolder {
        val binding = ItemMenuFolderDetailMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityWritePostGetDetailRVAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

}
