package com.bangkit.submissionstoryapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.submissionstoryapps.adapter.AdapterStory
import com.bangkit.submissionstoryapps.adapter.LoadingStateAdapter
import com.bangkit.submissionstoryapps.autentifikasi.AddStory
import com.bangkit.submissionstoryapps.autentifikasi.AuthViewModel
import com.bangkit.submissionstoryapps.autentifikasi.MainActivity
import com.bangkit.submissionstoryapps.autentifikasi.ViewModelFactory
import com.bangkit.submissionstoryapps.autentifikasi.data.Preferences
import com.bangkit.submissionstoryapps.databinding.ActivityHomeBinding
import com.bangkit.submissionstoryapps.maps.MapsActivity
import com.bangkit.submissionstoryapps.model.response.ListStory

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var preferences: Preferences
    private val authViewModel: AuthViewModel by viewModels {ViewModelFactory(this)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = Preferences(this)

        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        getStory()

        binding.fab.setOnClickListener{
            addStory()
        }

        supportActionBar?.title = "STORY"
    }
    private fun addStory(){
        val story = Intent(this, AddStory::class.java)
        startActivity(story)
    }

    private fun getStory(){
        val adapter = AdapterStory(object : AdapterStory.OnItemClickCallback{
            override fun onItemClicked(data: ListStory) {
                Intent(this@HomeActivity, DetailActivity::class.java).also {
                    it.putExtra(com.bangkit.submissionstoryapps.autentifikasi.data.ExtraData.EXTRA_NAME, data.name)
                    it.putExtra(com.bangkit.submissionstoryapps.autentifikasi.data.ExtraData.EXTRA_DESCRIPTION, data.description)
                    it.putExtra(com.bangkit.submissionstoryapps.autentifikasi.data.ExtraData.EXTRA_AVATAR, data.photoUrl)
                    startActivity(it)
                }
            }
        })
        val token:String = preferences.getToken(com.bangkit.submissionstoryapps.autentifikasi.data.ExtraData.TOKEN).toString()
        binding.rvUsers.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        authViewModel.getStory(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.maps -> {
                val moveMap = Intent(this, MapsActivity::class.java)
                startActivity(moveMap)
                Toast.makeText(this, "Your Maps", Toast.LENGTH_SHORT).show()
            }
            R.id.logout -> {
                AlertDialog.Builder(this)
                    .setTitle("Apakah Anda yakin?")
                    .setMessage("Do you want to logout")
                    .setPositiveButton("Yes") { _, _ -> logout()
                    Toast.makeText(applicationContext, "Logout Success", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { _, _ ->}
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout(){
        preferences.clear()
        val logoutUser = Intent(this,MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(logoutUser)
        finish()
    }
}