package com.example.ourmenu.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.example.ourmenu.addMenu.AddMenuActivity
import com.example.ourmenu.data.HomeMenuData
import com.example.ourmenu.data.onboarding.data.OnboardingData
import com.example.ourmenu.databinding.FragmentHomeBinding
import com.example.ourmenu.databinding.HomeOnboardingDialogBinding
import com.example.ourmenu.home.adapter.HomeMenuMainRVAdapter
import com.example.ourmenu.home.adapter.HomeMenuSubRVAdapter
import com.example.ourmenu.home.iteminterface.HomeItemClickListener
import com.example.ourmenu.menu.menuInfo.MenuInfoActivity
import com.example.ourmenu.util.Utils.applyBlurEffect
import com.example.ourmenu.util.Utils.dpToPx
import com.example.ourmenu.util.Utils.removeBlurEffect

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var dummyItems: ArrayList<HomeMenuData>
    lateinit var itemClickListener: HomeItemClickListener
    private var onBoardingList = ArrayList<OnboardingData>()
    lateinit var mContext: Context
    lateinit var spf: SharedPreferences
    lateinit var edit: SharedPreferences.Editor

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        spf = requireContext().getSharedPreferences("Onboarding", Context.MODE_PRIVATE)
        edit = spf.edit()
        // 오늘 처음이면 온보딩 실행
        if (spf.getBoolean("isFirst", true)) {
            initOnboarding()
        }


        initDummyData()
        initItemClickListener()
        initMainMenuRV()
        initSubMenuRV()





        return binding.root
    }


    private fun initOnboarding() {
        edit.putBoolean("isFirst", false)
        edit.apply()


        val rootView = (activity?.window?.decorView as? ViewGroup)?.getChildAt(0) as? ViewGroup
        // 블러 효과 추가
        rootView?.let { applyBlurEffect(it) }

        val dialogBinding = HomeOnboardingDialogBinding.inflate(LayoutInflater.from(context))
        val onboardingDialog =
            android.app.AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

//        CoroutineScope(Dispatchers.IO).launch {
//        val retrofit = RetrofitObject.retrofit
//        val onboardingService = retrofit.create(OnboardingService::class.java)
//
//        onboardingService.getOnboarding().enqueue(
//            object : Callback<OnboardingResponse> {
//                override fun onResponse(call: Call<OnboardingResponse>, response: Response<OnboardingResponse>) {
//                    if (response.isSuccessful) {
//                        val result = response.body()
//                        result?.onBoardingData?.let {
//                            onBoardingList = result.onBoardingData
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<OnboardingResponse>, t: Throwable) {
//                    Log.d("getOnboarding()", t.message.toString())
//                }
//
//            }
//        )

//        }


        onboardingDialog.setOnShowListener {
            val window = onboardingDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val params = window?.attributes
            params?.width = dpToPx(mContext, 288)
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = params

            // 질문 설정
        }
//        setOnboarding(dialogBinding)

        // dialog 사라지면 블러효과도 같이 사라짐
        onboardingDialog.setOnDismissListener {
            rootView?.let { removeBlurEffect(it) }
        }

        dialogBinding.ivOnboardingDice.setOnClickListener {
            setOnboarding(dialogBinding)
        }
        dialogBinding.root.setOnClickListener {
            Log.d("id", "id")
        }

        dialogBinding.ivOnboardingClose.setOnClickListener {
            // 닫기 버튼 클릭 처리
            onboardingDialog.dismiss()
        }

        dialogBinding.clOnboardingFirst.setOnClickListener {
            // API
            onboardingDialog.dismiss()
        }

        dialogBinding.clOnboardingSecond.setOnClickListener {
            // API
            onboardingDialog.dismiss()
        }

        onboardingDialog.show()
    }

    // 질문 설정
    private fun setOnboarding(dialogBinding: HomeOnboardingDialogBinding) {
        while (true) {
            if (onBoardingList.isEmpty()) break
            val randomQuestion = onBoardingList.random()
            if (randomQuestion.question == dialogBinding.tvOnboardingQuestion.text)
                continue
            dialogBinding.tvOnboardingQuestion.text = randomQuestion.question
            dialogBinding.tvOnboardingFirstText.text = randomQuestion.yes
            dialogBinding.tvOnboardingSecondText.text = randomQuestion.no
            Glide.with(this)
                .load(randomQuestion.yesImg)
                .into(dialogBinding.ivOnboardingFirstIcon)
            Glide.with(this)
                .load(randomQuestion.noImg)
                .into(dialogBinding.ivOnboardingSecondIcon)

        }
    }

    private fun initItemClickListener() {
        itemClickListener =
            object : HomeItemClickListener {
                override fun onItemClick(homeMenuData: HomeMenuData) {
                    val intent = Intent(activity, MenuInfoActivity::class.java)
                    // TODO 추가할 데이터 추가
                    intent.putExtra("tag", "menuInfo")
                    startActivity(intent)
                }
            }

        binding.ivHomeTitleAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initSubMenuRV() {
        binding.rvHomeMenuSubFirst.adapter =
            HomeMenuSubRVAdapter(dummyItems).apply {
                setOnItemClickListener(itemClickListener)
            }
        binding.rvHomeMenuSubSecond.adapter =
            HomeMenuSubRVAdapter(dummyItems).apply {
                setOnItemClickListener(itemClickListener)
            }
    }

    private fun initDummyData() {
        dummyItems = ArrayList<HomeMenuData>()
        for (i in 1..6) {
            dummyItems.add(
                HomeMenuData("1", "menu2$i", "store3")
            )
        }
    }

    private fun initMainMenuRV() {
        binding.rvHomeMenuMain.adapter =
            HomeMenuMainRVAdapter(dummyItems, requireContext()).apply {
                setOnItemClickListener(itemClickListener)
            }

        // 아이템의 width를 구하기 위해 viewTreeObserver 사용
        // 시작 위치 조정용
        binding.rvHomeMenuMain.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.rvHomeMenuMain.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val width = binding.rvHomeMenuMain.layoutManager?.getChildAt(0)?.width
                val screenWidth = context?.resources?.displayMetrics?.widthPixels
                val offset = (screenWidth!! - width!!) / 2

                (binding.rvHomeMenuMain.layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(
                        ((1000 / dummyItems.size.toInt()) * dummyItems.size) - 1,
                        offset
                    )
            }

        })

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvHomeMenuMain)
    }
}
