package com.example.team29v2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashScreen: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        val animasjon = AnimationUtils.loadAnimation(this, R.anim.welcome_animation)
        findViewById<TextView>(R.id.welcome).animation = animasjon

        val SPLASH_TIME_OUT = 4000;
        val homeIntent = Intent(this@SplashScreen, MainActivity::class.java)

        Handler().postDelayed({
            startActivity(homeIntent)
            finish()
        },SPLASH_TIME_OUT.toLong())
    }
}