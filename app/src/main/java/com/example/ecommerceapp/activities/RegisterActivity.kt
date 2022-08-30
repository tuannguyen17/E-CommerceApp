package com.example.ecommerceapp.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toolbar
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.ActivityRegisterBinding

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setupActionBar()

        binding.btnRegister.setOnClickListener { validateRegisterDetails() }
        binding.tvLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupActionBar() {
        val tbRegisterActivity: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_register_activity)
        setSupportActionBar(findViewById(R.id.toolbar_register_activity))

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }

        tbRegisterActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(etFirstName.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                    false
                }

                TextUtils.isEmpty(etLastName.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                    false
                }

                TextUtils.isEmpty(etEmail.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                    false
                }

                TextUtils.isEmpty(etPassword.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                    false
                }

                TextUtils.isEmpty(etConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                    false
                }

                etPassword.text.toString().trim { it <= ' ' } != etConfirmPassword.text.toString()
                    .trim { it <= ' ' } -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                    false
                }
                !cbTermsAndCondition.isChecked -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                    false
                }
                else -> {
                    showErrorSnackBar("Your details are valid.", false)
                    true
                }
            }
        }
    }
}