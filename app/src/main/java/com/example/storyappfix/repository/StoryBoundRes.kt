package com.example.storyappfix.repository

import android.util.Log
import com.example.storyappfix.network.Result
import kotlinx.coroutines.flow.*

abstract class StoryBoundRes<RequestType> {
    private var result: Flow<StoryResources<RequestType>> = flow {

        emit(StoryResources.Loading())
        when (val apiResponse = createCall().first()) {
            is Result.Success -> {
                getResponse(apiResponse.body)
                emit(StoryResources.Success(apiResponse.body))
            }
            is Result.Empty -> {}
            is Result.Error -> {
                emit(StoryResources.Error(apiResponse.errorMessage))
                Log.e("OnlineBoundRes", apiResponse.errorMessage)
            }
        }

    }

    protected abstract suspend fun createCall(): Flow<Result<RequestType>>

    protected abstract fun getResponse(data: RequestType)

    fun asFlow(): Flow<StoryResources<RequestType>> = result
}