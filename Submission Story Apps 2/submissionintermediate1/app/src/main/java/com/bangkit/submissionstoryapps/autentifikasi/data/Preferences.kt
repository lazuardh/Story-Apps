package com.bangkit.submissionstoryapps.autentifikasi.data

import android.content.Context
import android.content.SharedPreferences

class Preferences(context: Context) {
    private val keyPref = "loginData"
    private val pref: SharedPreferences = context.getSharedPreferences(keyPref, Context.MODE_PRIVATE)
    val editor :SharedPreferences.Editor = pref.edit()

    fun input(name: String, token: String){
        editor.apply{
            putString(ExtraData.NAME, name)
            putString(ExtraData.TOKEN,token)
        }
    }

    fun inputLogin(key: String, isLogin: Boolean){
        editor.putBoolean(key, isLogin)
            .apply()
    }

    fun getLogin(key: String) : Boolean{
        return pref.getBoolean(key, false)
    }

    fun getToken(key: String): String? {
        return pref.getString(key, null)
    }

    fun clear(){
        editor.clear().apply()
    }
}