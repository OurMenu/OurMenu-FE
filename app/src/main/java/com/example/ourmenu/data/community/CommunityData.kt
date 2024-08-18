package com.example.ourmenu.data.community

import java.io.Serializable

data class ArticleResponseData(
    val articleId: Int,
    val articleTitle: String,
    val userNickname: String,
    val userImgUrl: String,
    val createBy: String,
    val articleContent: String,
    val articleThumbnail: String,
    val articleViews: Int,
    val articleMenus: ArrayList<ArticleMenuData>
)

data class ArticleMenuData(
    val menuTitle: String,
    val menuPrice: Int,
    val menuImgUrl: String,
    val menuAddress: String
)

data class ArticleRequestData(
    val placeTitle: String,
    val menuTitle: String,
    val menuPrice: Int,
    val menuImgUrl: String,
    val menuAddress: String
)

data class CommunityResponseData(
    val articleId: Int,
    val articleTitle: String,
    val articleContent: String,
    val userNickname: String,
    val userImgUrl: String?,
    val createBy: String?,
    val menusCount: Int,
    val articleViews: Int,
    val articleThumbnail: String?
) : Serializable

