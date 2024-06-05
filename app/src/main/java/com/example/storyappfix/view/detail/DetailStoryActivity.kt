package com.example.storyappfix.view.detail

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.storyappfix.R
import com.example.storyappfix.utils.SharedPreference
import com.example.storyappfix.entity.StoryEntity
import com.example.storyappfix.databinding.ActivityDetailStoryBinding
import com.example.storyappfix.view.main.MainStoryAdapter
import com.example.storyappfix.view.login.LoginUserActivity
import com.example.storyappfix.view.maps.MapsActivity

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.show()

        val extras = intent.extras
        val data = extras?.getParcelable<StoryEntity>(MainStoryAdapter.EXTRA_STORY)

        with(binding) {
            data?.apply {
                supportActionBar?.title = name
                tvUsername.text = name
                tvDescription.text = description
                Glide.with(this@DetailStoryActivity)
                    .load(photoUrl)
                    .into(imgPosting)
            }
            btnShare.setOnClickListener {
                val shareUser = Intent(Intent.ACTION_SEND)
                shareUser.type = "text/plain"
                data?.apply {
                    val textOnShare = "This Story Name = $name, Created at = ${createdAt.toString().removeRange(16, createdAt.toString().length)}" +
                            ", Description = $description, Image = $photoUrl"
                    shareUser.putExtra(Intent.EXTRA_TEXT, textOnShare)
                    startActivity(Intent.createChooser(shareUser, "Share Via"))
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.languageSetting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.action_logout -> {
                Intent(this@DetailStoryActivity, LoginUserActivity::class.java)
                    .apply {
                        startActivity(this)
                        finishAffinity()
                        SharedPreference(this@DetailStoryActivity).clearPreference()
                    }
            }
            R.id.btnmaps -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return true
    }
}