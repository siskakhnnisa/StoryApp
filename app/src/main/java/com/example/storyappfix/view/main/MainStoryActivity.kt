package com.example.storyappfix.view.main


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyappfix.R
import com.example.storyappfix.utils.SharedPreference
import com.example.storyappfix.databinding.ActivityMainStoryBinding
import com.example.storyappfix.view.add.AddStoryActivity
import com.example.storyappfix.view.login.LoginUserActivity
import com.example.storyappfix.view.maps.MapsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AlertDialog
import androidx.paging.LoadState
import com.example.storyappfix.utils.Utils

class MainStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainStoryBinding
    private val viewModel: MainStoryViewModel by viewModel()
    private val adapter = MainStoryAdapter()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var loading: AlertDialog
    private val Utils = Utils()
    private var recyclerViewState: Parcelable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.show()

        loading = Utils.showAlertLoading(this)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        recyclerView = findViewById(R.id.rv_stories)
        swipeRefreshLayout.setOnRefreshListener {
            fetchData()
        }
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        initView()
    }

    private fun fetchData() {
        val token = SharedPreference(applicationContext).getToken()!!
        loading.show()
        viewModel.getAllStories(token).observe(this) { stories ->
            loading.hide()
            swipeRefreshLayout.isRefreshing = false
            adapter.submitData(lifecycle, stories)
        }
    }

    private fun getData() {
        val token = SharedPreference(applicationContext).getToken()!!
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainStoryActivity)
            adapter =
                this@MainStoryActivity.adapter.withLoadStateFooter(
                    footer = LoadingAdapter {
                        this@MainStoryActivity.adapter.retry()
                    }
                )
        }
        if (!isFinishing) {
            viewModel.getAllStories(token).observe(this) {
                adapter.submitData(lifecycle, it)
            }
        }
        adapter.addLoadStateListener { loadState ->
            val errorState = loadState.source.refresh as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    this,
                    "Error: ${it.error.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (loadState.source.refresh is LoadState.NotLoading &&
                loadState.append.endOfPaginationReached &&
                adapter.itemCount < 1
            ) {
                Toast.makeText(
                    this,
                    "No data available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun initView() {
        with(binding) {
            tvUsername.text = SharedPreference(this@MainStoryActivity).getName()
            fabAdd.setOnClickListener {
                Intent(this@MainStoryActivity, AddStoryActivity::class.java)
                    .apply {
                        finish()
                        startActivity(this)
                    }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.permission_forbidden),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
//    override fun onResume() {
//        super.onResume()
//        getData()
//    }
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
                Intent(this@MainStoryActivity, LoginUserActivity::class.java)
                    .apply {
                        startActivity(this)
                        finishAffinity()
                        SharedPreference(this@MainStoryActivity).clearPreference()
                    }
            }
            R.id.btnmaps -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        getData()
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
