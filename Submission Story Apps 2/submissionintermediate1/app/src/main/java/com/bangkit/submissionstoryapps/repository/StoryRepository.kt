package com.bangkit.submissionstoryapps.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bangkit.submissionstoryapps.api.ApiService
import com.bangkit.submissionstoryapps.model.response.ListStory
import com.bangkit.submissionstoryapps.model.response.StoryResponse
import com.bangkit.submissionstoryapps.model.response.UploadResponse
import okhttp3.MultipartBody

class StoryRepository (private val apiService: ApiService){
    fun getStory(token: String): LiveData<PagingData<ListStory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                PagingSource(apiService, token)
            }
        ).liveData
    }

    suspend fun getStoryLocation(token: String): StoryResponse {
        return apiService.getStory(token, 1)
    }

    suspend fun uploadStory(token:String, file: MultipartBody.Part, description: String, lat:Float, lon:Float): UploadResponse {
        return apiService.uploadImage(token, file, description, lat, lon)
    }
}