package com.example.ourmenu.menu.menuFolder

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.addMenu.AddMenuActivity
import com.example.ourmenu.data.BaseResponse
import com.example.ourmenu.data.HomeMenuData
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.data.menuFolder.request.MenuFolderRequest
import com.example.ourmenu.data.menuFolder.response.MenuFolderResponse
import com.example.ourmenu.data.menu.response.MenuArrayResponse
import com.example.ourmenu.data.menuFolder.request.MenuFolderPatchRequest
import com.example.ourmenu.databinding.CommunityDeleteDialogBinding
import com.example.ourmenu.databinding.FragmentMenuFolderDetailBinding
import com.example.ourmenu.menu.adapter.MenuFolderAllFilterSpinnerAdapter
import com.example.ourmenu.menu.adapter.MenuFolderDetailFilterSpinnerAdapter
import com.example.ourmenu.menu.adapter.MenuFolderDetailRVAdapter
import com.example.ourmenu.menu.iteminterface.MenuItemClickListener
import com.example.ourmenu.menu.menuInfo.MenuInfoActivity
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuFolderService
import com.example.ourmenu.retrofit.service.MenuService
import com.example.ourmenu.util.Utils.applyBlurEffect
import com.example.ourmenu.util.Utils.dpToPx
import com.example.ourmenu.util.Utils.removeBlurEffect
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder

class MenuFolderDetailFragment : Fragment() {
    lateinit var binding: FragmentMenuFolderDetailBinding
    private val menuItems = ArrayList<MenuData>()
    private val sortedMenuItems = ArrayList<MenuData>()
    lateinit var rvAdapter: MenuFolderDetailRVAdapter
    private var isEdit: Boolean = false
    private var menuFolderId = 0
    private var menuFolderTitle = ""

    private val retrofit = RetrofitObject.retrofit
    private val menuFolderService = retrofit.create(MenuFolderService::class.java)
    private val menuService = retrofit.create(MenuService::class.java)

    private var imageUri: Uri? = null

    // 갤러리 open
    private val galleryPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {

                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
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
                    binding.ivMenuFolderMainImage.setImageURI(imageUri)
                }
            }
        }


    @RequiresApi(Build.VERSION_CODES.S) // 이거 있어야 setRenderEffect 가능
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMenuFolderDetailBinding.inflate(layoutInflater)

        arguments?.getInt("menuFolderId")?.let {
            menuFolderId = it
        }
        arguments?.getString("menuFolderTitle")?.let {
            menuFolderTitle = it
            binding.etMenuFolderTitle.setText(menuFolderTitle)
        }
        arguments?.getString("menuFolderImgUrl")?.let {
            Glide.with(this)
                .load(it)
                .into(binding.ivMenuFolderMainImage)
        }

        initListener()
        initKebabOnClickListener()
        getMenuItems()
