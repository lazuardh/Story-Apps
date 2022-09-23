package com.bangkit.submissionstoryapps.di

import android.content.Context
import com.bangkit.submissionstoryapps.api.ApiConfig
import com.bangkit.submissionstoryapps.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository(apiService)
    }
}