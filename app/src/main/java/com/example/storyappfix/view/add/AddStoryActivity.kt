package com.example.storyappfix.view.add

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.storyappfix.R
import com.example.storyappfix.utils.CameraUtils
import com.example.storyappfix.utils.SharedPreference
import com.example.storyappfix.databinding.ActivityAddStoryBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*
import com.example.storyappfix.view.custom.CustomTextInput
import com.example.storyappfix.view.main.MainStoryActivity
import com.example.storyappfix.view.login.LoginUserActivity
import com.example.storyappfix.view.maps.MapsActivity
import com.example.storyappfix.repository.StoryResources
import com.example.storyappfix.utils.Utils

class AddStoryActivity : AppCompatActivity(), LocationListener {
    private lateinit var binding : ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModel()
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String
    private var lat: Float = -7.482145F
    private var lon: Float = 110.77294F
    private lateinit var locationManager: LocationManager
    private lateinit var loading: AlertDialog
    private val Utils = Utils()

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            binding.imgPosting.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = Utils.uriToFile(selectedImg, this)
            getFile = myFile
            binding.imgPosting.setImageURI(selectedImg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading = Utils.showAlertLoading(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.show()
        with(binding) {
            getLocation()
            btnCamera.setOnClickListener {
                openCamera()
            }
            btnGallery.setOnClickListener {
                openGallery()
            }

            tiDescription.globalChange()

            btnUpload.setOnClickListener {
                val token = SharedPreference(applicationContext).getToken()!!
                val file = Utils.reduceFileImage(getFile as File)
                val description = tiDescription.text.toString()
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,

                    requestImageFile
                )
                viewModel.setStoryParam(token, imageMultipart, description, lat, lon)
                viewModel.postStory.observe(this@AddStoryActivity) {
                    when (it) {
                        is StoryResources.Loading -> {
                            loading.show()
                        }
                        is StoryResources.Success -> {
                            loading.hide()
                            Toast.makeText(
                                this@AddStoryActivity,
                                resources.getString(R.string.upload_success),
                                Toast.LENGTH_SHORT
                            ).show()

                            setResult(RESULT_OK)
                            finish()

                            Intent(this@AddStoryActivity, MainStoryActivity::class.java)
                                .apply {
                                    finish()
                                    startActivity(this)
                                }
                        }
                        is StoryResources.Error -> {
                            loading.hide()
                            Toast.makeText(this@AddStoryActivity, it.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainStoryActivity::class.java))
        finish()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        CameraUtils.createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.storyappfix.provider",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)

        }
    }
    private fun CustomTextInput.globalChange() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(binding) {
                    btnUpload.isEnabled = tiDescription.isValid == true && getFile != null
                }
            }

        })
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        lon = location.longitude.toFloat()
        lat = location.latitude.toFloat()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.languageSetting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.action_logout -> {
                Intent(this@AddStoryActivity, LoginUserActivity::class.java)
                    .apply {
                        startActivity(this)
                        finishAffinity()
                        SharedPreference(this@AddStoryActivity).clearPreference()
                    }
                return true
            }
            R.id.btnmaps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}