package com.example.storyappfix.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.storyappfix.entity.StoryEntity
import com.example.storyappfix.response.GeneralResponse
import com.example.storyappfix.response.LoginResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface DataRepositoryImpl {

    fun loginProcess(email: String, password: String): Flow<StoryResources<LoginResponse>>

    fun signupProcess(email: String, password: String, name: String): Flow<StoryResources<GeneralResponse>>

    fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: String,
        lat: Float,
        lon: Float
    ): Flow<StoryResources<GeneralResponse>>

    fun getAllStories(token: String): LiveData<PagingData<StoryEntity>>

    fun getLocalStories(): Flow<List<StoryEntity>>
}