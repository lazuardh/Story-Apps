package com.bangkit.submissionstoryapps.autentifikasi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bangkit.submissionstoryapps.HomeActivity
import com.bangkit.submissionstoryapps.autentifikasi.data.Preferences
import com.bangkit.submissionstoryapps.autentifikasi.data.reduceFileImage
import com.bangkit.submissionstoryapps.autentifikasi.data.uriToFile
import com.bangkit.submissionstoryapps.databinding.ActivityAddStoryBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AddStory : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null
    private lateinit var preferences: Preferences
    private lateinit var etDescription : EditText
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        preferences = Preferences(this)
        if (!allPermissionsGranted()){
            ActivityCompat.requestPermissions(this,
                REQUIRED_PERMISSIONS,
                REQUEST_PERMISSIONS_CODE
            )
        }
        etDescription = binding.tvDescription
        etDescription.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setUploadButton()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.btnCamera.setOnClickListener {
            startTakePhoto()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.btnUpload.setOnClickListener {
            uploadStory()
        }
    }
    private fun startGallery(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser =Intent.createChooser(intent, "Choose a picture")
        launcherIntentGallery.launch(chooser)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto(){
        val intent =Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        com.bangkit.submissionstoryapps.autentifikasi.data.createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStory,
                "com.bangkit.submissionstoryapps",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(myFile.path)

            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@AddStory)

            getFile = myFile

            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun uploadStory() {
        val token : String? = preferences.getToken(com.bangkit.submissionstoryapps.autentifikasi.data.ExtraData.TOKEN)
        if (getFile != null){
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val lat = location.latitude.toFloat()
                        val lon = location.longitude.toFloat()
                        val file = reduceFileImage(getFile as File)
                        val desc = binding.tvDescription.text.toString()
                        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
                        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "photo",
                            file.name,
                            requestImageFile
                        )

                        authViewModel.uploadStory("Bearer $token", imageMultipart, desc, lat, lon)
                        authViewModel.uploadImageResponse.observe(this){
                            if (!it.error){
                                Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@AddStory, HomeActivity::class.java))
                                finish()
                            }else{
                                Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Location is not found. Try Again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }else{
            Toast.makeText(applicationContext, "Insert a Picture First", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lat = location.latitude.toString()
                    val lon = location.longitude.toString()
                    Log.d("latitude", lat)
                    Log.d("longitude", lon)
                } else {
                    Toast.makeText(
                        this,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                }
            }
        }

    private fun setUploadButton() {
        val  desc = binding.tvDescription.text
        binding.btnUpload.isEnabled = desc.isNotEmpty()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, "Couldn't Get Permission",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it)== PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_PERMISSIONS_CODE = 10
    }
}