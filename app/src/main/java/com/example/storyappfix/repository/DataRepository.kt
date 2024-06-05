package com.example.storyappfix.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.storyappfix.utils.SharedPreference
import com.example.storyappfix.datasource.DatasourceLocal
import com.example.storyappfix.entity.StoryEntity
import com.example.storyappfix.paging.PagingSource
import com.example.storyappfix.datasource.DatasourceRemote
import com.example.storyappfix.response.GeneralResponse
import com.example.storyappfix.response.LoginResponse
import com.example.storyappfix.network.Result
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

class DataRepository(
    private val datasourceRemote: DatasourceRemote,
    private val sharedPreference: SharedPreference,
    private val pagingSource: PagingSource,
    private val datasourceLocal: DatasourceLocal
): DataRepositoryImpl {
    override fun loginProcess(email: String, password: String): Flow<StoryResources<LoginResponse>> {
        return object : StoryBoundRes<LoginResponse>() {
            override suspend fun createCall(): Flow<Result<LoginResponse>> {
                return datasourceRemote.doLogin(email, password)
            }

            override fun getResponse(data: LoginResponse) {
                sharedPreference.setToken(data.resultLogin?.token)
                sharedPreference.setUserId(data.resultLogin?.userId)
                sharedPreference.setName(data.resultLogin?.name)
            }
        }.asFlow()
    }

    override fun signupProcess(
        email: String,
        password: String,
        name: String
    ): Flow<StoryResources<GeneralResponse>> {
        return object : StoryBoundRes<GeneralResponse>() {
            override suspend fun createCall(): Flow<Result<GeneralResponse>> {
                return datasourceRemote.signupProcess(email, password, name)
            }

            override fun getResponse(data: GeneralResponse) {

            }
        }.asFlow()
    }

    override fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: String,
        lat: Float,
        lon: Float
    ): Flow<StoryResources<GeneralResponse>> {
        return object : StoryBoundRes<GeneralResponse>() {
            override suspend fun createCall(): Flow<Result<GeneralResponse>> {
                return datasourceRemote.addStory(token, file, description, lat, lon)
            }
            override fun getResponse(data: GeneralResponse) {
            }
        }.asFlow()
    }

    override fun getAllStories(token: String): LiveData<PagingData<StoryEntity>> {
        return pagingSource.getStories(token)
    }

    override fun getLocalStories(): Flow<List<StoryEntity>> {
        return datasourceLocal.getAllStories()
    }
}