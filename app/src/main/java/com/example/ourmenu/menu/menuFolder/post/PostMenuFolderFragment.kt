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
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import com.example.ourmenu.util.Utils.getTypeOf
import com.example.ourmenu.util.Utils.viewGone
import com.example.ourmenu.util.Utils.viewVisible
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
    var dummyItems = ArrayList<MenuData>()
    var menuIdsList = ArrayList<Int>()
    private val retrofit = RetrofitObject.retrofit
    private val service = retrofit.create(MenuFolderService::class.java)

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

        initDummy()
        initListener()
        initRV()
        onSaveInstanceState(Bundle())

        return binding.root
    }

    private fun initRV() {
        for (i in 0 until dummyItems.size) {
            menuIdsList.add(dummyItems[i].groupId.toInt())
        }

        binding.rvPmfMenu.adapter =
            PostMenuFolderRVAdapter(
                dummyItems,
                requireContext(),
                onButtonClicked = {
                    val postMenuFolderGetFragment = PostMenuFolderGetFragment()
                    val bundle = Bundle()
                    bundle.putSerializable("items", dummyItems)
                    bundle.putString("title", binding.etPmfTitle.text.toString())
                    bundle.putString("image", imageUri.toString())

                    postMenuFolderGetFragment.arguments = bundle

                    parentFragmentManager
                        .beginTransaction()
                        .replace(R.id.post_menu_folder_frm, postMenuFolderGetFragment)
                        .commitAllowingStateLoss()
                },
            )
    }

    private fun initDummy() {
        // TODO Util 로 빼기
        // 안드로이드 버전에 따라 쓰는 함수가 다름
        dummyItems.addAll(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable("items", getTypeOf<ArrayList<MenuData>>())
                    ?: arrayListOf()
            } else {
                arguments?.getSerializable("items") as ArrayList<MenuData>
                    ?: arrayListOf()
            }, // 제네릭으로 * 을 줘야 getSerializable 가능
        )

        val title = arguments?.getString("title")
        if (title != null && title != "") {
            binding.etPmfTitle.setText(title)
        }
        val image = arguments?.getString("image")
        if (image != "null" && image != "" && image != null) {
            Glide
                .with(this)
                .load(image.toUri())
                .into(binding.ivPmfImage)
            imageUri = image.toUri()
        }
    }

    private fun initListener() {
        // 뒤로가기
        binding.ivPmfBack.setOnClickListener {
            requireActivity().finish()
        }

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
        }

        // 확인
        binding.btnPmfOk.setOnClickListener {
            postMenuFolder()
        }

        binding.etPmfTitle.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.tvPmfHint.viewGone()
            } else {
                if (binding.etPmfTitle.text.toString().isEmpty()) {
                    binding.tvPmfHint.viewVisible()
                }
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

        val toList = arrayListOf<Int>().toList()

        val menuIdsList = ArrayList<RequestBody>()

        service
            .postMenuFolder(
                menuFolderImage = menuFolderImgPart,
                menuFolderTitle = menuFolderTitleRequestBody,
                menuFolderIcon = RequestBody.create("application/json".toMediaTypeOrNull(), "1"),
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
