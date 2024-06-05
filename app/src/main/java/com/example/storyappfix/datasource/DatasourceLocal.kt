package com.example.storyappfix.datasource

import com.example.storyappfix.room.StoryDao


class DatasourceLocal (
    private val storyDao: StoryDao
) {
    fun getAllStories() = storyDao.getAllStories()
}