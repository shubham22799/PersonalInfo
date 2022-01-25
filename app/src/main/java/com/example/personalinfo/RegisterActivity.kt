package com.example.personalinfo

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.personalinfo.databinding.ActivityRegisterBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var bind: ActivityRegisterBinding
    private val cal = Calendar.getInstance()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bind.root)

        displayUserAge()
        setUpUserCourse()
        setUpSpinnerState()

        bind.submit.setOnClickListener(this)
        bind.imageViewProfileImage.setOnClickListener(this)
        bind.etDOB.setOnClickListener(this)

        sharedPreferences =
            this.getSharedPreferences(
                ContractPersonalInfo.PersonalInfoPreferences.sharedPreferenceName,
                MODE_PRIVATE
            )

    }

    override fun onClick(v: View?) {
        Log.v("onClick", "Working")
        when (v?.id) {
            R.id.submit -> submit(v)
            R.id.etDOB -> setCalendar()
            R.id.imageViewProfileImage -> requestPermissions()
            R.id.btnAddProfileImage -> requestPermissions()
        }
    }

    private fun requestPermissions() {
        val tag = "Permission"
        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport?) {
                        if (multiplePermissionsReport!!.areAllPermissionsGranted()) {
                            Log.v(tag, "Permission Granted")
                            showBottomSheet()
                            return
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                            //showSettingDialouge()
                            Log.v("Permissions", "denied permanently")
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        list: MutableList<PermissionRequest>?,
                        permissionToken: PermissionToken?
                    ) {
                        permissionToken!!.continuePermissionRequest()
                    }
                })
            .withErrorListener {
                Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show()
            }
            .onSameThread().check()
    }

    private fun showSettingDialouge() {
        TODO("Not yet implemented")
    }

    private fun showBottomSheet() {
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.show()
        val gallery = view.findViewById<Button>(R.id.btn_gallery)
        gallery!!.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            getResult.launch(galleryIntent)
            dialog.dismiss()
        }
        val camera = view.findViewById<Button>(R.id.btn_camera)
        camera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getResult.launch(cameraIntent)
            dialog.dismiss()
        }
        dialog.setCancelable(false)
    }

    private fun convertToBase64(bitmap: Bitmap) {
        val base = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, base)
        val b: ByteArray = base.toByteArray()
        val outPut = encodeToString(b, DEFAULT)

    }


    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri: Uri? = result.data?.data
                if (uri != null) {
                    //convert Uri to Bitmap
                    val galleryBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    bind.imageViewProfileImage.setImageBitmap(galleryBitmap)
                    Log.v("From Gallery", galleryBitmap.toString())
                    convertToBase64(galleryBitmap)
                } else {
                    val capturedImage: Bitmap = result.data?.extras?.get("data") as Bitmap
                    bind.imageViewProfileImage.setImageBitmap(capturedImage)
                    convertToBase64(capturedImage)
                }
            }
        }

    private fun onSnack(view: View, texColor: Int, errorMessage: String) {
        val snackBar =
            Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).setAction("Action", null)
        val snackBarView = snackBar.view
        //       snackBarView.setBackgroundColor(getColor(R.color.light_gray))
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))

        val textView =
            snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(texColor)
        textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        textView.textSize = 14f
        snackBar.show()
    }

    private fun setCalendar() {
        val dd = cal.get(Calendar.DAY_OF_MONTH)
        val mm = cal.get(Calendar.MONTH)
        val yy = cal.get(Calendar.YEAR)
        val datePickListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)
            bind.etDOB.setText(SimpleDateFormat("dd-MM-yyyy", Locale.US).format(cal.time))
        }
        val dialog = DatePickerDialog(
            this,
            R.style.DatePickerDialouge,
            datePickListener,
            yy, mm, dd
        )
        dialog.datePicker.maxDate = Date().time
        dialog.show()
    }

    private fun setUpUserCourse() {
        val course = resources.getStringArray(R.array.Courses)
        val courseAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, course)
        bind.etCourse.setAdapter(courseAdapter)
    }

    private fun submit(view: View) {
//        if (bind.imageViewProfileImage.drawable != null) {
//            onSnack(
//                view,
//                ContextCompat.getColor(this, R.color.error_text),
//                ContractPersonalInfo.emptyImageView
//            )
//            return
//        }
        if (bind.etUserName.text.isEmpty()) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.emptyUserName
            )
            return
        }
        if (bind.etEmail.text.isEmpty()) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.emptyEmailId
            )
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(bind.etEmail.text.toString()).matches()) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.incorrectEmailId
            )
            return
        }
        if (bind.etPassword.text.isEmpty()) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.emptyPassword
            )
            return
        }
        if (bind.etConfirmPassword.text.isEmpty() || bind.etConfirmPassword.text != bind.etPassword.text) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.errorConfirmPassword
            )
            return
        }
        if (bind.rgGender.checkedRadioButtonId == -1) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.emptyGender
            )

            return
        }
        if (bind.etCourse.text.isEmpty()) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.emptyCourse
            )

            return
        }
        if (bind.seekBar.progress == 0) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.invalidAge
            )

            return
        }
        if (bind.etDOB.text.isEmpty()) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.emptyDOB
            )

            return
        }
        if (bind.spinnerState.id == -1) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.emptyState
            )
            Log.v("spinner", bind.spinnerState.id.toString())

            return
        }
        val editor = sharedPreferences.edit()

        Toast.makeText(this, "all well", Toast.LENGTH_SHORT).show()

        editor.putString(
            ContractPersonalInfo.PersonalInfoPreferences.userName,
            bind.etUserName.text.toString()
        )

        editor.putString(
            ContractPersonalInfo.PersonalInfoPreferences.emailId,
            bind.etEmail.text.toString()
        )

        editor.putString(
            ContractPersonalInfo.PersonalInfoPreferences.password,
            bind.etConfirmPassword.text.toString()
        )

        editor.putString(
            ContractPersonalInfo.PersonalInfoPreferences.confirmPassword,
            bind.etConfirmPassword.text.toString()
        )

        editor.putString(ContractPersonalInfo.PersonalInfoPreferences.gender, getUserGender())

        editor.putString(
            ContractPersonalInfo.PersonalInfoPreferences.course,
            bind.etCourse.text.toString()
        )

        editor.putString(
            ContractPersonalInfo.PersonalInfoPreferences.age,
            bind.seekBar.progress.toString()
        )

        editor.putString(
            ContractPersonalInfo.PersonalInfoPreferences.dob,
            bind.etDOB.text.toString()
        )

        editor.putString(
            ContractPersonalInfo.PersonalInfoPreferences.state,
            bind.spinnerState.selectedItem.toString()
        )

        editor.apply()

        onSnack(
            view,
            ContextCompat.getColor(this, R.color.non_error_text),
            ContractPersonalInfo.submitSuccessful
        )

        startActivity(Intent(this, LandingPage::class.java))

    }

    private fun getUserGender(): String {
        val selectedId = bind.rgGender.checkedRadioButtonId
        val selectedRadioButton = findViewById<RadioButton>(selectedId)
        return selectedRadioButton.text.toString()
    }

    private fun setUpSpinnerState() {
        val states = resources.getStringArray(R.array.States)
        val simpleSpinnDroDown = android.R.layout.simple_spinner_dropdown_item
        val adapterStates =
            ArrayAdapter(this, simpleSpinnDroDown, states)
        bind.spinnerState.prompt = "Select State"
        bind.spinnerState.adapter = adapterStates
    }

    private fun displayUserAge() {
        val sBar = bind.seekBar
        sBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var pval = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                pval = progress
                bind.tvAge.text = (pval.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}
