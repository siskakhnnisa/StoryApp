package com.example.storyappfix.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyappfix.entity.StoryEntity
import com.example.storyappfix.paging.PagingSource

class MainStoryViewModel(private val dataRepository: PagingSource) : ViewModel() {

    val quote: LiveData<PagingData<StoryEntity>> =
        dataRepository.getStories("token").cachedIn(viewModelScope)
    fun getAllStories(token: String) = dataRepository.getStories(token)
}