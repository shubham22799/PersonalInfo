package com.example.personalinfo.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.personalinfo.R
import com.example.personalinfo.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.btnLogin.setOnClickListener {
//            submit()
//        }
//        binding.btnSignup.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }
        binding.btnLogin.setOnClickListener(this)
        binding.btnSignup.setOnClickListener(this)


    }

    private fun submit() {
        if(binding.etEmail.text.isEmpty()){
            Toast.makeText(this, "Empty Email id field", Toast.LENGTH_SHORT).show()
        }else {
            val i = Intent(this, LandingPage::class.java)
            val data = binding.etEmail.text.toString()
            i.putExtra("Data", data)
            startActivity(i)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnLogin ->{submit()}
            R.id.btnSignup ->{startActivity(Intent(this, RegisterActivity::class.java))}

        }
    }
}