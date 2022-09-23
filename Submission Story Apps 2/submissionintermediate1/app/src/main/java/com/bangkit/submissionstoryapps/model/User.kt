package com.bangkit.submissionstoryapps.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var token: String? = ""
):Parcelable
