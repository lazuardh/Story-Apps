package com.bangkit.submissionstoryapps.autentifikasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.submissionstoryapps.model.response.*
import com.bangkit.submissionstoryapps.repository.StoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AuthViewModel(private val storyRepository: StoryRepository): ViewModel() {
    val uploadImageResponse = MutableLiveData<UploadResponse>()
    val storyResponse = MutableLiveData<StoryResponse>()

    fun getStoryLocation(token: String){
        viewModelScope.launch {
            storyResponse.postValue(storyRepository.getStoryLocation(token))
        }
    }

    fun getStory(token: String): LiveData<PagingData<ListStory>> {
        return storyRepository.getStory(token).cachedIn(viewModelScope)
    }

    fun uploadStory(token:String, file: MultipartBody.Part, description: String, lat:Float, lon:Float){
        viewModelScope.launch {
            uploadImageResponse.postValue(storyRepository.uploadStory(token, file, description, lat, lon))
        }
    }
}