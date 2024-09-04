package com.example.ourmenu.data

import java.io.Serializable

// 테스트용 더미 데이터 API 사용 X
data class DummyMenuData(
    val imageUrl : String ?= "",
    val menu : String ?= "0",
    val title : String ?= "서울숲 맛집",
    val menuCount : Int ? = 0,
    val store : String ?= "",
    val address: String ?= ""
) : Serializable
