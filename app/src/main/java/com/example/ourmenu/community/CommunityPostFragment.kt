package com.example.ourmenu.community

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.R
import com.example.ourmenu.community.adapter.CommunityPostRVAdapter
import com.example.ourmenu.community.adapter.CommunitySaveDialogRVAdapter
import com.example.ourmenu.data.HomeMenuData
import com.example.ourmenu.data.PostData
import com.example.ourmenu.databinding.CommunityDeleteDialogBinding
import com.example.ourmenu.databinding.CommunityKebabBottomSheetDialogBinding
import com.example.ourmenu.databinding.CommunityReportDialogBinding
import com.example.ourmenu.databinding.CommunitySaveDialogBinding
import com.example.ourmenu.databinding.FragmentCommunityPostBinding
import com.example.ourmenu.util.Utils.applyBlurEffect
import com.example.ourmenu.util.Utils.dpToPx
import com.example.ourmenu.util.Utils.removeBlurEffect
import com.example.ourmenu.util.Utils.showToast
import com.example.ourmenu.util.Utils.viewGone
import com.example.ourmenu.util.Utils.viewVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.max

class CommunityPostFragment(val isMine: Boolean) : Fragment() {

    lateinit var binding: FragmentCommunityPostBinding
    lateinit var dummyItems: ArrayList<HomeMenuData>
    lateinit var menuFolderList: ArrayList<String> // TODO 나중에 데이터로 바꾸기


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCommunityPostBinding.inflate(layoutInflater)


        initDummy()
        initBundle()
        initListener()
        initRV()


