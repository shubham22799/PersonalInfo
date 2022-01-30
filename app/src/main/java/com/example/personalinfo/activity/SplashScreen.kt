package com.example.personalinfo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.personalinfo.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    lateinit var bind: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(bind.root)
        
    }
}