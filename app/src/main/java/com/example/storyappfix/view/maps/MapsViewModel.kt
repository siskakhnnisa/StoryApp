package com.example.storyappfix.view.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyappfix.repository.DataRepository

class MapsViewModel(private val dataRepository: DataRepository): ViewModel() {
    fun getAllStories() = dataRepository.getLocalStories().asLiveData()
}