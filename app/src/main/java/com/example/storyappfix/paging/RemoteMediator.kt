package com.example.storyappfix.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storyappfix.entity.StoryKeys
import com.example.storyappfix.entity.StoryEntity
import com.example.storyappfix.room.DatabaseStories
import com.example.storyappfix.network.ApiService

@OptIn(ExperimentalPagingApi::class)
class RemoteMediator(
    private val token: String,
    private val apiService: ApiService,
    private val databaseStories: DatabaseStories
): RemoteMediator<Int, StoryEntity>() {
    override suspend fun load(
        loadType: LoadType,
        pagingState: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val pages = when (loadType) {
            LoadType.REFRESH ->{
                val remoteKeys = getClosestRemoteKeyToCurrentPosition(pagingState)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstItemRemoteKey(pagingState)
                val previousKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                previousKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastItemRemoteKey(pagingState)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getAllStories("Bearer $token", pages.toString(), pagingState.config.pageSize.toString(), "1").listStory
            val endOfPaginationReached = responseData?.isEmpty() ?: true

            databaseStories.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    databaseStories.dao().deleteRemoteKeys()
                    databaseStories.dao().deleteStory()
                }
                val previousKey = if (pages == 1) null else pages - 1
                val nextKey = if (endOfPaginationReached) null else pages + 1
                val keys = responseData?.map {
                    StoryKeys(id = it.id.toString(), prevKey = previousKey, nextKey = nextKey)
                }
                keys?.let { databaseStories.dao().insertAll(keys) }
                val storyMap = responseData?.map {
                    with(it) {
                        StoryEntity(
                            photoUrl, createdAt, name, description, lon, id.toString(), lat
                        )
                    }
                }.orEmpty()
                databaseStories.dao().insertStory(storyMap)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getLastItemRemoteKey(state: PagingState<Int, StoryEntity>): StoryKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            databaseStories.dao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getFirstItemRemoteKey(state: PagingState<Int, StoryEntity>): StoryKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            databaseStories.dao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getClosestRemoteKeyToCurrentPosition(state: PagingState<Int, StoryEntity>): StoryKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                databaseStories.dao().getRemoteKeysId(id)
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


}