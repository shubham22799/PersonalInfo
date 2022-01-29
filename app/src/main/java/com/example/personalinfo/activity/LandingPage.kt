package com.example.personalinfo.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.personalinfo.databinding.ActivityLandingPageBinding
import com.example.personalinfo.utility.ContractPersonalInfo

class LandingPage : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityLandingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = this.intent.getStringExtra("Data")
        binding.textView.setText(data)


    }
}