package com.example.storyappfix.network

import com.example.storyappfix.response.GeneralResponse
import com.example.storyappfix.response.LoginResponse
import com.example.storyappfix.response.StoriesRespon
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun loginProcess(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun signupProcess(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): GeneralResponse


    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: String? = null,
        @Query("size") size: String? = null,
        @Query("location") location: String? = null,
    ): StoriesRespon

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float
    ): GeneralResponse
}