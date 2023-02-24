package com.gdsc.fourcutalbum

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.gdsc.fourcutalbum.databinding.ActivityMainBinding
import com.gdsc.fourcutalbum.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        findViewById<LinearLayout>(R.id.splash).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            handler?.removeCallbacksAndMessages(null)
        }
        setInitialize()
    }

    private fun setInitialize() {
        handler = Handler(Looper.getMainLooper())
        handler?.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }
}