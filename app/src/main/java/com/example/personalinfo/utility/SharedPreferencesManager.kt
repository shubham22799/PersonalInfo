package com.example.personalinfo.utility


import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object SharedPreferencesManager {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private const val sharedPreferenceName: String = "mySharedPreference"
    private const val imageUrl: String = "image"
    private const val userName: String = "name"
    private const val emailId: String = "email"
    private const val password: String = "password"
    private const val location: String = "location"
    private const val gender: String = "gender"
    private const val course: String = "course"
    private const val age: String = "age"
    private const val dob: String = "dob"
    private const val state: String = "state"

    private const val defImage: String = "Url"
    private const val defUserName: String = "User Name"
    private const val defEmailId: String = "Email Id"
    private const val defPassword: String = "Password"
    private const val defLocation: String = "location"
    private const val defGender: String = "Gender"
    private const val defCourse: String = "Course"
    private const val defAge: String = "Age"
    private const val defDOB: String = "Dob"
    private const val defState: String = "State"


    fun createPreference(context: Context) {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun getSharedPreference(): SharedPreferences {
        return sharedPreferences
    }

    fun getImage(): String? {
        return sharedPreferences.getString(imageUrl, defImage)
    }
    fun getUserName(): String? {
        return sharedPreferences.getString(userName, defUserName)
    }
    fun getEmailId(): String? {
        return sharedPreferences.getString(emailId, defEmailId)
    }
    fun getPassword(): String? {
        return sharedPreferences.getString(password, defPassword)
    }
    fun getGender(): String? {
        return sharedPreferences.getString(gender, defGender)
    }
    fun getLocation(): String? {
        return sharedPreferences.getString(location, defLocation)
    }
    fun getCourse(): String? {
        return sharedPreferences.getString(course, defCourse)
    }
    fun getAge(): String? {
        return sharedPreferences.getString(age, defAge)
    }
    fun getDOB(): String? {
        return sharedPreferences.getString(dob, defImage)
    }

    fun getState(): String? {
        return sharedPreferences.getString(state, defState)
    }
    fun putImage(string: String) {
        editor.putString(imageUrl, string).apply()
    }

    fun putUsername(string: String) {
        editor.putString(userName, string).apply()
    }

    fun putEmailId(string: String) {
        editor.putString(emailId, string).apply()
    }

    fun putPassword(string: String) {
        editor.putString(password, string).apply()
    }

    fun putGender(string: String) {
        editor.putString(gender, defUserName).apply()
    }

    fun putLocation(string: String) {
        editor.putString(location, defLocation).apply()
    }

    fun putCourse(string: String) {
        editor.putString(course, string).apply()
    }

    fun putAge(string: String) {
        editor.putString(age, string).apply()
    }

    fun putDOB(string: String) {
        editor.putString(dob, string).apply()
    }

    fun putState(string: String) {
        editor.putString(state, string).apply()
    }
}