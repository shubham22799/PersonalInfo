package com.example.personalinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.personalinfo.databinding.ActivityLandingPageBinding

class LandingPage : AppCompatActivity() {
    private lateinit var binding: ActivityLandingPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = this.intent.getStringExtra("Data")
        binding.textView.setText(data)
    }
}