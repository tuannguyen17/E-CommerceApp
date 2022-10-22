package com.example.ecommerceapp.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.ActivityAddProductBinding
import com.example.ecommerceapp.firestore.FirestoreClass
import com.example.ecommerceapp.models.Product
import com.example.ecommerceapp.utils.Constants
import com.example.ecommerceapp.utils.GlideLoader
import java.io.IOException

class AddProductActivity : BaseActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.apply {
            ivAddUpdateProduct.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this@AddProductActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    Constants.showImageChooser(this@AddProductActivity)
                } else {
                    /*Requests permissions to be granted to this application. These permissions
                     must be requested in your manifest, they should not be granted to your app,
                     and they should have protection level*/
                    ActivityCompat.requestPermissions(
                        this@AddProductActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }
            }

            btnSubmitAddProduct.setOnClickListener {
                if (validateProductDetails()) {
                    uploadProductImage()
                }
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarAddProductActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_arrow_back_24)
        }

        binding.toolbarAddProductActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Constants.showImageChooser(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @Suppress("DEPRECATION")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    binding.ivAddUpdateProduct.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddProductActivity,
                            R.drawable.ic_vector_edit
                        )
                    )

                    mSelectedImageFileUri = data.data!!

                    try {
                        GlideLoader(this).loadUserPicture(
                            mSelectedImageFileUri!!,
                            binding.ivProductImage
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateProductDetails(): Boolean {
        return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(binding.etProductTitle.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(binding.etProductPrice.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(binding.etProductDescription.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }

            TextUtils.isEmpty(binding.etProductQuantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun uploadProductImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().uploadImageToCloudStorage(
            this@AddProductActivity,
            mSelectedImageFileUri,
            Constants.PRODUCT_IMAGE
        )
    }

    fun imageUploadSuccess(imageURL: String) {
        mProductImageURL = imageURL
        uploadProductDetails()
    }

    private fun uploadProductDetails() {

        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username =
            this.getSharedPreferences(Constants.ECOMMERCE_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constants.LOGGED_IN_USERNAME, "")!!

        // Here we get the text from editText and trim the space
        val product = Product(
            FirestoreClass().getCurrentUserID(),
            username,
            binding.etProductTitle.text.toString().trim { it <= ' ' },
            binding.etProductPrice.text.toString().trim { it <= ' ' },
            binding.etProductDescription.text.toString().trim { it <= ' ' },
            binding.etProductQuantity.text.toString().trim { it <= ' ' },
            mProductImageURL
        )

        FirestoreClass().uploadProductDetails(this@AddProductActivity, product)
    }

    fun productUploadSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@AddProductActivity,
            resources.getString(R.string.product_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}