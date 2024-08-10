package com.example.ourmenu.addMenu

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.R
import com.example.ourmenu.addMenu.adapter.AddMenuImageAdapter
import com.example.ourmenu.data.AddMenuImageData
import com.example.ourmenu.databinding.FragmentAddMenuNameBinding
import com.example.ourmenu.menu.callback.DragItemTouchHelperCallback

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
            binding.etAddMenuNameMenu.setText(it.getString("MENU_NAME", ""))
            binding.etAddMenuNamePrice.setText(it.getString("MENU_PRICE", ""))
            binding.etAddMenuNameRestaurant.setText(it.getString("PLACE_NAME", ""))
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
