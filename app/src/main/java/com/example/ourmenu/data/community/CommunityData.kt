package com.example.ourmenu.data.community

import java.io.Serializable

data class ArticleResponseData(
    val articleId: Int,
    val articleTitle: String,
    val userEmail : String,
    val userNickname: String,
    val userImgUrl: String,
    val createBy: String,
    val articleContent: String,
    val articleThumbnail: String,
    val articleViews: Int,
    val articleMenus: ArrayList<ArticleMenuData>
)

data class ArticleMenuData(
    val articleMenuId : Int,
    val placeTitle: String,
    val menuTitle: String,
    val menuPrice: Int,
    val menuImgUrl: String,
    val menuAddress: String,
    val sharedCount : Int,
    val menuMemoTitle : String,
    val menuIconType : String,
    val placeMemo : String,
    val placeLatitude : Int,
    val placeLongitude : Int
)

data class ArticleRequestData(
    val placeTitle: String,
    val menuTitle: String,
    val menuPrice: Int,
    val menuImgUrl: String,
    val menuAddress: String,
    val menuMemoTitle : String,
    val menuIconType : String,
    val placeMemo : String,
    val placeLatitude : Int,
    val placeLongitude : Int
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

data class CommunityMenuGroupId(
    val menuGroupId : Int
)
