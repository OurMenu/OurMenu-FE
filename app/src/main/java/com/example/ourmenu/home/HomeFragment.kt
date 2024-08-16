package com.example.ourmenu.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
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
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.addMenu.AddMenuActivity
import com.example.ourmenu.data.HomeMenuData
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.data.onboarding.data.OnboardingData
import com.example.ourmenu.data.onboarding.data.OnboardingMenuData
import com.example.ourmenu.data.onboarding.data.OnboardingTagData
import com.example.ourmenu.data.onboarding.response.OnboardingRecommendResponse
import com.example.ourmenu.data.onboarding.response.OnboardingResponse
import com.example.ourmenu.data.onboarding.response.OnboardingStateResponse
import com.example.ourmenu.data.onboarding.response.OnboardingTagResponse
import com.example.ourmenu.databinding.FragmentHomeBinding
import com.example.ourmenu.databinding.HomeOnboardingDialogBinding
import com.example.ourmenu.home.adapter.HomeMenuMainRVAdapter
import com.example.ourmenu.home.adapter.HomeMenuSubRVAdapter
import com.example.ourmenu.home.iteminterface.HomeItemClickListener
import com.example.ourmenu.home.recommend.RecommendMain
import com.example.ourmenu.menu.menuInfo.MenuInfoActivity
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.OnboardingService
import com.example.ourmenu.util.Utils.applyBlurEffect
import com.example.ourmenu.util.Utils.dpToPx
import com.example.ourmenu.util.Utils.loadImageFromUrl
import com.example.ourmenu.util.Utils.removeBlurEffect
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.properties.Delegates

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var dummyItems: ArrayList<OnboardingMenuData>
    lateinit var itemClickListener: HomeItemClickListener
    lateinit var mContext: Context
    lateinit var tagMenus: ArrayList<OnboardingTagData>

    lateinit var mainMenuItems: ArrayList<OnboardingMenuData>
    lateinit var subMenuItems: ArrayList<ArrayList<OnboardingMenuData>>


    lateinit var spf: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    private val retrofit = RetrofitObject.retrofit
    private val onboardingService = retrofit.create(OnboardingService::class.java)

    private var onBoardingList = RecommendMain.onBoardingList
    private var questionId = -1

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

        val isLoggedIn = arguments?.getBoolean("isLoggedIn") ?: false

        /* spf 저장위치
        * View -> Tool Windows -> Device Explorer
        * -> data/data/com.example.ourmenu/shared_prefs */
        spf = requireContext().getSharedPreferences("Onboarding", Context.MODE_PRIVATE)
        edit = spf.edit()

//        if (isFirst()) {
//            initOnboarding()
//            Log.d("isF", isFirst().toString())
//        }

        if (isLoggedIn) {
            initOnboarding()
        } else {
            getOnboardingState()
        }

        initDummyData()
        initItemClickListener()
