package com.example.ourmenu.mypage

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.stream.QMediaStoreUriLoader.InputStreamFactory
import com.example.ourmenu.R
import com.example.ourmenu.addMenu.AddMenuActivity
import com.example.ourmenu.community.write.CommunityWritePostActivity
import com.example.ourmenu.data.PostData
import com.example.ourmenu.data.account.AccountRefreshTokenData
import com.example.ourmenu.data.account.AccountResponse
import com.example.ourmenu.data.user.UserImageData
import com.example.ourmenu.data.user.UserPatchResponse
import com.example.ourmenu.data.user.UserResponse
import com.example.ourmenu.databinding.FragmentMypageBinding
import com.example.ourmenu.databinding.MypageCurrentPasswordDialogBinding
import com.example.ourmenu.databinding.MypageImgBottomSheetDialogBinding
import com.example.ourmenu.databinding.MypageKebabBottomSheetDialogBinding
import com.example.ourmenu.databinding.MypageNewPasswordDialogBinding
import com.example.ourmenu.databinding.MypageNicknameDialogBinding
import com.example.ourmenu.landing.LandingActivity
import com.example.ourmenu.mypage.adapter.MypageRVAdapter
import com.example.ourmenu.retrofit.NetworkModule
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.AccountService
import com.example.ourmenu.retrofit.service.UserService
import com.example.ourmenu.util.Utils.dpToPx
import com.example.ourmenu.util.Utils.hideKeyboard
import com.example.ourmenu.util.Utils.isValidPassword
import com.example.ourmenu.util.Utils.reissueToken
import com.example.ourmenu.util.Utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

class MypageFragment : Fragment() {
    lateinit var binding: FragmentMypageBinding
    lateinit var dummyItems: ArrayList<PostData>
    lateinit var imageResult: ActivityResultLauncher<String>
    var imageUri: Uri? = null
    var imageFlag = true
    var file = File("/ourmenu")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageResult = registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            imageUri = result
            imageFlag = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        initDummyData()
        initMyPostRV()

        binding.ivMypageAddBtn.setOnClickListener {
            val intent = Intent(requireContext(), AddMenuActivity::class.java)
            startActivity(intent)
        }

        binding.ivMypageKebab.setOnClickListener {
            showCustomDialog()
        }

        binding.ivMypageEditProfileImg.setOnClickListener {
            showImageOptionsDialog()
        }

