package com.example.storyappfix.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyappfix.repository.DataRepository

class LoginUserViewModel (private val dataRepository: DataRepository) : ViewModel() {
    private var email = ""
    private var password = ""

    fun setLoginParam(email: String, password: String) {
        this.email = email
        this.password = password
    }

    fun loginProcess() = dataRepository.loginProcess(email, password).asLiveData()
}