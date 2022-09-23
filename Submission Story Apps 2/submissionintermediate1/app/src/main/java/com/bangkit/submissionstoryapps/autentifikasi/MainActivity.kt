package com.bangkit.submissionstoryapps.autentifikasi

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bangkit.submissionstoryapps.HomeActivity
import com.bangkit.submissionstoryapps.api.ApiConfig
import com.bangkit.submissionstoryapps.R
import com.bangkit.submissionstoryapps.autentifikasi.data.ExtraData
import com.bangkit.submissionstoryapps.autentifikasi.data.Preferences
import com.bangkit.submissionstoryapps.component.EmailEditText
import com.bangkit.submissionstoryapps.component.LoginButton
import com.bangkit.submissionstoryapps.component.PassEditText
import com.bangkit.submissionstoryapps.databinding.ActivityMainBinding
import com.bangkit.submissionstoryapps.model.response.LoginRequest
import com.bangkit.submissionstoryapps.model.response.LoginResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = Preferences(this)

        setupView()
        setupAction()
        playAnimation()

    }
    private fun setupView(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 600
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(login, register)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }

    override fun onStart() {
        super.onStart()
        if (preferences.getLogin(com.bangkit.submissionstoryapps.autentifikasi.data.ExtraData.IS_LOGIN)){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}