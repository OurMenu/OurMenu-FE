package com.example.ourmenu.account

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.ourmenu.R

class FindPasswordDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setView(R.layout.find_password_dialog)
            .create()
    }
    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog?
        dialog?.window?.let { window ->
            val layoutParams = window.attributes
            // 다이얼로그의 가로 및 세로 크기 조정
            layoutParams.gravity = Gravity.TOP
            // 다이얼로그 위치 조정 (X, Y 좌표)
            layoutParams.y = 380 // Y 좌표를 조정하여 위치 변경
            window.attributes = layoutParams
        }
    }
}
