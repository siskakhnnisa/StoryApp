package com.example.storyappfix.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.storyappfix.R
import com.example.storyappfix.databinding.ActivitySignupUserBinding
import com.example.storyappfix.view.custom.CustomTextInput
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.storyappfix.repository.StoryResources
import com.example.storyappfix.utils.Utils
import com.example.storyappfix.view.login.LoginUserActivity

class SignupUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupUserBinding
    private val viewModel: SignupUserViewModel by viewModel()
    private lateinit var loading: AlertDialog
    private val Utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Register"
        loading = Utils.showAlertLoading(this)
        binding = ActivitySignupUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            tiName.globalChange()
            tiPassword.globalChange()
            tiEmail.globalChange()
            btnRegister.setOnClickListener {
                viewModel.setRegisterParam(
                    binding.tiName.text.toString(),
                    binding.tiEmail.text.toString(),
                    binding.tiPassword.text.toString()
                )
                viewModel.signupProcess().observe(this@SignupUserActivity) {
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
                            Toast.makeText(this@SignupUserActivity, it.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        playAnimation()
    }

    private fun playAnimation() {
        with(binding) {
            val image = ObjectAnimator.ofFloat(imgOpening, View.ALPHA, 1f).setDuration(300)
            val signupFirst = ObjectAnimator.ofFloat(createAccount, View.ALPHA, 1f).setDuration(300)
            val tvName = ObjectAnimator.ofFloat(tvName, View.ALPHA, 1f).setDuration(300)
            val name = ObjectAnimator.ofFloat(tedName, View.ALPHA, 1f).setDuration(300)
            val tvEmail = ObjectAnimator.ofFloat(tvEmail, View.ALPHA, 1f).setDuration(300)
            val email = ObjectAnimator.ofFloat(tedEmail, View.ALPHA, 1f).setDuration(300)
            val tvPassword = ObjectAnimator.ofFloat(tvPassword, View.ALPHA, 1f).setDuration(300)
            val password = ObjectAnimator.ofFloat(tedPassword, View.ALPHA, 1f).setDuration(300)
            val buttonLogin = ObjectAnimator.ofFloat(btnRegister, View.ALPHA, 1f).setDuration(300)
            AnimatorSet().apply {
                playSequentially(image,signupFirst, tvName, name, tvEmail, email,tvPassword, password, buttonLogin)
                start()
            }
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
                    btnRegister.isEnabled =
                        tiEmail.isValid == true && tiPassword.isValid == true && tiName.isValid == true
                }
            }

        })
    }
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Signup Successful")
            .setMessage("You have successfully signed up!")
            .setPositiveButton("OK") { _, _ ->
                navigateToLogin()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginUserActivity::class.java)
        startActivity(intent)
        finish()
    }
}