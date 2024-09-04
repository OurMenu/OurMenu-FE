package com.example.ourmenu.addMenu

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.R
import com.example.ourmenu.addMenu.adapter.AddMenuFolderRVAdapter
import com.example.ourmenu.addMenu.adapter.AddMenuImageAdapter
import com.example.ourmenu.addMenu.callback.DragItemTouchHelperCallback
import com.example.ourmenu.data.AddMenuImageData
import com.example.ourmenu.data.menuFolder.data.MenuFolderData
import com.example.ourmenu.data.menuFolder.response.MenuFolderArrayResponse
import com.example.ourmenu.databinding.FragmentAddMenuNameBinding
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuFolderService
import com.example.ourmenu.util.Utils.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale

class AddMenuNameFragment : Fragment() {
    lateinit var binding: FragmentAddMenuNameBinding
    var imageUri: Uri? = null
    lateinit var imageResult: ActivityResultLauncher<String>
    lateinit var imagePermission: ActivityResultLauncher<String>
    lateinit var addMenuImageAdapter: AddMenuImageAdapter
    lateinit var addMenuImageItemList: ArrayList<AddMenuImageData>

    private var menuFolderItems = ArrayList<MenuFolderData>()
    private val retrofit = RetrofitObject.retrofit
    private val menuFolderService = retrofit.create(MenuFolderService::class.java)

    private lateinit var menuFolderAdapter: AddMenuFolderRVAdapter

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private fun openGallery() {
        imageResult.launch("image/*")
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            data?.data?.let {
                imageUri = it
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageResult =
            registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
                imageUri = result
                if (imageUri != null) {
                    addMenuImageItemList.add(AddMenuImageData(imageUri, "menuImage"))
                    addMenuImageAdapter.notifyDataSetChanged()
                    var count =
                        binding.tvAddMenuImageCount.text
                            .toString()
                            .toInt() + 1
                    binding.tvAddMenuImageCount.text = count.toString()
                }
            }
        imagePermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    openGallery()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddMenuNameBinding.inflate(inflater, container, false)

        // 전달받은 데이터로 EditText 채우기
        arguments?.let {
            binding.etAddMenuNameMenu.setText(it.getString("MENU_NAME", ""))
            binding.etAddMenuNamePrice.setText(it.getString("MENU_PRICE", ""))
            binding.etAddMenuNameRestaurant.setText(it.getString("PLACE_NAME", ""))
            binding.etAddMenuNameAddress.setText(it.getString("PLACE_ADDRESS", ""))
            binding.etAddMenuNameTime.setText(it.getString("PLACE_TIME", ""))
        }

        // 필수적인 EditText들에 TextWatcher 추가 및 초기 상태 확인
        setupTextWatchers(
            binding.etAddMenuNameName,
            binding.etAddMenuNameMenu,
            binding.etAddMenuNamePrice,
            binding.etAddMenuNameRestaurant,
            binding.etAddMenuNameAddress,
        )

        // 화면이 처음 로드될 때 필드 유효성 검사
        validateFields()

        binding.btnAddMenuNameNext.setOnClickListener {
            val address = binding.etAddMenuNameAddress.text.toString()
            val coordinates = getCoordinatesFromAddress(address)

            // 모든 EditText의 포커스를 해제하여 유효성 검사를 트리거
            binding.etAddMenuNameName.clearFocus()
            binding.etAddMenuNameMenu.clearFocus()
            binding.etAddMenuNamePrice.clearFocus()
            binding.etAddMenuNameRestaurant.clearFocus()
            binding.etAddMenuNameAddress.clearFocus()
            binding.etAddMenuNameTime.clearFocus()

            // 필드 유효성 검사
            validateFields()

            // 가게 운영 시간이 255글자를 넘는지 확인
            if (binding.etAddMenuNameTime.text
                    .toString()
                    .length > 500
            ) {
                showToast(
                    requireContext(),
                    R.drawable.ic_error,
                    "가게 운영 시간은 500글자를 넘길 수 없습니다. 현재: ${
                        binding.etAddMenuNameTime.text
                            .toString()
                            .length
                    } 글자",
                )
                return@setOnClickListener // 조건이 만족되면 이후 코드를 실행하지 않고 리턴
            }

            val fullAddress =
                binding.etAddMenuNameAddress.text.toString() + binding.etAddMenuNameAddressDetail.text.toString()
            val selectedMenuFolders = menuFolderAdapter.getSelectedItems()
            val selectedMenuFolderIds = ArrayList(selectedMenuFolders.map { it.menuFolderId })

            val menuPrice = parseIntWithComma(binding.etAddMenuNamePrice.text.toString())

            // Uri 리스트로 변환하여 Bundle에 추가
            val imageUriList = ArrayList<Uri>()
            addMenuImageItemList.forEach { imageData ->
                imageData.imageUri?.let {
                    imageUriList.add(it)
                }
            }

            val bundle =
                Bundle().apply {
                    putIntegerArrayList("menuFolderIds", selectedMenuFolderIds)
                    putString("menuTitle", binding.etAddMenuNameMenu.text.toString())
                    putInt("menuPrice", menuPrice)
                    putString("storeName", binding.etAddMenuNameRestaurant.text.toString())
                    putString("storeAddress", fullAddress)
                    putString("storeMemo", binding.etAddMenuNameTime.text.toString())

                    // 위도와 경도가 null이 아닌 경우에만 번들에 추가
                    coordinates?.let {
                        putDouble("storeLatitude", it.first)
                        putDouble("storeLongitude", it.second)
                    }

                    // Uri 리스트를 번들에 추가
                    putParcelableArrayList("menuImgs", imageUriList)
                }

            val addMenuTagFragment =
                AddMenuTagFragment().apply {
                    arguments = bundle
                }

            parentFragmentManager
                .beginTransaction()
                .addToBackStack("MenuAddNameFragment")
                .replace(R.id.cl_add_menu_main, addMenuTagFragment)
                .commit()
        }

