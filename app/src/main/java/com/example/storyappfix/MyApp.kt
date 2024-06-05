package com.example.storyappfix

import android.app.Application
import com.example.storyappfix.di.moduleDatabase
import com.example.storyappfix.di.moduleNetwork
import com.example.storyappfix.di.moduleRepository
import com.example.storyappfix.di.viewModelMod
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(
                listOf(
                    moduleNetwork,
                    moduleRepository,
                    viewModelMod,
                    moduleDatabase
                )
            )
        }
    }
}