package com.dev.notespace.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts

class MediaPicker: ActivityResultContracts.GetContent() {
    override fun createIntent(context: Context, input: String): Intent {
        super.createIntent(context, input)
        return Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
    }
}