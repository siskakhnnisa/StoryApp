package com.example.storyappfix.repository

sealed class StoryResources<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : StoryResources<T>(data)
    class Loading<T>(data: T? = null) : StoryResources<T>(data)
    class Error<T>(message: String, data: T? = null) : StoryResources<T>(data, message)
}