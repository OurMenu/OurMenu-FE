package com.example.ourmenu.data.community

data class CommunityArticleRequest(
    val articleTitle : String,
    val articleContent : String,
    val articleMenus : ArrayList<ArticleRequestData>
)