        binding.ivAddMenuNameReturn.setOnClickListener {
            parentFragmentManager.popBackStack()
            requireActivity().currentFocus?.clearFocus()
        }

        initImageRV()
        initDragAndDrop()
        initMenuFolderRV()
        getMenuFolders()

        binding.flAddMenuAddImage.setOnClickListener {
            openGallery()
        }

        return binding.root
    }

    private fun getCoordinatesFromAddress(address: String): Pair<Double, Double>? {
        val context = context ?: return null // context가 null이면 함수 자체에서 null을 반환
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses != null && addresses.isNotEmpty()) { // addresses가 null이 아닌지 확인
                val location = addresses[0]
                Pair(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun initMenuFolderRV() {
        binding.rvAmnMenuFolder.visibility = View.GONE
        binding.btnAmnMenuFolderConfirm.isEnabled = false

        binding.etAddMenuNameName.setOnClickListener {
            binding.rvAmnMenuFolder.visibility = View.VISIBLE
        }

        menuFolderAdapter =
            AddMenuFolderRVAdapter(ArrayList()) { selectedItems ->
                binding.btnAmnMenuFolderConfirm.isEnabled = selectedItems.isNotEmpty()
            }

        binding.rvAmnMenuFolder.adapter = menuFolderAdapter
        binding.rvAmnMenuFolder.layoutManager = LinearLayoutManager(context)

        // 확인 버튼을 클릭하면 dropdown 숨기고 선택된 항목들을 EditText에 설정
        binding.btnAmnMenuFolderConfirm.setOnClickListener {
            binding.rvAmnMenuFolder.visibility = View.GONE
            val selectedTitles = menuFolderAdapter.getSelectedItems().map { it.menuFolderTitle }.joinToString(", ")
            binding.etAddMenuNameName.setText(selectedTitles)
        }
    }

    private fun getMenuFolders() {
        menuFolderService.getMenuFolders().enqueue(
            object : Callback<MenuFolderArrayResponse> {
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
                            menuFolderAdapter =
                                AddMenuFolderRVAdapter(menuFolderItems) { selectedItems ->
                                    binding.btnAmnMenuFolderConfirm.isEnabled = selectedItems.isNotEmpty()
                                }
                            binding.rvAmnMenuFolder.adapter = menuFolderAdapter
                            menuFolderAdapter.notifyDataSetChanged()
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

    private fun setupTextWatchers(vararg editTexts: EditText) {
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
                    validateFields()
                }

                override fun afterTextChanged(s: Editable?) {}
            }

        editTexts.forEach { it.addTextChangedListener(textWatcher) }
    }

    private fun validateFields() {
        val areAllFieldsFilled =
            listOf(
                binding.etAddMenuNameName,
                binding.etAddMenuNameMenu,
                binding.etAddMenuNamePrice,
                binding.etAddMenuNameRestaurant,
                binding.etAddMenuNameAddress,
            ).all { it.text.toString().isNotEmpty() }

        binding.btnAddMenuNameNext.isEnabled = areAllFieldsFilled
    }

    private fun parseIntWithComma(input: String): Int =
        if (input.contains(",")) {
            input.replace(",", "").toInt()
        } else {
            input.toInt()
        }

    private fun initDragAndDrop() {
        val dragItemTouchHelperCallback = DragItemTouchHelperCallback(addMenuImageAdapter)
        val itemTouchHelper = ItemTouchHelper(dragItemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvAddMenuNameMenuImage)
    }

    private fun initImageRV() {
        addMenuImageItemList = arrayListOf<AddMenuImageData>()
        addMenuImageAdapter = AddMenuImageAdapter(addMenuImageItemList)

        addMenuImageAdapter.imageListener =
            object : AddMenuImageAdapter.OnImageClickListener {
                override fun onImageClick(addMenuImageData: AddMenuImageData) {
                    addMenuImageItemList.remove(addMenuImageData)
                    // List 반영
                    addMenuImageAdapter.notifyDataSetChanged()
                    // addMenuImageAdapter.notifyItemRemoved(addMenuImageData);

                    var count =
                        binding.tvAddMenuImageCount.text
                            .toString()
                            .toInt() - 1
                    binding.tvAddMenuImageCount.text = count.toString()
                }

                override fun onClick(v: View?) {
                }
            }

        binding.rvAddMenuNameMenuImage.layoutManager =
            LinearLayoutManager(
                requireContext(),
                RecyclerView.HORIZONTAL,
                false,
            )
        binding.rvAddMenuNameMenuImage.adapter = addMenuImageAdapter
    }
}
