package com.example.storyappfix.view.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyappfix.repository.DataRepository

    class SignupUserViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private var name = ""
    private var email = ""
    private var password = ""

    fun setRegisterParam(name: String, email: String, password: String) {
        this.name = name
        this.email = email
        this.password = password
    }
    fun signupProcess() = dataRepository.signupProcess(email, password, name).asLiveData()
}