package com.example.ourmenu.community

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.community.adapter.CommunityPostRVAdapter
import com.example.ourmenu.community.adapter.CommunitySaveDialogRVAdapter
import com.example.ourmenu.data.community.ArticleMenuData
import com.example.ourmenu.data.community.ArticleRequestData
import com.example.ourmenu.data.community.ArticleResponse
import com.example.ourmenu.data.community.CommunityArticleRequest
import com.example.ourmenu.data.community.CommunityResponseData
import com.example.ourmenu.data.community.StrResponse
import com.example.ourmenu.data.menuFolder.data.MenuFolderData
import com.example.ourmenu.data.menuFolder.response.MenuFolderArrayResponse
import com.example.ourmenu.data.user.UserResponse
import com.example.ourmenu.databinding.CommunityDeleteDialogBinding
import com.example.ourmenu.databinding.CommunityKebabBottomSheetDialogBinding
import com.example.ourmenu.databinding.CommunityReportDialogBinding
import com.example.ourmenu.databinding.CommunitySaveDialogBinding
import com.example.ourmenu.databinding.FragmentCommunityPostBinding
import com.example.ourmenu.retrofit.NetworkModule
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.CommunityService
import com.example.ourmenu.retrofit.service.MenuFolderService
import com.example.ourmenu.retrofit.service.UserService
import com.example.ourmenu.util.Utils.applyBlurEffect
import com.example.ourmenu.util.Utils.dpToPx
import com.example.ourmenu.util.Utils.removeBlurEffect
import com.example.ourmenu.util.Utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CommunityPostFragment(
    var isMine: Boolean,
) : Fragment() {
    lateinit var binding: FragmentCommunityPostBinding
    var menuItems: ArrayList<ArticleMenuData> = arrayListOf()
    private var menuFolderItems = ArrayList<MenuFolderData>()
    lateinit var rvAdapter: CommunitySaveDialogRVAdapter
    lateinit var menuFolderList: ArrayList<String>
    var userEmail = ""
    private val retrofit = RetrofitObject.retrofit
    private val menuFolderService = retrofit.create(MenuFolderService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCommunityPostBinding.inflate(layoutInflater)

        initPost() {}
        return binding.root
    }

    private fun getUserInfo() {

        NetworkModule.initialize(requireContext())
        val service = RetrofitObject.retrofit.create(UserService::class.java)
        val call = service.getUser()

        call.enqueue(object : retrofit2.Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    userEmail = response.body()?.response!!.email
                } else {
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
            }
        })

    }

    fun getArticleDetail(id: Int) {
        NetworkModule.initialize(requireContext())
        val service = RetrofitObject.retrofit.create(CommunityService::class.java)
        val call = service.getCommunityArticle(id)

        call.enqueue(
            object : retrofit2.Callback<ArticleResponse> {
                override fun onResponse(
                    call: Call<ArticleResponse>,
                    response: Response<ArticleResponse>,
                ) {
                    if (response.isSuccessful) {
                        val article = response.body()?.response
                        if (!article?.userImgUrl.isNullOrBlank()) {
                            Glide
                                .with(requireContext())
                                .load(article?.userImgUrl)
                                .into(binding.sivCommunityPostProfileImage)
                        }
                        isMine = if (response.body()?.response?.userEmail == userEmail) {
                            true
                        } else {
                            false
                        }

                        binding.etCommunityPostTitle.text =
                            Editable.Factory.getInstance().newEditable(article?.articleTitle)

                        // UTC 시간 -> KST 변환 및 포맷팅
                        val createdByUtc = LocalDateTime.parse(article?.createdBy, DateTimeFormatter.ISO_DATE_TIME)
                        val createdByKst =
                            createdByUtc
                                .atZone(
                                    ZoneId.of("UTC"),
                                ).withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                                .toLocalDateTime()
                        val formattedDate = createdByKst.format(DateTimeFormatter.ofPattern("yyyy. M. d. HH:mm"))

                        binding.tvCommunityPostTime.text = formattedDate

                        binding.tvCommunityPostName.text = article?.userNickname
                        binding.etCommunityPostContent.text =
                            Editable.Factory.getInstance().newEditable(article?.articleContent)

                        for (i in article?.articleMenus!!) {
                            menuItems.add(i)
                        }

                        binding.rvCommunityPost.adapter?.notifyDataSetChanged()

                        initRV()
                        initListener()
                    }
                }

                override fun onFailure(
                    call: Call<ArticleResponse>,
                    t: Throwable,
                ) {
                    TODO("Not yet implemented")
                }
            },
        )
    }

    fun initPost(callback: () -> Unit) {
        Thread {
            val postData = arguments?.get("articleData") as CommunityResponseData
            getUserInfo()
            getArticleDetail(postData?.articleId!!)
            callback()
        }.start()
    }

    private fun initRV() {
        val adapter =
            CommunityPostRVAdapter(
                menuItems,
                requireContext(),
                onDeleteClick = {
                    deleteArticle()
                },
                onSaveClick = {
                    // todo 게시글 추가 api
                    postCommnunityArticle()
                    showSaveDialog()
                },
            )
        binding.rvCommunityPost.adapter = adapter

        // 아이템의 width를 구하기 위해 viewTreeObserver 사용
        // 시작 위치 조정용
        binding.rvCommunityPost.viewTreeObserver.addOnGlobalLayoutListener(
            object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.rvCommunityPost.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val width =
                        binding.rvCommunityPost.layoutManager
                            ?.getChildAt(0)
                            ?.width
                    val screenWidth = context?.resources?.displayMetrics?.widthPixels
                    val offset = (screenWidth!! - width!!) / 2

                    (binding.rvCommunityPost.layoutManager as LinearLayoutManager)
                        .scrollToPositionWithOffset(
                            (1000 / menuItems.size) * menuItems.size,
                            offset,
                        )
                }
            },
        )

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCommunityPost)
    }

    private fun initListener() {
        binding.ivCommunityPostBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.ivCommunityPostKebab.setOnClickListener {
            showKebabDialog()
        }

        binding.btnCommunityPostOk.setOnClickListener {
            putArticle()
            // 게시글 수정
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

        getMenuFolders(dialogBinding)

        saveDialog.setCanceledOnTouchOutside(false)

        saveDialog.setOnShowListener {
            val window = saveDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val params = window?.attributes
            params?.width = dpToPx(requireContext(), 288)
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = params
        }

        dialogBinding.etCsdSearchField.setOnClickListener {
            dialogBinding.clCommunitySaveContainer.visibility = View.VISIBLE
        }

        rvAdapter =
            CommunitySaveDialogRVAdapter(ArrayList()) { selectedItems ->
                dialogBinding.btnCsdEtConfirm.isEnabled = selectedItems.isNotEmpty()
            }

        dialogBinding.rvCommunitySave.adapter = rvAdapter
        dialogBinding.rvCommunitySave.layoutManager = LinearLayoutManager(context)

        // 확인 버튼을 클릭하면 dropdown 숨기고 선택된 항목들을 EditText에 설정
        dialogBinding.btnCsdEtConfirm.setOnClickListener {
            dialogBinding.clCommunitySaveContainer.visibility = View.GONE
            val selectedTitles = rvAdapter.getSelectedItems().map { it.menuFolderTitle }.joinToString(", ")
            dialogBinding.etCsdSearchField.setText(selectedTitles)
        }

        // dialog 사라지면 블러효과도 같이 사라짐
        saveDialog.setOnDismissListener {
            rootView?.let { removeBlurEffect(it) }
        }

        saveDialog.show()
    }

    private fun getMenuFolders(dialogBinding: CommunitySaveDialogBinding) {
        menuFolderService.getMenuFolders().enqueue(
            object : Callback<MenuFolderArrayResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<MenuFolderArrayResponse>,
                    response: Response<MenuFolderArrayResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val menuFolders = result?.response
                        menuFolders?.let {
                            menuFolderItems = it.menuFolders

                            // 어댑터에 데이터 설정 및 갱신
                            rvAdapter =
                                CommunitySaveDialogRVAdapter(menuFolderItems) { selectedItems ->
                                    dialogBinding.btnCsdEtConfirm.isEnabled = selectedItems.isNotEmpty()
                                }
                            dialogBinding.rvCommunitySave.adapter = rvAdapter
                            rvAdapter.notifyDataSetChanged()
                        }
                    } else {
                        Log.d("err", response.errorBody().toString())
                    }
                }

                override fun onFailure(
                    call: Call<MenuFolderArrayResponse>,
                    t: Throwable,
                ) {
                    Log.d("menuFolders", t.message.toString())
                }
            },
        )
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

            // 취소
            dialogBinding.ivCrdClose.setOnClickListener {
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
            deleteDialog.dismiss()
            showToast(requireContext(), R.drawable.ic_complete, "게시글이 삭제되었어요!")
            deleteArticle()
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

    fun postCommnunityArticle() {
        NetworkModule.initialize(requireContext())
        val service = RetrofitObject.retrofit.create(CommunityService::class.java)
        val call =
            service.postCommunityArticle(
                CommunityArticleRequest(
                    binding.etCommunityPostTitle.text.toString(),
                    binding.etCommunityPostContent.text.toString(),
                    arrayListOf(),
                ),
            )
        call.enqueue(
            object : retrofit2.Callback<ArticleResponse> {
                override fun onResponse(
                    call: Call<ArticleResponse>,
                    response: Response<ArticleResponse>,
                ) {
                    TODO("Not yet implemented")
                }

                override fun onFailure(
                    call: Call<ArticleResponse>,
                    t: Throwable,
                ) {
                    TODO("Not yet implemented")
                }
            },
        )
    }

    fun deleteArticle() {
        NetworkModule.initialize(requireContext())
        val service = RetrofitObject.retrofit.create(CommunityService::class.java)
        val call = service.deleteCommunityArticle(articleId = arguments?.getInt("articleId")!!)

        call.enqueue(
            object : retrofit2.Callback<StrResponse> {
                override fun onResponse(
                    call: Call<StrResponse>,
                    response: Response<StrResponse>,
                ) {
                    if (response.isSuccessful) {
                        requireActivity().finish()
                    }
                }

                override fun onFailure(
                    call: Call<StrResponse>,
                    t: Throwable,
                ) {
                    TODO("Not yet implemented")
                }
            },
        )
    }

    fun putArticle() {
        NetworkModule.initialize(requireContext())
        val service = RetrofitObject.retrofit.create(CommunityService::class.java)

        val call =
            service.putCommunityArticle(
                arguments?.getInt("articleId")!!,
                CommunityArticleRequest(
                    binding.etCommunityPostTitle.text.toString(),
                    binding.etCommunityPostContent.text.toString(),
                    menuItems
                        .map {
                            ArticleRequestData(
                                it.placeTitle,
                                it.menuTitle,
                                it.menuPrice,
                                it.menuImgUrl,
                                it.menuAddress,
                                "",
                                "",
                                "",
                                0,
                                0,
                            )
                        }.toCollection(ArrayList()),
                ),
            )


        call.enqueue(
            object : retrofit2.Callback<ArticleResponse> {
                override fun onResponse(
                    call: Call<ArticleResponse>,
                    response: Response<ArticleResponse>,
                ) {
                    if (response.isSuccessful) {
                        showToast(requireContext(), R.drawable.ic_complete, "게시글이 수정되었어요!")
                    }
                }

                override fun onFailure(
                    call: Call<ArticleResponse>,
                    t: Throwable,
                ) {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}
