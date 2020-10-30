package com.aut.covid.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.aut.covid.R
import com.aut.covid.activity.SplashActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val secondsDelayed = 1
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, secondsDelayed * TIMEOUT.toLong())
    }

    companion object {
        private const val TIMEOUT = 3000
    }
}