package com.bangkit.submissionstoryapps.autentifikasi

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bangkit.submissionstoryapps.HomeActivity
import com.bangkit.submissionstoryapps.R
import com.bangkit.submissionstoryapps.api.ApiConfig
import com.bangkit.submissionstoryapps.autentifikasi.data.Preferences
import com.bangkit.submissionstoryapps.component.EmailEditText
import com.bangkit.submissionstoryapps.component.LoginButton
import com.bangkit.submissionstoryapps.component.PassEditText
import com.bangkit.submissionstoryapps.databinding.ActivityLoginBinding
import com.bangkit.submissionstoryapps.model.response.LoginRequest
import com.bangkit.submissionstoryapps.model.response.LoginResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: Preferences
    private lateinit var etEmail: EmailEditText
    private lateinit var etPass: PassEditText
    private lateinit var btnLogin: LoginButton
    val loginResponse = MutableLiveData<LoginResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etEmail = binding.myEmailText
        etPass = binding.myPassword
        btnLogin = binding.myButton
        sharedPreferences = Preferences(this)

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setLoginButton()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        etPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setLoginButton()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.myButton.setOnClickListener {
            login()
        }

        val goRegister: TextView = findViewById(R.id.register_text)
        goRegister.setOnClickListener {
            val moveRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(moveRegister)
            Toast.makeText(this, "Create your Account", Toast.LENGTH_SHORT).show()
        }

        setupView()

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

    private fun login(){
        val request = LoginRequest()
        request.email = binding.myEmailText.text.toString()
        request.password = binding.myPassword.text.toString()

        val client = ApiConfig.getApiService().userLogin(request)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    val userResponse = response.body()
                    if (userResponse != null){
                        Log.e("success", "Login successfully ${userResponse.message}")
                    }
                    Log.e("success", "Success")
                    loginResponse.postValue(response.body())
                }else{
                    val error = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(applicationContext, error.getString("message"), Toast.LENGTH_SHORT).show()
                    Log.e("Failed", "Failed to Sign Up")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("Failed", t.message.toString())
            }

        })

        loginResponse.observe(this){
            if (!it.error){
                it.loginResult?.let {
                        its -> sharedPreferences.input(its.name, it.loginResult.token)
                }
                sharedPreferences.inputLogin(com.bangkit.submissionstoryapps.autentifikasi.data.ExtraData.IS_LOGIN, true)
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
                Toast.makeText(applicationContext, "Your Logged", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun setLoginButton(){
        val email = etEmail.text.toString()
        if (etPass.length() >= 6 && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            btnLogin.isEnabled = true
        }
    }
}