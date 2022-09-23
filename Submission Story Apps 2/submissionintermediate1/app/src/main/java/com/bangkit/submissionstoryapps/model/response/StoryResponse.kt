package com.bangkit.submissionstoryapps.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class StoryResponse (
    val error: Boolean,
    val message: String,
    val listStory: ArrayList<ListStory>
)
@Entity(tableName = "story")
data class ListStory(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("photoUrl")
    val photoUrl: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("lat")
    val lat: Float,
    @field:SerializedName("lon")
    val lon: Float
)