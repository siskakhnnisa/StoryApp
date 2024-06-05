package com.example.storyappfix.paging


import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.ItemKeyedDataSource
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.liveData
import com.example.storyappfix.entity.StoryEntity
import com.example.storyappfix.room.DatabaseStories
import com.example.storyappfix.network.ApiService
import com.example.storyappfix.response.ListStoryItem
import kotlinx.coroutines.flow.Flow

class PagingSource(
    private val databaseStories: DatabaseStories,
    private val apiService: ApiService,
) {
    fun getStories(token: String):LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator =
            RemoteMediator(token, apiService, databaseStories)
            ,
            pagingSourceFactory = {
                databaseStories.dao().getPagingStories()
            }
        ).liveData
    }
}