//        initRV()
//        initSpinner()

        // 수정화면이면 함수 사용, 아니면 그냥 실행
        isEdit = arguments?.getBoolean("isEdit")!!
        if (isEdit) {
            setEdit()
        }
        return binding.root
    }

    private fun initSpinner() {
        val adapter =
            MenuFolderDetailFilterSpinnerAdapter<String>(requireContext(), arrayListOf("이름순", "등록순", "가격순"))
        adapter.setDropDownViewResource(R.layout.spinner_item_background)
        binding.spnMenuFolderDetailFilter.adapter = adapter
        binding.spnMenuFolderDetailFilter.setSelection(1)
        binding.spnMenuFolderDetailFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                adapter.selectedPos = position
                sortBySpinner(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun sortBySpinner(position: Int) {
        when (position) {
            0 -> { // 이름순, 이름이 같아면 가격순
                sortedMenuItems.sortWith(compareBy<MenuData> { it.menuTitle }.thenBy { it.menuPrice })
            }

            1 -> { // 등록순
                sortedMenuItems.sortWith(compareBy<MenuData> {
                    val formatter = DateTimeFormatterBuilder()
                        .appendPattern("yyyy-MM-dd'T'HH:mm:ss") // #1
                        .toFormatter()

                    LocalDateTime.parse(it.createdAt, formatter)
                })
            }

            2 -> { // 가격순, 가격이 같다면 이름순
                sortedMenuItems.sortWith(compareBy<MenuData> { it.menuPrice }.thenBy { it.menuTitle })
            }

            else -> return
        }
        rvAdapter.updateList(sortedMenuItems)
        binding.rvMenuFolderMenuList.scrollToPosition(0)


    }

    @SuppressLint("SetTextI18n")
    private fun getMenuItems() {
        menuService.getMenus(
            tags = arrayListOf<String>(),
            title = null,
            menuFolderId = menuFolderId,
            page = null,
            size = 100,
            minPrice = 5000, maxPrice = 50000 // default 값으로 필수로 넣어달라함

        ).enqueue(object : Callback<MenuArrayResponse> {
            override fun onResponse(call: Call<MenuArrayResponse>, response: Response<MenuArrayResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val menuData = result?.response
                    menuData?.let {
                        menuItems.addAll(menuData)
                        sortedMenuItems.addAll(menuData)
                        binding.tvMenuFolderMenuNumber.text = menuItems.size.toString() + " 개"
                        initRV()
                        initSpinner()
                    }
                }
            }

            override fun onFailure(call: Call<MenuArrayResponse>, t: Throwable) {
                Log.d("MenuFolderDetail", t.message.toString())
            }

        })


    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun initListener() {
        // TODO 뒤로가기 설정
        binding.ivMenuFolderBack.setOnClickListener {
            if (isEdit) resetEdit()
            else requireActivity().finish()
        }

        // 이미지 가져오기
        binding.ivMenuFolderCamera.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            else
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        binding.btnMenuFolderEditOk
    }

    private fun initRV() {

        rvAdapter = MenuFolderDetailRVAdapter(
            menuItems, requireContext(),
            onButtonClicked = {
                val intent = Intent(context, AddMenuActivity::class.java)
                startActivity(intent)
            }).apply {
            setOnItemClickListener(object : MenuItemClickListener {

                override fun onMenuClick(groupId: Int) {
                    val intent = Intent(context, MenuInfoActivity::class.java)
                    intent.putExtra("groupId", groupId)
                    intent.putExtra("tag", "menuInfo")
                    startActivity(intent)
                }

                override fun onMapClick(groupId: Int) {
                    val intent = Intent(context, MenuInfoActivity::class.java)
                    intent.putExtra("groupId", groupId)
                    intent.putExtra("tag", "menuInfoMap")
                    startActivity(intent)
                }

            })
        }
        binding.rvMenuFolderMenuList.adapter = rvAdapter
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setEdit() {
        isEdit = true
        // blur 효과 제거
        binding.clMenuFolderBlur.setRenderEffect(null)
        // 메뉴판 수정하기, 삭제하기, 취소 gone
        binding.clMenuFolderKebab.visibility = View.GONE

        // 상단 이미지 blur 효과 적용
        binding.ivMenuFolderMainImage.setRenderEffect(
            RenderEffect.createBlurEffect(2f, 2f, Shader.TileMode.CLAMP),
        )
        // Kebab 버튼 gone
        binding.ivMenuFolderKebab.visibility = View.GONE

        // 카메라 , textView visible
        binding.llMenuFolderEdit.visibility = View.VISIBLE

        // edittext enabled, drawable 적용
        with(binding.etMenuFolderTitle) {
            isEnabled = true
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pen, 0, 0, 0)
        }


        // 확인 버튼 visible
        binding.btnMenuFolderEditOk.visibility = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun resetEdit() {
        isEdit = false

        binding.ivMenuFolderMainImage.setRenderEffect(null) // 상단 이미지 blur 효과 적용
        binding.ivMenuFolderKebab.visibility = View.VISIBLE // Kebab 버튼 visible
        binding.llMenuFolderEdit.visibility = View.GONE // 카메라 , textView visible
        with(binding.etMenuFolderTitle) { // edittext 원상 복구
            isEnabled = false
            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
        binding.btnMenuFolderEditOk.visibility = View.GONE  // 확인 버튼 gone

        // 메뉴판 PATCH API
        patchMenuFolder()
    }

    @RequiresApi(Build.VERSION_CODES.S) // 이거 있어야 setRenderEffect 가능
    private fun initKebabOnClickListener() {
        // Kebab 버튼 클릭
        binding.ivMenuFolderKebab.setOnClickListener {
            binding.clMenuFolderBlur.setRenderEffect(
                // 배경에 blur 효과 적용
                RenderEffect.createBlurEffect(7.5f, 7.5f, Shader.TileMode.CLAMP),
            )
            binding.clMenuFolderKebab.visibility = View.VISIBLE // 메뉴판 수정하기, 삭제하기, 취소 visible
        }

        // 메뉴판 수정하기
        binding.btnMenuFolderEdit.setOnClickListener {
            setEdit()
        }

        // 삭제하기
        binding.btnMenuFolderDelete.setOnClickListener {
            binding.clMenuFolderBlur.setRenderEffect(null) // blur 효과 제거
            binding.clMenuFolderKebab.visibility = View.GONE // 메뉴판 수정하기, 삭제하기, 취소 gone


            // TODO 삭제버튼 클릭 API 구현
            showDeleteDialog()
        }

        // 취소
        binding.btnMenuFolderCancel.setOnClickListener {

            binding.clMenuFolderBlur.setRenderEffect(null) // blur 효과 제거
            binding.clMenuFolderKebab.visibility = View.GONE // 메뉴판 수정하기, 삭제하기, 취소 gone
        }

        // 확인
        binding.btnMenuFolderEditOk.setOnClickListener {
            resetEdit()
        }
    }

    private fun patchMenuFolder() {
        val menuFolderId = arguments?.getInt("menuFolderId")!!

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
            binding.etMenuFolderTitle.text.toString().toRequestBody("application/json".toMediaTypeOrNull())


        menuFolderService.patchMenuFolder(
            menuFolderId = menuFolderId,
            menuFolderImage = menuFolderImgPart,
            menuFolderTitle = menuFolderTitleRequestBody,
            menuFolderIcon = RequestBody.create("application/json".toMediaTypeOrNull(), "1"),
        ).enqueue(object : Callback<MenuFolderResponse> {
            override fun onResponse(call: Call<MenuFolderResponse>, response: Response<MenuFolderResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val menuFolder = result?.response
                    menuFolder?.let {
                        Log.d("menuFolder", menuFolder.toString())
                    }
                }
            }

            override fun onFailure(call: Call<MenuFolderResponse>, t: Throwable) {
                Log.d("patchMenuFolder", t.message.toString())

            }

        })
    }

    // 삭제하기 다이얼로그
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
            deleteMenuFolder()

            deleteDialog.dismiss()
        }

        dialogBinding.btnCddCancel.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }

    // /menuFolder/{menuFolderId} DELETE API
    private fun deleteMenuFolder() {
        menuFolderService.deleteMenuFolder(menuFolderId).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("deleteMenuFolder", result.toString())
                    requireActivity().finish()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.d("deleteMenuFolder", t.toString())
            }

        })
    }
}
