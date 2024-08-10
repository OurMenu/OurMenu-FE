package com.example.ourmenu.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.ourmenu.R
import com.example.ourmenu.databinding.CommunityFilterSpinnerItemBinding
import com.example.ourmenu.databinding.MenuFolderDetailSpinnerDefaultBinding
import com.example.ourmenu.util.Utils.viewGone


class MenuFolderDetailFilterSpinnerAdapter<T>(
    private val context: Context,
    private val items: List<String>,
) :
    ArrayAdapter<String>(context, R.layout.spinner_default_background, items) {
    var selectedPos = 0

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 스피너의 기본 선택된 항목을 위한 뷰
        val binding = MenuFolderDetailSpinnerDefaultBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        binding.itemMfdspDropdownText.text = this.getItem(position)

        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 스피너의 드롭다운 목록을 위한 뷰
        val binding = CommunityFilterSpinnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // 최신순
        binding.itemCfsiText.text = items[position]

        if (selectedPos == position) {
            binding.itemCfsiText.setTextColor(
                ContextCompat.getColor(context, R.color.Primary_500_main)
            )
        } else {
            binding.itemCfsiText.setTextColor(
                ContextCompat.getColor(context, R.color.Neutral_500)
            )
        }
        if(position == 2){
            binding.itemCfsiLine.viewGone()

        }

        binding.root.setBackgroundColor(Color.TRANSPARENT)


        return binding.root
    }


}

