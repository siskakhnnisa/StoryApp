package com.example.storyappfix.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyappfix.entity.StoryKeys
import com.example.storyappfix.entity.StoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): StoryKeys?

    @Query("DELETE FROM story_table")
    fun deleteStory()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<StoryKeys>)

    @Query("DELETE FROM remote_keys")
    suspend fun deleteRemoteKeys()

    @Query("SELECT * FROM story_table")
    fun getPagingStories(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM story_table")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(storyEntity: List<StoryEntity>)


}