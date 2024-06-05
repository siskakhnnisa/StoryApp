package com.example.storyappfix.di

import androidx.room.Room
import com.example.storyappfix.utils.SharedPreference
import com.example.storyappfix.repository.DataRepository
import com.example.storyappfix.datasource.DatasourceLocal
import com.example.storyappfix.room.DatabaseStories
import com.example.storyappfix.paging.PagingSource
import com.example.storyappfix.datasource.DatasourceRemote
import com.example.storyappfix.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val moduleDatabase = module {
    factory { get<DatabaseStories>().dao() }
    single {
        Room.databaseBuilder(
            androidContext(),
            DatabaseStories::class.java, "story_db"
        ).fallbackToDestructiveMigration().build()
    }
}

val moduleNetwork = module {
    single {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()


    }

    single {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

}

val moduleRepository = module {
    single { DatasourceRemote(get(), get()) }
    single { SharedPreference(get()) }
    single { DataRepository(get(), get(), get(), get()) }
    single { PagingSource(get(), get()) }
    single { DatasourceLocal(get()) }
}