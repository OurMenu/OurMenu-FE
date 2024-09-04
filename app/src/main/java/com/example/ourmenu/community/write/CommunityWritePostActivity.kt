package com.example.ourmenu.community.write

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmenu.R
import com.example.ourmenu.community.CommunityPostFragment
import com.example.ourmenu.databinding.ActivityCommunityWritePostBinding
import com.example.ourmenu.retrofit.NetworkModule

class CommunityWritePostActivity : AppCompatActivity() {

    lateinit var binding: ActivityCommunityWritePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommunityWritePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        NetworkModule.initialize(applicationContext)

        checkFlag()


    }

    private fun checkFlag() {
        val flag = intent.getStringExtra("flag") ?: "post"
        when (flag) {
            "post" -> {
                Log.d("오류",intent.extras.toString())
                val bundle = Bundle()
                val postData = intent.getSerializableExtra("postData")
                val isMine = intent.getBooleanExtra("isMine", false)
                bundle.putSerializable("articleData", postData)
                bundle.putInt("articleId",intent.getIntExtra("ArticleId",0))
                val communityPostFragment = CommunityPostFragment(isMine)
                communityPostFragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.community_post_frm, communityPostFragment)
                    .commitAllowingStateLoss()
            }

            "write" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.community_post_frm, CommunityWritePostFragment())
                    .commitAllowingStateLoss()
            }
        }
    }
}
