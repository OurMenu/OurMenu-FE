package com.example.ourmenu.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.data.map.data.MapSearchData
import com.example.ourmenu.databinding.ItemMapSearchResultBinding

class MapSearchResultRVAdapter(
    var items: ArrayList<MapSearchData>,
    val itemClickListener: (MapSearchData) -> Unit,
) : RecyclerView.Adapter<MapSearchResultRVAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemMapSearchResultBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MapSearchData) {
            binding.tvMsrMenuTitle.text = item.menuTitle
            binding.tvMsrPlaceTitle.text = item.placeTitle
            binding.tvMsrAddress.text = item.placeAddress
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemMapSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    fun updateItemsFromSearch(newItems: ArrayList<MapSearchData>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
