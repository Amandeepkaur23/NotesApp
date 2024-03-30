package com.example.notesapp.di

import android.content.Context
import com.example.notesapp.api.AuthInterceptor
import com.example.notesapp.api.NoteAPI
import com.example.notesapp.api.UserAPI
import com.example.notesapp.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
    }

    @Singleton
    @Provides
    fun provideUserApi(retrofitBuilder: Retrofit.Builder): UserAPI{
        return retrofitBuilder.build().create(UserAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient{
        return OkHttpClient.Builder().addInterceptor(authInterceptor).build()
    }

    @Singleton
    @Provides
    fun provideNoteApi(retrofitBuilder: Builder, okHttpClient: OkHttpClient): NoteAPI{
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(NoteAPI::class.java)
    }
    @Provides
    fun provideAppContext( @ApplicationContext appContext: Context): Context{
        return appContext
    }
}