        return binding.root
    }

    private fun initDummy() {
        dummyItems = ArrayList<HomeMenuData>()
        for (i in 1..6) {
            dummyItems.add(
                HomeMenuData(
                    "제목",
                    "화산라멘",
                    "화산점",
                    R.drawable.menu_sample
                ),
            )
        }
        menuFolderList = arrayListOf("메뉴판1", "메뉴판2", "메뉴판3", "판4", "판5", "menu")
    }

    private fun initRV() {
        val adapter = CommunityPostRVAdapter(
            dummyItems,
            requireContext(),
            onDeleteClick = {
                // TODO 삭제 API
            },
            onSaveClick = {
                // TODO 저장 API
                showSaveDialog()

            }
        )
        binding.rvCommunityPost.adapter = adapter

        // 아이템의 width를 구하기 위해 viewTreeObserver 사용
        // 시작 위치 조정용
        binding.rvCommunityPost.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.rvCommunityPost.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val width = binding.rvCommunityPost.layoutManager?.getChildAt(0)?.width
                val screenWidth = context?.resources?.displayMetrics?.widthPixels
                val offset = (screenWidth!! - width!!) / 2

                (binding.rvCommunityPost.layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(
                        998,
                        offset
                    )
            }

        })

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCommunityPost)

    }


    private fun initBundle() {
        val postData = arguments?.getSerializable("postData") as PostData
        postData.let {
            binding.sivCommunityPostProfileImage.setImageResource(it.profileImg)
            binding.etCommunityPostTitle.hint = it.title
            binding.tvCommunityPostName.text = it.username
            binding.etCommunityPostContent.hint = it.content
        }
    }

    private fun initListener() {
        binding.ivCommunityPostBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.ivCommunityPostKebab.setOnClickListener {
            showKebabDialog()
        }

        binding.btnCommunityPostOk.setOnClickListener {
            showToast(requireContext(), R.drawable.ic_complete, "게시글이 수정되었어요!")
            // TODO 게시글 수정하기
            setEdit(false)
        }
    }


    // 게시글 수정하기 시 화면 세팅
    private fun setEdit(isEdit: Boolean) {
        if (isEdit) {
            binding.clCommunityPostProfile.visibility = View.GONE
            binding.btnCommunityPostOk.visibility = View.VISIBLE
            binding.ivCommunityPostKebab.visibility = View.GONE
        } else {
            binding.clCommunityPostProfile.visibility = View.VISIBLE
            binding.btnCommunityPostOk.visibility = View.GONE
            binding.ivCommunityPostKebab.visibility = View.VISIBLE
        }
    }

    // 저장하기
    @SuppressLint("ClickableViewAccessibility")
    private fun showSaveDialog() {
        val rootView = (activity?.window?.decorView as? ViewGroup)?.getChildAt(0) as? ViewGroup
        // 블러 효과 추가
        rootView?.let { applyBlurEffect(it) }

        val dialogBinding = CommunitySaveDialogBinding.inflate(LayoutInflater.from(context))
        val saveDialog =
            android.app.AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        saveDialog.setCanceledOnTouchOutside(false)

        saveDialog.setOnShowListener {

            val window = saveDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val params = window?.attributes
            params?.width = dpToPx(requireContext(), 288)
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = params

            dialogBinding.rvCommunitySave.adapter = CommunitySaveDialogRVAdapter(requireContext(), menuFolderList)
        }

        // dialog 사라지면 블러효과도 같이 사라짐
        saveDialog.setOnDismissListener {
            rootView?.let { removeBlurEffect(it) }
        }

        dialogBinding.btnCsdEtConfirm.setOnClickListener {
            searchMenuFolder(dialogBinding.etCsdSearchField.text.toString(), dialogBinding)
            dialogBinding.clCommunitySaveContainer.visibility = View.VISIBLE
        }

        with(dialogBinding.etCsdSearchField) {

            // 포커스 감지
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    dialogBinding.clCommunitySaveContainer.viewVisible()
                } else {
//                    dialogBinding.clCommunitySaveContainer.viewGone()
                }

            }

            setOnClickListener {

            }

            // 엔터키 클릭
            setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
                ) {
                    searchMenuFolder(dialogBinding.etCsdSearchField.text.toString(), dialogBinding)
                    true
                } else {
                    false
                }
            }
        }

        saveDialog.show()
    }

    private fun searchMenuFolder(query: String, dialogBinding: CommunitySaveDialogBinding) {
        val newMenuFolderList = ArrayList(menuFolderList.filter { it.contains(query, ignoreCase = true) })
        if (newMenuFolderList.size < 3) {
            // 메뉴판의 수가 3보다 작으면 그만큼 크기를 작게함
            dialogBinding.clCommunitySaveContainer.layoutParams.height =
                dpToPx(requireContext(), 44 * newMenuFolderList.size)
        } else {
            dialogBinding.clCommunitySaveContainer.layoutParams.height =
                dpToPx(requireContext(), 132)
        }
        dialogBinding.rvCommunitySave.adapter = CommunitySaveDialogRVAdapter(requireContext(), newMenuFolderList)
    }


    // kebab 버튼 클릭
    private fun showKebabDialog() {
        val bottomSheetDialog: BottomSheetDialog
        // 내 글 일때
        if (isMine) {
            val dialogBinding = CommunityKebabBottomSheetDialogBinding.inflate(LayoutInflater.from(context))
            bottomSheetDialog = BottomSheetDialog(requireContext())
            bottomSheetDialog.setContentView(dialogBinding.root)

            // 수정하기
            dialogBinding.btnCkbsgEdit.setOnClickListener {
                bottomSheetDialog.dismiss()
                setEdit(true)
            }

            // 삭제하기
            dialogBinding.btnCkbsgDelete.setOnClickListener {
                bottomSheetDialog.dismiss()
                showDeleteDialog()
            }

            // 취소
            dialogBinding.btnCkbsgCancel.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            // 흐린 배경 제거
            bottomSheetDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

            bottomSheetDialog.show()
        } else {
            // 남의 글 일때
            val dialogBinding = CommunityReportDialogBinding.inflate(LayoutInflater.from(context))
            bottomSheetDialog = BottomSheetDialog(requireContext())
            bottomSheetDialog.setContentView(dialogBinding.root)

            // 신고하기
            dialogBinding.btnCrdReport.setOnClickListener {
                bottomSheetDialog.dismiss()
                showReportDialog()
            }

            // 취소
            dialogBinding.btnCrdCancel.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }
        // 흐린 배경 제거
        bottomSheetDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        bottomSheetDialog.show()
    }


    // kebab -> 삭제하기
    private fun showDeleteDialog() {
        val rootView = (activity?.window?.decorView as? ViewGroup)?.getChildAt(0) as? ViewGroup
        // 블러 효과 추가
        rootView?.let { applyBlurEffect(it) }

        val dialogBinding = CommunityDeleteDialogBinding.inflate(LayoutInflater.from(context))
        val deleteDialog =
            android.app.AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        deleteDialog.setOnShowListener {
            val window = deleteDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val params = window?.attributes
            params?.width = dpToPx(requireContext(), 288)
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = params
        }

        // dialog 사라지면 블러효과도 같이 사라짐
        deleteDialog.setOnDismissListener {
            rootView?.let { removeBlurEffect(it) }
        }

        dialogBinding.ivCddClose.setOnClickListener {
            deleteDialog.dismiss()
        }

        dialogBinding.btnCddDelete.setOnClickListener {
            // TODO: 게시글 삭제 API
            deleteDialog.dismiss()
            showToast(requireContext(), R.drawable.ic_complete, "게시글이 삭제되었어요!")
        }

        dialogBinding.btnCddCancel.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }

    private fun showReportDialog() {
        val rootView = (activity?.window?.decorView as? ViewGroup)?.getChildAt(0) as? ViewGroup
        // 블러 효과 추가
        rootView?.let { applyBlurEffect(it) }

        val dialogBinding = CommunityReportDialogBinding.inflate(LayoutInflater.from(context))
        val deleteDialog =
            android.app.AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        deleteDialog.setOnShowListener {
            val window = deleteDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val params = window?.attributes
            params?.width = dpToPx(requireContext(), 288)
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = params
        }

        // dialog 사라지면 블러효과도 같이 사라짐
        deleteDialog.setOnDismissListener {
            rootView?.let { removeBlurEffect(it) }
        }

        dialogBinding.ivCrdClose.setOnClickListener {
            deleteDialog.dismiss()
        }

        dialogBinding.btnCrdReport.setOnClickListener {
            // TODO: 게시글 신고 API
            deleteDialog.dismiss()
            showToast(requireContext(), R.drawable.ic_complete, "게시글이 신고되었어요!")
        }

        dialogBinding.btnCrdCancel.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }

}
