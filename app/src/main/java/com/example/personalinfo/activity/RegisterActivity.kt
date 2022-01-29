package com.example.personalinfo.activity

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import androidx.core.content.FileProvider
import com.example.personalinfo.R
import com.example.personalinfo.utility.SingleShotLocationProvider
import com.example.personalinfo.databinding.ActivityRegisterBinding
import com.example.personalinfo.utility.ContractPersonalInfo
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var bind: ActivityRegisterBinding
    private val cal = Calendar.getInstance()
    val TAG = this.toString()
    lateinit var selectedState: String
    private lateinit var file: File
    private lateinit var userlatlong: String

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
        bind.btnLocation.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        Log.v("onClick", "Working")
        when (v?.id) {
            R.id.submit -> submit(v)
            R.id.etDOB -> setCalendar()
            R.id.imageViewProfileImage -> requestPermissions()
            R.id.btnAddProfileImage -> requestPermissions()
            R.id.btnLocation ->requestLocationPermission()
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
            file = getFile()
            var fileProvider = FileProvider.getUriForFile(this, "com.example.personalinfo", file)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            getResult.launch(cameraIntent)
            dialog.dismiss()
        }
        dialog.setCancelable(false)
    }

    private fun getFile(): File {
        var directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("Picture", ".jpge", directoryStorage)
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
                    val takePicture = BitmapFactory.decodeFile(file.absolutePath)
                    bind.imageViewProfileImage.setImageBitmap(takePicture)
                    convertToBase64(takePicture)
//                    val capturedImage: Bitmap = result.data?.extras?.get("data") as Bitmap
//                    bind.imageViewProfileImage.setImageBitmap(capturedImage)
//                    convertToBase64(capturedImage)
                }
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
            Log.v(TAG, bind.spinnerState.id.toString())
            // or  Log.v(TAG, selectedState)
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
        if (bind.etConfirmPassword.text.isEmpty() || bind.etConfirmPassword.text.toString() != bind.etPassword.text.toString()) {
            onSnack(
                view,
                ContextCompat.getColor(this, R.color.error_text),
                ContractPersonalInfo.errorConfirmPassword
            )
            return
        }
//        if (bind.rgGender.checkedRadioButtonId == -1) {
//            onSnack(
//                view,
//                ContextCompat.getColor(this, R.color.error_text),
//                ContractPersonalInfo.emptyGender
//            )
//
//            return
//        }
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
                ContractPersonalInfo.errorAge
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

        onSnack(
            view,
            ContextCompat.getColor(this, R.color.non_error_text),
            ContractPersonalInfo.submitSuccessful
        )

        startActivity(Intent(this, LandingPage::class.java))

    }

    private fun getUserGender(): String {
        var userGender = ""
        val selectedId = bind.rgGender.checkedRadioButtonId
        val selectedRadioButton = findViewById<RadioButton>(selectedId)
        if (selectedId != -1) {
            userGender = selectedRadioButton.text.toString()
        } else {
            Log.v("Gender", "Not Selected!")
        }
        return userGender
    }

    private fun setUpSpinnerState() {
        val states = resources.getStringArray(R.array.States)
        val simpleSpinnerDropDown = android.R.layout.simple_spinner_item
        val adapterStates =
            ArrayAdapter(this, simpleSpinnerDropDown, states)
        bind.spinnerState.prompt = "Select State"
        bind.spinnerState.adapter = adapterStates

        bind.spinnerState.onItemSelectedListener =
            object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener,
                AdapterView.OnItemLongClickListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    View: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.v(TAG, states[position])
                    selectedState = states[position]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemLongClick(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ): Boolean {
                    TODO("Not yet implemented")
                }

                override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    TODO("Not yet implemented")
                }


            }
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

    private fun requestLocationPermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(multiplePermissionsReport : MultiplePermissionsReport?) {
                        if (multiplePermissionsReport!!.areAllPermissionsGranted()){
                            getLocationFromLocationManager()
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied){

                        }

                    }



                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        TODO("Not yet implemented")
                    }

                }
            ).onSameThread().check()
    }

    private fun getLocationFromLocationManager() {
        SingleShotLocationProvider.requestSingleUpdate(
            this,
            object : SingleShotLocationProvider.LocationCallback {
                override fun onNewLocationAvailable(location: SingleShotLocationProvider.GPSCoordinates?) {
                    if (location != null) {
                        Log.e("TAGA", location.latitude.toString())
                        Log.e("TAGA", location.longitude.toString())
                        val address = SingleShotLocationProvider.getAddress(
                            this@RegisterActivity,
                            location.latitude,
                            location.longitude
                        )
                        val userLoc =
                            "${location.latitude},${location.longitude}"
                        userlatlong = userLoc

                        bind.tvLocation.text = address
                    }
                }
            }
        )
    }
}