//        initMainMenuRV()
        initSubMenuRV()





        return binding.root
    }

    private fun getOnboardingState() {
        onboardingService.getOnboardingState().enqueue(object : Callback<OnboardingStateResponse> {
            override fun onResponse(call: Call<OnboardingStateResponse>, response: Response<OnboardingStateResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.response?.let {
                        RecommendMain.setRecommendMain(it.questionId, it.answerType, binding)
                        getOnboardingRecommend(it.questionId, it.answerType)
                    }
                }
            }

            override fun onFailure(call: Call<OnboardingStateResponse>, t: Throwable) {
                Log.d("onBoardingState", t.toString())
            }
        })
    }

    private fun getOnboardingRecommend(questionId: Int, answerType: String) {
        onboardingService.getRecommend(questionId, answerType).enqueue(object : Callback<OnboardingRecommendResponse> {
            override fun onResponse(
                call: Call<OnboardingRecommendResponse>,
                response: Response<OnboardingRecommendResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.response?.let {
                        mainMenuItems = it.menus
                        initMainMenuRV()
                    }
                }
            }

            override fun onFailure(call: Call<OnboardingRecommendResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }


    // true면 온보딩 실행, false 면 실행 x
//    private fun isFirst(): Boolean {
//        val year = spf.getInt("year", -1)
//        val month = spf.getInt("month", -1)
//        val day = spf.getInt("day", -1)
//
//        // 초기 유저인 경우 온보딩 실행
//        if (year == -1 && month == -1 && day == -1) {
//            return true
//        }
//
//        // 연, 월, 일이 모두 같으면 false, 다르면 true
//        return !(year == LocalDate.now().year
//            && month == LocalDate.now().monthValue
//            && day == LocalDate.now().dayOfMonth)
//    }


    private fun initOnboarding() {
//        val year = LocalDate.now().year
//        val month = LocalDate.now().monthValue
//        val day = LocalDate.now().dayOfMonth
//
//        edit.putInt("year", year)
//        edit.putInt("month", month)
//        edit.putInt("day", day)
//        edit.commit()


        val rootView = (activity?.window?.decorView as? ViewGroup)?.getChildAt(0) as? ViewGroup
        // 블러 효과 추가
        rootView?.let { applyBlurEffect(it) }

        val dialogBinding = HomeOnboardingDialogBinding.inflate(LayoutInflater.from(context))
        val onboardingDialog =
            android.app.AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()


        onboardingDialog.setOnShowListener {
            val window = onboardingDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val params = window?.attributes
            params?.width = dpToPx(mContext, 288)
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = params

            // 질문 설정
        }
        setOnboarding(dialogBinding)

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

        dialogBinding.btnOnboardingFirst.setOnClickListener {
            // API
            getHomeRecommend("YES")
            onboardingDialog.dismiss()
        }

        dialogBinding.btnOnboardingSecond.setOnClickListener {
            // API
            getHomeRecommend("NO")
            onboardingDialog.dismiss()
        }

        onboardingDialog.show()
    }

    // 질문 설정
    private fun setOnboarding(dialogBinding: HomeOnboardingDialogBinding) {
        while (true) {
            if (onBoardingList.isEmpty()) break
            val randomQuestion = onBoardingList.random()
            if (randomQuestion.questionId == questionId)
                continue
            dialogBinding.tvOnboardingQuestion.text = randomQuestion.question
            dialogBinding.tvOnboardingFirstText.text = randomQuestion.yes
            dialogBinding.tvOnboardingSecondText.text = randomQuestion.no

            dialogBinding.ivOnboardingFirstIcon.setImageResource(
                randomQuestion.yesImg
            )
            dialogBinding.ivOnboardingSecondIcon.setImageResource(
                randomQuestion.noImg
            )

            questionId = randomQuestion.questionId

            break

        }
    }

    private fun getHomeRecommend(answer: String) {

        RecommendMain.setRecommendMain(questionId, answer, binding)

        onboardingService.getRecommend(
            questionId = questionId, answer = answer
        ).enqueue(object : Callback<OnboardingRecommendResponse> {
            override fun onResponse(
                call: Call<OnboardingRecommendResponse>,
                response: Response<OnboardingRecommendResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.response?.let {

                        mainMenuItems = it.menus
                        Log.d("riu", it.recommendImgUrl)

                    }

                }
            }

            override fun onFailure(call: Call<OnboardingRecommendResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
        getHomeTag()
    }

    private fun getHomeTag() {
        onboardingService.getOnboardingTag().enqueue(object : Callback<OnboardingTagResponse> {
            override fun onResponse(call: Call<OnboardingTagResponse>, response: Response<OnboardingTagResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.response?.let {
                        binding.tvHomeTagSubFirst.text = it[0].tagName
                        binding.tvHomeTagSubSecond.text = it[1].tagName
                        // TODO menus 추가
                        tagMenus = result.response
                    }
                }
            }

            override fun onFailure(call: Call<OnboardingTagResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initItemClickListener() {
        itemClickListener =
            object : HomeItemClickListener {
                override fun onItemClick(onboardingMenuData: OnboardingMenuData) {
                    val intent = Intent(activity, MenuInfoActivity::class.java)
                    // TODO 추가할 데이터 추가
                    intent.putExtra("tag", "menuInfo")
                    intent.putExtra("groupId", onboardingMenuData.groupId)
                    startActivity(intent)
                }
            }

        binding.ivHomeTitleAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initSubMenuRV() {
//        binding.rvHomeMenuSubFirst.adapter =
//            HomeMenuSubRVAdapter(tagMenus[0].menus).apply {
//                setOnItemClickListener(itemClickListener)
//            }
//        binding.rvHomeMenuSubSecond.adapter =
//            HomeMenuSubRVAdapter(tagMenus[1].menus).apply {
//                setOnItemClickListener(itemClickListener)
//            }

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
        dummyItems = ArrayList<OnboardingMenuData>()
        for (i in 1..6) {
            dummyItems.add(
                OnboardingMenuData(
                    menuImgUrl = "", menuTitle = "title $i", placeName = "place $i", groupId = 0
                )
            )
        }
    }

    private fun initMainMenuRV() {
//        binding.rvHomeMenuMain.adapter =
//            HomeMenuMainRVAdapter(responseMenus, requireContext()).apply {
//                setOnItemClickListener(itemClickListener)
//            }
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvHomeMenuMain)

        binding.rvHomeMenuMain.adapter =
            HomeMenuMainRVAdapter(mainMenuItems, requireContext()).apply {
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
                Log.d("of", offset.toString())
//                Log.d("sw", screenWidth.toString())
//                Log.d("wi", width.toString())
//                val offset = screenWidth!! * 56 / 360

                (binding.rvHomeMenuMain.layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(
                        ((1000 / dummyItems.size) * dummyItems.size) - 1,
                        offset
                    )
            }

        })


    }
}
