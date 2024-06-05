package com.example.storyappfix.view.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.storyappfix.databinding.ActivitySplashBinding
import com.example.storyappfix.view.main.MainStoryActivity
import android.os.Handler
import android.os.Looper
import com.example.storyappfix.utils.SharedPreference
import com.example.storyappfix.view.login.LoginUserActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LoginSession()
    }

    private fun LoginSession() {
        val sharedPref = SharedPreference(this@SplashActivity)
        if (sharedPref.getToken() != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                goToHomeActivity()
            }, 3000)
        } else {
            goToLoginActivity()
        }
    }

    private fun goToLoginActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this@SplashActivity, LoginUserActivity::class.java)
                .apply {
                    startActivity(this)
                    finish()
                }
        }, 3000)
    }

    private fun goToHomeActivity() {
        Intent(this@SplashActivity, MainStoryActivity::class.java)
            .apply {
                startActivity(this)
                finish()
            }
    }
}
