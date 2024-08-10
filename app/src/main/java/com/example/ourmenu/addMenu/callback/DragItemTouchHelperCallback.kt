package com.example.ourmenu.addMenu.callback

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.addMenu.adapter.AddMenuImageAdapter


class DragItemTouchHelperCallback(val addMenuImageAdapter: AddMenuImageAdapter) : ItemTouchHelper.Callback() {

    // 이동 방향 결정하기
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        // 설정 안 하고 싶으면 0
        return makeMovementFlags(LEFT or RIGHT, 0)
    }

    // 드래그 일어날 때 동작 (롱터치 후 드래그)
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // 어댑터의 함수 실행
        addMenuImageAdapter.onItemMove(viewHolder,target,viewHolder.adapterPosition,target.adapterPosition)

        return true
    }

    // 스와이프 일어날 때 동작
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 스와와이프 끝까지 하면 해당 데이터 삭제하기 -> 스와이프 후 <삭제> 버튼 눌러야 삭제 되도록 변경
        // recyclerViewAdapter.removeData(viewHolder.layoutPosition)
    }

}
