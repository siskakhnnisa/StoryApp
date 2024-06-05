package com.example.storyappfix.network

sealed class Result<out R> {
    data class Success<out T>(val body: T) : Result<T>()
    data class Error(val errorMessage: String) : Result<Nothing>()
    object Empty : Result<Nothing>()
}