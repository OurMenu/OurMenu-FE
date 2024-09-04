package com.example.ourmenu.addMenu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.data.AddMenuImageData
import com.example.ourmenu.databinding.ItemAddMenuNameMenuImageBinding

class AddMenuImageAdapter(val items: ArrayList<AddMenuImageData>) :
    RecyclerView.Adapter<AddMenuImageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAddMenuNameMenuImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun defaultBind(item: AddMenuImageData) {

            if (item.imageUri != null) {
                binding.sivAddMenuImage.setImageURI(item.imageUri)
            }
            binding.sivAddMenuImage.clipToOutline = true
            binding.flAddMenuFirst.visibility = View.INVISIBLE
            binding.ivAddMenuItemDelete.setOnClickListener {

                imageListener.onImageClick(item)
            }
        }

        fun firstBind(item: AddMenuImageData) {

            if (item.imageUri != null) {
                binding.sivAddMenuImage.setImageURI(item.imageUri)
            }
            binding.sivAddMenuImage.clipToOutline = true
            binding.ivAddMenuItemDelete.setOnClickListener {
                imageListener.onImageClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val defaultBinding = ItemAddMenuNameMenuImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(defaultBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.firstBind(items[position])
        } else {
            holder.defaultBind(items[position])
        }
    }

    override fun getItemCount(): Int = items.size


    public interface OnImageClickListener : View.OnClickListener {
        public fun onImageClick(addMenuImageData: AddMenuImageData)
    }

    lateinit var imageListener: OnImageClickListener

    public fun setOnProfileClickListener(listener: OnImageClickListener) {
        this.imageListener = listener
    }

    // 드래그 앤 드롭시 교환하는 함수
    fun onItemMove(fromHolder:RecyclerView.ViewHolder,toHolder: RecyclerView.ViewHolder, from: Int, to: Int) {
        val item: AddMenuImageData = items[from]
        items.removeAt(from)
        items.add(to, item)
        notifyItemMoved(from, to)
        fromHolder as ViewHolder
        toHolder as ViewHolder
        if(from==0){
            fromHolder.binding.flAddMenuFirst.visibility = View.INVISIBLE
            toHolder.binding.flAddMenuFirst.visibility = View.VISIBLE
        }else if(to == 0){
            fromHolder.binding.flAddMenuFirst.visibility = View.VISIBLE
            toHolder.binding.flAddMenuFirst.visibility = View.INVISIBLE
        }
    }
}
