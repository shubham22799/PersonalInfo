package com.example.personalinfo.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.personalinfo.R
import com.example.personalinfo.databinding.ActivityMainBinding
import com.example.personalinfo.utility.ContractPersonalInfo
import com.example.personalinfo.utility.SharedPreferencesManager
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener(this)
        binding.btnSignup.setOnClickListener(this)
    }

    private fun submit(view: View) {
        var emailIdFromEditText = binding.etEmail.text.toString().trim()
        var passwordFromEditText = binding.etPassword.text.toString().trim()

        var emailFromSP = SharedPreferencesManager.getEmailId()
        var passwordFromSP = SharedPreferencesManager.getPassword()

        if (emailIdFromEditText.isEmpty()) {
            onSnack(
                view,
                ContextCompat.getColor(this,  R.color.error_text),
                ContractPersonalInfo.emptyEmailId
            )
            Log.v("spinner", emailIdFromEditText)

            return
        }
        if (passwordFromEditText.isEmpty()) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.emptyPassword
            )
            Log.v("spinner", passwordFromEditText)

            return
        }
        if (emailIdFromEditText != emailFromSP) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.wrongEmailorPassword
            )
            Log.v("spinner", emailFromSP.toString())

            return
        }
        if (passwordFromEditText != passwordFromSP) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.wrongEmailorPassword
            )
            Log.v("spinner", passwordFromSP.toString())

            return
        }

        startActivity(Intent(this, LandingPage::class.java))
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnLogin ->{submit(v)}
            R.id.btnSignup ->{startActivity(Intent(this, RegisterActivity::class.java))}

        }
    }
    private fun onSnack(view: View, texColor: Int, errorMessage: String) {
        val snackBar =
            Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).setAction("Action", null)
        val snackBarView = snackBar.view
        //       snackBarView.setBackgroundColor(getColor(R.color.light_gray))
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        val textView =
            snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(texColor)
        textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        textView.textSize = 14f

        snackBar.show()
    }
}