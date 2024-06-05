package com.example.storyappfix.datasource

import android.content.Context
import android.util.Log
import com.example.storyappfix.R
import com.example.storyappfix.response.GeneralResponse
import com.example.storyappfix.response.LoginResponse
import com.example.storyappfix.response.StoriesRespon
import com.example.storyappfix.network.Result
import com.example.storyappfix.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException

class DatasourceRemote(
    private val apiService: ApiService?,
    private val context: Context
) {

    suspend fun doLogin(email: String, password: String): Flow<Result<LoginResponse>> {
        return flow {
            try {
                val respon = apiService?.loginProcess(email, password)
                if (respon != null) {
                    emit(Result.Success(respon))
                } else {
                    emit(Result.Empty)
                }
            } catch (e: Exception) {
                emit(exceptionLog(e, "doLogin"))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun signupProcess(
        email: String,
        password: String,
        name: String
    ): Flow<Result<GeneralResponse>> {
        return flow {
            try {
                val response = apiService?.signupProcess(email, password, name)
                if (response != null) {
                    emit(Result.Success(response))
                } else {
                    emit(Result.Empty)
                }
            } catch (e: Exception) {
                emit(exceptionLog(e, "signupProcess"))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllStories(
        token: String,
        page: String?,
        size: String?,
        location: String?
    ): Flow<Result<StoriesRespon>> {
        return flow {
            try {
                val response = apiService?.getAllStories(token, page, size, location)
                if (response != null) {
                    emit(Result.Success(response))
                } else {
                    emit(Result.Empty)
                }
            } catch (e: Exception) {
                emit(exceptionLog(e, "getAllStories"))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: String,
        lat: Float,
        lon: Float
    ): Flow<Result<GeneralResponse>> {
        return flow {
            try {
                val respon = apiService?.addStory(token, file, description.toRequestBody("text/plain".toMediaType()), lat, lon)
                if (respon != null) {
                    emit(Result.Success(respon))
                } else {
                    emit(Result.Empty)
                }
            } catch (e: Exception) {
                emit(exceptionLog(e, "addNewStory"))
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun exceptionLog(e: Exception, tagLog: String): Result.Error {
        val tag = this::class.java.simpleName

        when (e) {
            is SocketTimeoutException -> {
                Log.e(tag, e.message.toString())
                return Result.Error(
                    e.message.toString() + ", " + context.resources.getString(
                        R.string.check_connection
                    )
                )
            }

            is HttpException -> {
                return try {
                    val `object` = JSONObject(e.response()?.errorBody()?.string().toString())
                    val messageString: String = `object`.getString("message")
                    Log.e(tag, messageString)
                    Result.Error(messageString)
                } catch (e: Exception) {
                    val messageString = context.resources.getString(R.string.something_went_wrong)
                    Log.e(tag, messageString)
                    Result.Error(messageString)
                }
            }

            is NoSuchElementException -> {
                Log.e(tag, e.message.toString())
                return (Result.Error(
                    e.message.toString() + ", " + context.resources.getString(
                        R.string.check_connection
                    )
                ))
            }

            else -> {
                val messageString = context.resources.getString(R.string.something_went_wrong)
                Log.e(tag, e.message.toString())
                return Result.Error(messageString)
            }
        }
    }
}