        return binding.root
    }

    private fun openGallery(callback: () -> Unit) {
        Thread {
            imageResult.launch("image/*")
            while (imageFlag) {
                Thread.sleep(1000)
            }
            imageFlag = true
            callback()
        }.start()
    }

    private fun initMyPostRV() {
        val adapter =
            MypageRVAdapter(dummyItems) {
                // TODO: 해당 게시물로 이동하기
                val intent = Intent(context, CommunityWritePostActivity::class.java)
                intent.putExtra("postData", it)
                intent.putExtra("flag", "post")
                startActivity(intent)
            }

        binding.rvPmfMenu.adapter = adapter
        binding.rvPmfMenu.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initDummyData() {
        dummyItems = ArrayList<PostData>()
        for (i in 1..6) {
            dummyItems.add(
                PostData(
                    "제목",
                    "가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하",
                    R.drawable.menu_sample2,
                    "베터씨",
                    "1 day ago",
                    999,
                    R.drawable.menu_sample3,
                    9,
                ),
            )
        }
    }

    private fun getUserInfo(): ArrayList<String>? {
        NetworkModule.initialize(requireContext())
        val service = RetrofitObject.retrofit.create(UserService::class.java)
        val call = service.getUser()
        var result: ArrayList<String>? = null

        call.enqueue(object : retrofit2.Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    result = arrayListOf(
                        response.body()?.response!!.email,
                        response.body()?.response!!.nickname,
                        response.body()?.response!!.imageUrl
                    )
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
            }

        })
        if (result != null) {
            Log.d("분기", "2")
        }
        return result
    }

    fun uriToFile() {
        file = File(requireContext().cacheDir, "temp_file")
        requireContext().contentResolver.openInputStream(imageUri!!)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        }
    }

    private fun patchUserImage() {
        uriToFile()
        NetworkModule.initialize(requireContext())
        val service = RetrofitObject.retrofit.create(UserService::class.java)
        val call = service.patchUserImage(
            MultipartBody.Part.createFormData(
                "userImage", file.name, RequestBody.create("application/json".toMediaTypeOrNull(), file)
            )
        )

        call.enqueue(object : retrofit2.Callback<UserPatchResponse> {
            override fun onResponse(call: Call<UserPatchResponse>, response: Response<UserPatchResponse>) {
                if (response.isSuccessful) {
                    val userInfo = getUserInfo()
                    Log.d("분기", "1")
                    if (userInfo != null) {
                        Glide.with(requireContext())
                            .load(URL(userInfo[2]))
                            .into(binding.ivMypageProfileImg)
                    }
                } else {
                    Log.d("분기", "3")
                    reissueToken(requireContext())
                }

            }

            override fun onFailure(call: Call<UserPatchResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showImageOptionsDialog() {
        val dialogBinding = MypageImgBottomSheetDialogBinding.inflate(LayoutInflater.from(context))
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

        // Edit profile 이미지 클릭 시 다른 이미지 표시
        binding.ivMypageEditProfileBorder.visibility = View.VISIBLE
        binding.ivMypageEditProfileImgOrange.visibility = View.VISIBLE

        dialogBinding.btnMypageImgDialogAlbum.setOnClickListener {
            openGallery() {
                patchUserImage()
                bottomSheetDialog.dismiss()
            }
        }

        dialogBinding.btnMypageImgDialogDefault.setOnClickListener {
            binding.ivMypageProfileImg.setImageResource(R.drawable.ic_profile)
            bottomSheetDialog.dismiss()
        }

        dialogBinding.btnMypageImgDialogCancel.setOnClickListener {
            // 취소 버튼 클릭 처리
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setOnDismissListener {
            binding.ivMypageEditProfileBorder.visibility = View.GONE
            binding.ivMypageEditProfileImgOrange.visibility = View.GONE
        }

        // 흐린 배경 제거
        bottomSheetDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        bottomSheetDialog.show()
    }

    private fun showCustomDialog() {
        val dialogBinding = MypageKebabBottomSheetDialogBinding.inflate(LayoutInflater.from(context))
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.btnMypageKebabDialogNickname.setOnClickListener {
            bottomSheetDialog.dismiss()
            // TODO: 닉네임 변경하기 다이얼로그 표시
            showNicknameDialog()
        }

        dialogBinding.btnMypageKebabDialogPassword.setOnClickListener {
            bottomSheetDialog.dismiss()
            // TODO: 비밀번호 변경하기 다이얼로그 표시
            showCurrentPasswordDialog()
        }

        dialogBinding.btnMypageKebabDialogLogout.setOnClickListener {
            bottomSheetDialog.dismiss()

            showToast(requireContext(), R.drawable.ic_complete, "로그아웃 되었어요!")

            val intent = Intent(requireContext(), LandingActivity::class.java)
            startActivity(intent)
        }

        dialogBinding.btnMypageKebabDialogCancel.setOnClickListener {
            // 취소 버튼 클릭 처리
            bottomSheetDialog.dismiss()
        }

        // 흐린 배경 제거
        bottomSheetDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        bottomSheetDialog.show()
    }

    private fun showNicknameDialog() {
        val rootView = (activity?.window?.decorView as? ViewGroup)?.getChildAt(0) as? ViewGroup
        // 블러 효과 추가
        rootView?.let { applyBlurEffect(it) }

        val dialogBinding = MypageNicknameDialogBinding.inflate(LayoutInflater.from(context))
        val nicknameDialog =
            android.app.AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        nicknameDialog.setOnShowListener {
            val window = nicknameDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val params = window?.attributes
            params?.width = dpToPx(requireContext(), 288)
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = params
        }

        // 바깥쪽 클릭으로 다이얼로그 닫기 불가
        nicknameDialog.setCanceledOnTouchOutside(false)

        nicknameDialog.setOnDismissListener {
            rootView?.let { removeBlurEffect(it) }
        }

        dialogBinding.ivMypageCloseNicknameDialog.setOnClickListener {
            // 닫기 버튼 클릭 처리
            nicknameDialog.dismiss()
        }

        dialogBinding.btnMypageNicknameConfirm.setOnClickListener {
            val newNickname = dialogBinding.etMypageNickname.text.toString()

            // 오류가 뜰 때 키보드를 숨기기 위한 코드
            hideKeyboard(requireContext(), dialogBinding.root)

            if (newNickname.length > 10) {
                showToast(requireContext(), R.drawable.ic_error, "최대 10자까지 가능해요!")
                dialogBinding.etMypageNickname.setBackgroundResource(R.drawable.edittext_bg_dialog_error)
                return@setOnClickListener
            }

            // TODO: 닉네임 변경 로직 추가
            nicknameDialog.dismiss()
        }

        nicknameDialog.show()
    }

    private fun showCurrentPasswordDialog() {
        val rootView = (activity?.window?.decorView as? ViewGroup)?.getChildAt(0) as? ViewGroup
        // 여기서는 블러 효과 더하는 것만 적용
        rootView?.let { applyBlurEffect(it) }

        val dialogBinding = MypageCurrentPasswordDialogBinding.inflate(LayoutInflater.from(context))
        val currentPasswordDialog =
            android.app.AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        currentPasswordDialog.setOnShowListener {
            val window = currentPasswordDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            // dp를 px로 변환하여 너비를 288dp로 고정
            val params = window?.attributes
            params?.width = dpToPx(requireContext(), 288)
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = params
        }

        // 바깥쪽 클릭으로 다이얼로그 닫기 불가
        currentPasswordDialog.setCanceledOnTouchOutside(false)

        dialogBinding.cbMypageCpwShowPw.setOnCheckedChangeListener { _, isChecked ->
            // 비밀번호가 보여도 font설정에 제대로 되도록 설정
            val currentTypeface = dialogBinding.etMypageCpw.typeface

            if (isChecked) {
                // 비밀번호 보이게 하기
                dialogBinding.etMypageCpw.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // 비밀번호 숨기기
                dialogBinding.etMypageCpw.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            dialogBinding.etMypageCpw.typeface = currentTypeface

            // 커서를 text 제일 뒤로 옮기기
            dialogBinding.etMypageCpw.setSelection(dialogBinding.etMypageCpw.text.length)
        }

        dialogBinding.ivMypageCloseCpwDialog.setOnClickListener {
            // 닫기 버튼 클릭 처리
            rootView?.let { removeBlurEffect(it) }
            currentPasswordDialog.dismiss()
        }

        dialogBinding.btnMypageCpwConfirm.setOnClickListener {
            // TODO: 확인 버튼 클릭 처리
            val currentPassword = dialogBinding.etMypageCpw.text.toString()

            // 오류가 뜰 때 키보드를 숨기기 위한 코드
            hideKeyboard(requireContext(), dialogBinding.root)

            // 현재 비밀번호 확인 로직 추가
            currentPasswordDialog.dismiss()
            showNewPasswordDialog()
        }

        currentPasswordDialog.show()
    }

    private fun showNewPasswordDialog() {
        val rootView = (activity?.window?.decorView as? ViewGroup)?.getChildAt(0) as? ViewGroup

        val dialogBinding = MypageNewPasswordDialogBinding.inflate(LayoutInflater.from(context))
        val newPasswordDialog =
            android.app.AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        newPasswordDialog.setOnShowListener {
            val window = newPasswordDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            // dp를 px로 변환하여 너비를 288dp로 고정
            val params = window?.attributes
            params?.width = dpToPx(requireContext(), 288)
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = params
        }

        // 바깥쪽 클릭으로 다이얼로그 닫기 불가
        newPasswordDialog.setCanceledOnTouchOutside(false)

        // 여기서는 지우는 효과만 적용
        newPasswordDialog.setOnDismissListener {
            rootView?.let { removeBlurEffect(it) }
        }

        dialogBinding.cbMypageNpwShowPw.setOnCheckedChangeListener { _, isChecked ->
            // 비밀번호가 보여도 font설정에 제대로 되도록 설정
            val currentTypeface = dialogBinding.etMypageNpw.typeface

            if (isChecked) {
                // 비밀번호 보이게 하기
                dialogBinding.etMypageNpw.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                dialogBinding.etMypageNpwCheck.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // 비밀번호 숨기기
                dialogBinding.etMypageNpw.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                dialogBinding.etMypageNpwCheck.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            dialogBinding.etMypageNpw.typeface = currentTypeface
            dialogBinding.etMypageNpwCheck.typeface = currentTypeface

            // 커서를 text 제일 뒤로 옮기기
            dialogBinding.etMypageNpw.setSelection(dialogBinding.etMypageNpw.text.length)
            dialogBinding.etMypageNpwCheck.setSelection(dialogBinding.etMypageNpwCheck.text.length)
        }

        dialogBinding.ivMypageCloseNpwDialog.setOnClickListener {
            // 닫기 버튼 클릭 처리
            newPasswordDialog.dismiss()
        }

        // edittext 모두 채워졌는지 확인
        val textWatcher =
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    val newPassword = dialogBinding.etMypageNpw.text.toString()
                    val checkNewPassword = dialogBinding.etMypageNpwCheck.text.toString()
                    dialogBinding.btnMypageNpwConfirm.isEnabled =
                        newPassword.isNotEmpty() &&
                            checkNewPassword.isNotEmpty()
                }

                override fun afterTextChanged(s: Editable?) {}
            }

        dialogBinding.etMypageNpw.addTextChangedListener(textWatcher)
        dialogBinding.etMypageNpwCheck.addTextChangedListener(textWatcher)

        // 초기 상태 설정
        dialogBinding.btnMypageNpwConfirm.isEnabled = false

        dialogBinding.btnMypageNpwConfirm.setOnClickListener {
            val newPassword = dialogBinding.etMypageNpw.text.toString()
            val checkNewPassword = dialogBinding.etMypageNpwCheck.text.toString()

            // 오류가 뜰 때 키보드를 숨기기 위한 코드
            hideKeyboard(requireContext(), dialogBinding.root)

            if (!isValidPassword(newPassword)) {
                // 비밀번호 조건이 맞지 않는 경우
                showToast(requireContext(), R.drawable.ic_error, "비밀번호 조건을 다시 확인해주세요.")
                dialogBinding.etMypageNpw.setBackgroundResource(R.drawable.edittext_bg_dialog_error)
                return@setOnClickListener
            }

            if (newPassword != checkNewPassword) {
                // 비밀번호가 일치하지 않는 경우
                showToast(requireContext(), R.drawable.ic_error, "비밀번호가 일치하지 않아요.")
                dialogBinding.etMypageNpwCheck.setBackgroundResource(R.drawable.edittext_bg_dialog_error)
                return@setOnClickListener
            }

            // 비밀번호 변경 로직 추가
            newPasswordDialog.dismiss()
        }

        newPasswordDialog.show()
    }
}

private fun applyBlurEffect(viewGroup: ViewGroup) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        viewGroup.setRenderEffect(RenderEffect.createBlurEffect(10f, 10f, Shader.TileMode.CLAMP))
    }
}

private fun removeBlurEffect(viewGroup: ViewGroup) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        viewGroup.setRenderEffect(null)
    }
}
