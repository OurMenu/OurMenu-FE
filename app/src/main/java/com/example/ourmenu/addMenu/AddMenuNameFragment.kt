package com.example.ourmenu.addMenu

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.example.ourmenu.addMenu.adapter.AddMenuImageAdapter
import com.example.ourmenu.addMenu.callback.DragItemTouchHelperCallback
import com.example.ourmenu.data.AddMenuImageData
import com.example.ourmenu.databinding.FragmentAddMenuNameBinding

class AddMenuNameFragment : Fragment() {
    lateinit var binding: FragmentAddMenuNameBinding
    var imageUri: Uri? = null
    lateinit var imageResult: ActivityResultLauncher<String>
    lateinit var imagePermission: ActivityResultLauncher<String>
    lateinit var addMenuImageAdapter: AddMenuImageAdapter
    lateinit var addMenuImageItemList: ArrayList<AddMenuImageData>

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
            // 메뉴 이름, 가격, 가게 이름은 이미 채워져있으면 수정 안되게 함
            setEditTextIfEmpty(binding.etAddMenuNameMenu, it.getString("MENU_NAME", ""))
            setEditTextIfEmpty(binding.etAddMenuNamePrice, it.getString("MENU_PRICE", ""))
            setEditTextIfEmpty(binding.etAddMenuNameRestaurant, it.getString("PLACE_NAME", ""))

            // 가게 주소, 가게 운영 시간은 이미 채워져있어도 수정 가능하게끔 ..
            binding.etAddMenuNameAddress.setText(it.getString("PLACE_ADDRESS", ""))
            binding.etAddMenuNameTime.setText(it.getString("PLACE_TIME", ""))
        }

        binding.btnAddMenuNameNext.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .addToBackStack("MenuAddNameFragment")
                .replace(R.id.cl_add_menu_main, AddMenuTagFragment())
                .commit()
        }

        binding.ivAddMenuNameReturn.setOnClickListener {
            parentFragmentManager.popBackStack()
            requireActivity().currentFocus?.clearFocus()
        }

        initRV()
        initDragAndDrop()

        binding.flAddMenuAddImage.setOnClickListener {
            openGallery()
            addMenuImageItemList.add(AddMenuImageData(imageUri, "menuImage"))
            addMenuImageAdapter.notifyDataSetChanged()
            var count =
                binding.tvAddMenuImageCount.text
                    .toString()
                    .toInt() + 1
            binding.tvAddMenuImageCount.text = count.toString()
        }

        return binding.root
    }

    private fun setEditTextIfEmpty(
        editText: EditText,
        value: String,
    ) {
        if (editText.text.isEmpty()) {
            editText.setText(value)
            editText.isFocusable = false
            editText.isFocusableInTouchMode = false
            editText.isCursorVisible = false
        }
    }

    private fun initDragAndDrop() {
        val dragItemTouchHelperCallback = DragItemTouchHelperCallback(addMenuImageAdapter)
        val itemTouchHelper = ItemTouchHelper(dragItemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvAddMenuNameMenuImage)
    }

    private fun initRV() {
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
