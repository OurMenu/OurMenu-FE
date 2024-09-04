package com.example.ourmenu.data

import java.io.Serializable

data class PostData(
    var title: String,
    var content: String,
    var profileImg: Int, // url(string?)로 받아와야함
    var username: String,
    val time: String, // 수정?
    var viewCount: Int,
    var thumbnail: Int, // url로 받아옴
    var menuCount: Int,
) : Serializable
