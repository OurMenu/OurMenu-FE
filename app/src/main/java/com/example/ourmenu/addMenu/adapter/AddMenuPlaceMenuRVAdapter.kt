package com.example.ourmenu.addMenu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.R
import com.example.ourmenu.data.PlaceMenuData
import com.example.ourmenu.databinding.ItemAddMenuBtnBinding
import com.example.ourmenu.databinding.ItemAddMenuPlaceMenuBinding
import com.example.ourmenu.util.Utils.showToast

class AddMenuPlaceMenuRVAdapter(
    var items: ArrayList<PlaceMenuData>,
    val onItemSelected: (Int?) -> Unit, // toggle되도록 nullable 추가
    val onButtonClicked: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition: Int? = null // toggle되도록 nullable 추가

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_BUTTON = 1
    }

    inner class MenuViewHolder(
        private val binding: ItemAddMenuPlaceMenuBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ivAddMenuBsAddBtn.setOnClickListener {
                val previousPosition = selectedPosition
                if (adapterPosition == selectedPosition) {
                    selectedPosition = null // 다시 선택하면 선택 취소되도록
                } else {
                    selectedPosition = adapterPosition

                    // 다른 아이템이 선택되었을 때 토스트 메시지 표시
                    previousPosition?.let {
                        showToast(
                            binding.root.context,
                            R.drawable.ic_error,
                            "한 번에 한 가지 메뉴만 등록 가능해요.",
                        )
                    }
                }

                previousPosition?.let { notifyItemChanged(it) }
                selectedPosition?.let { notifyItemChanged(it) }

                onItemSelected(selectedPosition) // 아이템 선택 콜백 호출
            }
        }

        fun bind(item: PlaceMenuData) {
            binding.tvAddMenuBsMenu.text = item.menuName
            binding.tvAddMenuBsPrice.text = item.price

            if (adapterPosition == selectedPosition) {
                binding.ivAddMenuBsAddBtn.setImageResource(R.drawable.ic_add_menu_checked)
            } else {
                binding.ivAddMenuBsAddBtn.setImageResource(R.drawable.ic_add_menu_stroked)
            }
        }
    }

    inner class ButtonViewHolder(
        private val binding: ItemAddMenuBtnBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnAddMenuAddMenu.setOnClickListener {
                onButtonClicked()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemAddMenuPlaceMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MenuViewHolder(binding)
        } else {
            val binding = ItemAddMenuBtnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ButtonViewHolder(binding)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is MenuViewHolder) {
            holder.bind(items[position])
        }
    }

    override fun getItemCount(): Int = items.size + 1 // 버튼때문에 1 추가

    override fun getItemViewType(position: Int): Int = if (position == items.size) VIEW_TYPE_BUTTON else VIEW_TYPE_ITEM
}
