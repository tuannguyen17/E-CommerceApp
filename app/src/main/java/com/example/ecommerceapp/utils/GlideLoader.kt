package com.example.ecommerceapp.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.ecommerceapp.R
import java.io.IOException

class GlideLoader(val context: Context) {
    fun loadUserPicture(imageURI: Uri, imageView: ImageView) {
        try {
            // Load the user image in the ImageView.
            Glide
                .with(context)
                .load(Uri.parse(imageURI.toString()))
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}