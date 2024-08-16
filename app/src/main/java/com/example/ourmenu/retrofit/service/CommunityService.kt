package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.community.ArticleResponse
import com.example.ourmenu.data.community.CommunityArticleRequest
import com.example.ourmenu.data.community.CommunityResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityService {

    @GET("community/article/{articleId}")
    fun getCommunityArticle(
        @Path("articleId") articleId: Int
    ): Call<ArticleResponse>

    @PUT("community/article/{articleId}")
    fun putCommunityArticle(
        @Path("articleId") articleId: Int,
        @Body body : CommunityArticleRequest
    ):Call<ArticleResponse>

    @POST("community/article")
    fun postCommunityArticle(
        @Body body : CommunityArticleRequest
    ):Call<ArticleResponse>

    @GET("community/community")
    fun getCommunity(
        @Query("title") title : String,
        @Query("page") page : Int = 0,
        @Query("size") size : Int = 5,
        @Query("orderCriteria") orderCriteria : String = "CREATED_AT_DESC"
    ) : Call<CommunityResponse>
}
