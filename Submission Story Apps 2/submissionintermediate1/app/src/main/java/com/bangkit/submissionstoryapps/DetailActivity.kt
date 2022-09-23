package com.bangkit.submissionstoryapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.submissionstoryapps.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra(com.bangkit.submissionstoryapps.autentifikasi.data.ExtraData.EXTRA_NAME)
        val description = intent.getStringExtra(com.bangkit.submissionstoryapps.autentifikasi.data.ExtraData.EXTRA_DESCRIPTION)
        val avatar = intent.getStringExtra(com.bangkit.submissionstoryapps.autentifikasi.data.ExtraData.EXTRA_AVATAR)

        binding.apply {
            detailName.text = name
            detailDescription.text = description
            Glide.with(this@DetailActivity)
                .load(avatar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .into(imgDetail)
        }
        supportActionBar?.title = "Detail's User"
    }
}