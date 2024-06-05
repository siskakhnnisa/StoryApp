package com.example.storyappfix.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.storyappfix.databinding.ActivityLoginUserBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.storyappfix.view.custom.CustomTextInput
import com.example.storyappfix.view.main.MainStoryActivity
import com.example.storyappfix.view.signup.SignupUserActivity
import com.example.storyappfix.repository.StoryResources
import com.example.storyappfix.utils.Utils


class LoginUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginUserBinding
    private lateinit var loading: AlertDialog
    private val Utils = Utils()

    private val viewModel : LoginUserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Login"
        loading = Utils.showAlertLoading(this)

        with(binding) {
            tiPassword.globalChange()
            tiEmail.globalChange()
            btnLogin.setOnClickListener {
                viewModel.setLoginParam(tiEmail.text.toString(), tiPassword.text.toString())
                viewModel.loginProcess().observe(this@LoginUserActivity) {
                    when (it) {
                        is StoryResources.Loading -> {
                            loading.show()
                        }
                        is StoryResources.Success -> {
                            loading.hide()
                            showSuccessDialog()
                        }
                        is StoryResources.Error -> {
                            loading.hide()
                            Toast.makeText(this@LoginUserActivity, it.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            btnSignup.isEnabled = true
            btnSignup.setOnClickListener {
                Intent(this@LoginUserActivity, SignupUserActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }


    private fun goToHomeActivity() {
        Intent(this@LoginUserActivity, MainStoryActivity::class.java)
            .apply {
                startActivity(this)
                finish()
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
                    btnLogin.isEnabled = tiEmail.isValid== true && tiPassword.isValid == true
                }
            }
        })
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Signup Successful")
            .setMessage("You have successfully login!")
            .setPositiveButton("OK") { _, _ ->
                goToHomeActivity()
            }
            .setCancelable(false)
            .show()
    }

    companion object{
        const val EXTRA_TOKEN = "extra-token"
    }
}