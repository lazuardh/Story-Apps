package com.bangkit.submissionstoryapps.model.response

data class LoginResponse (
    val error : Boolean,
    val message : String,
    val loginResult : LoginResult? = null
)
data class LoginResult(
    val userId : String,
    val name : String,
    val token : String
)