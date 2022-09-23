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
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bangkit.submissionstoryapps.api.ApiConfig
import com.bangkit.submissionstoryapps.component.EmailEditText
import com.bangkit.submissionstoryapps.component.PassEditText
import com.bangkit.submissionstoryapps.component.RegisterButton
import com.bangkit.submissionstoryapps.databinding.ActivityRegisterBinding
import com.bangkit.submissionstoryapps.model.response.RegisterRequest
import com.bangkit.submissionstoryapps.model.response.RegisterResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    val registerResponse = MutableLiveData<RegisterResponse>()
    private lateinit var etEmail: EmailEditText
    private lateinit var etPass: PassEditText
    private lateinit var btnRegister: RegisterButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etEmail = binding.myEmailText
        etPass = binding.myPassword
        btnRegister = binding.registerButton

        etEmail.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setRegisterButton()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        etPass.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setRegisterButton()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.registerButton.setOnClickListener{
            registerUser()
        }

        binding.haveAccount.setOnClickListener {
            val moveLogin = Intent(this@RegisterActivity, LoginActivity::class.java)
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show()
            startActivity(moveLogin)
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

    private fun registerUser(){
        val request = RegisterRequest()
        request.name = binding.myUsername.text.toString()
        request.email = binding.myEmailText.text.toString()
        request.password = binding.myPassword.text.toString()

        val client =ApiConfig.getApiService().userRegister(request)
        client.enqueue(object : Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful){
                    registerResponse.postValue(response.body())
                    Log.e("success", "Register success")
                }else{
                    val error = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(applicationContext, error.getString("message"), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("Failed", t.message.toString())
            }
        })

        registerResponse.observe(this){
            if (it.error){
                Toast.makeText(applicationContext, "Created Account Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRegisterButton() {
        val email = etEmail.text.toString()
        if(etPass.length() >= 6 &&  Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            btnRegister.isEnabled = true
        }
    }
}