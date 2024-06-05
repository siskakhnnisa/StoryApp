package com.example.storyappfix.di

import com.example.storyappfix.view.add.AddStoryViewModel
import com.example.storyappfix.view.main.MainStoryViewModel
import com.example.storyappfix.view.login.LoginUserViewModel
import com.example.storyappfix.view.maps.MapsViewModel
import com.example.storyappfix.view.signup.SignupUserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelMod = module {
    viewModel { LoginUserViewModel(get()) }
    viewModel { SignupUserViewModel(get()) }
    viewModel { MainStoryViewModel(get()) }
    viewModel { AddStoryViewModel(get()) }
    viewModel { MapsViewModel(get()) }
}