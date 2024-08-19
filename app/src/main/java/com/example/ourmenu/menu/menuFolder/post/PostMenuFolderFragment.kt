package com.example.ourmenu.menu.menuFolder.post

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.data.menuFolder.response.MenuFolderResponse
import com.example.ourmenu.databinding.FragmentPostMenuFolderBinding
import com.example.ourmenu.menu.menuFolder.post.adapter.PostMenuFolderRVAdapter
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuFolderService
import com.example.ourmenu.util.FolderIconUtil.indexToFolderResourceId
import com.example.ourmenu.util.Utils.getTypeOf
import com.example.ourmenu.util.Utils.hideKeyboard
import com.example.ourmenu.util.Utils.isNotNull
import com.example.ourmenu.util.Utils.viewGone
import com.example.ourmenu.util.Utils.viewVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PostMenuFolderFragment : Fragment() {
    lateinit var binding: FragmentPostMenuFolderBinding
    var menuItems = ArrayList<MenuData>()
    var menuIdsList = ArrayList<Int>()
    private var isTitleFilled = false
    private val retrofit = RetrofitObject.retrofit
    private val service = retrofit.create(MenuFolderService::class.java)

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var iconGroupBS: ConstraintLayout
    private lateinit var checkedIcon: ImageView
    private var checkedIconIndex = 31
    private var postIconIndex = 31

    private var imageUri: Uri? = null

    // 갤러리 open
    private val galleryPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {

                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*",
                )
                pickImageLauncher.launch(intent)
            } else {
            }
        }

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("res", result.resultCode.toString())
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data

                data?.data?.let {
                    imageUri = it
                    Log.d("imguri", imageUri.toString())
                    binding.ivPmfImage.setImageURI(imageUri)
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPostMenuFolderBinding.inflate(layoutInflater)

        imageUri?.let {
            binding.ivPmfImage.setImageURI(imageUri)
        }
        if (isTitleFilled) binding.tvPmfHint.viewGone()

        initBottomSheet()
        initMenuItems()
        initListener()
        initRV()

        return binding.root
    }

    private fun initBottomSheet() {
        iconGroupBS = binding.bsPmfFolderIconGroup.pmfgBottomSheet

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bsPmfFolderIconGroup.pmfgBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val screenHeight = requireContext().resources.displayMetrics.heightPixels
        iconGroupBS.layoutParams.height = (screenHeight * 444) / 800

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.btnPmfOk.viewVisible()
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.btnPmfOk.viewVisible()
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.btnPmfOk.viewGone()
                    }

                    else -> {
                        return
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })


        initBottomSheetChips()
        initBottomSheetListener()
    }

    private fun initBottomSheetListener() {
        binding.bsPmfFolderIconGroup.btnSpmfciCancel.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.bsPmfFolderIconGroup.btnSpmfciApply.setOnClickListener {
            postIconIndex = checkedIconIndex
            binding.ivPfmIcon.setImageResource(
                indexToFolderResourceId(postIconIndex)
            )
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }
    }

    private fun initBottomSheetChips() {
        checkedIcon = binding.bsPmfFolderIconGroup.bsPmfciChipGroup.getChildAt(31) as ImageView
        checkedIconIndex = 31

        val iconGroup = binding.bsPmfFolderIconGroup.bsPmfciChipGroup

        for (i in 0 until iconGroup.childCount) {
            val icon = iconGroup.getChildAt(i) as ImageView
            icon.setOnClickListener {
                // 같은거 클릭
                if (checkedIcon == icon) {
                    return@setOnClickListener
                } else {
                    icon.setBackgroundResource(R.drawable.chip_bg_oval_n400)
                    checkedIcon.background = null

                    checkedIcon = icon
                    checkedIconIndex = i
                }

            }
        }

    }

    private fun initRV() {
        for (i in 0 until menuItems.size) {
            menuIdsList.add(menuItems[i].groupId.toInt())
        }

        binding.rvPmfMenu.adapter =
            PostMenuFolderRVAdapter(
                menuItems,
                requireContext(),
                onButtonClicked = {
                    val postMenuFolderGetFragment = PostMenuFolderGetFragment()
                    val bundle = Bundle()
                    bundle.putSerializable("items", menuItems)
                    bundle.putString("title", binding.etPmfTitle.text.toString())
                    bundle.putString("image", imageUri.toString())
                    bundle.putInt("iconIndex", postIconIndex)

                    postMenuFolderGetFragment.arguments = bundle

                    parentFragmentManager
                        .beginTransaction()
                        .addToBackStack("PostMenuFolderFragment")
                        .replace(R.id.post_menu_folder_frm, postMenuFolderGetFragment)
                        .commitAllowingStateLoss()
                },
            )
    }

    private fun initMenuItems() {
        // TODO Util 로 빼기
        // 안드로이드 버전에 따라 쓰는 함수가 다름
        val bundleData =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable("items", getTypeOf<ArrayList<MenuData>>())
                    ?: arrayListOf()
            } else {
                arguments?.getSerializable("items") as ArrayList<MenuData>
                    ?: arrayListOf()
            } // 제네릭으로 * 을 줘야 getSerializable 가능

        menuItems.addAll(bundleData)

        val title = arguments?.getString("title")
        binding.etPmfTitle.setText(title)
        binding.tvPmfHint.viewGone()
        val image = arguments?.getString("image")
        if (image != "null" && image != "" && image != null) {
            Glide
                .with(this)
                .load(image.toUri())
                .into(binding.ivPmfImage)
            imageUri = image.toUri()
        }
        arguments?.clear()
    }

    private fun initListener() {
        // 뒤로가기
        binding.ivPmfBack.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                requireActivity().finish()
            }
        }

        // 기기 뒤로가기
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    requireActivity().finish()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        // 이미지 추가하기
        binding.ivPmfCamera.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // TODO 아이콘 추가하기
        binding.clPmfAddIcon.setOnClickListener {
            hideKeyboard(requireContext(), binding.root)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // 확인
        binding.btnPmfOk.setOnClickListener {
            postMenuFolder()
        }

        binding.etPmfTitle.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.tvPmfHint.viewGone()
                isTitleFilled = true
            }
        }

    }

    private fun postMenuFolder() {
        val contentResolver = requireContext().contentResolver
        val file = File.createTempFile("tempFile", null, requireContext().cacheDir)
        var menuFolderImgPart: MultipartBody.Part? = null

        imageUri?.let {
            contentResolver.openInputStream(it)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            val requestFile =
                RequestBody.create("application/json".toMediaTypeOrNull(), file)
            menuFolderImgPart = MultipartBody.Part.createFormData("menuFolderImg", file.name, requestFile)
        } ?: run {
            menuFolderImgPart = null
        }

        val menuFolderTitleRequestBody =
            binding.etPmfTitle.text
                .toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

        val menuFolderIconRequestBody =
            postIconIndex.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())


        val menuIdsList = menuItems.map {
            it.menuId
        }.toCollection(ArrayList())

        service
            .postMenuFolder(
                menuFolderImage = menuFolderImgPart,
                menuFolderTitle = menuFolderTitleRequestBody,
                menuFolderIcon = menuFolderIconRequestBody,
                menuIds = menuIdsList,
            ).enqueue(
                object : Callback<MenuFolderResponse> {
                    override fun onResponse(
                        call: Call<MenuFolderResponse>,
                        response: Response<MenuFolderResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            val postedMenuFolder = result?.response
                            postedMenuFolder?.let {
                                Log.d("postedMenuFolder", postedMenuFolder.toString())
                                requireActivity().finish()
                            }
                        } else {
                            val error = response.errorBody()?.string()
                            Log.d("err", error!!)
                        }
                    }

                    override fun onFailure(
                        call: Call<MenuFolderResponse>,
                        t: Throwable,
                    ) {
                        Log.d("postMenuFolder", t.message.toString())
                    }
                },
            )
    }
}
