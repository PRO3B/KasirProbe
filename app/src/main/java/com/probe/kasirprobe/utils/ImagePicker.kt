package com.probe.kasirprobe.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberImagePicker(): ImagePickerState {
    val context = LocalContext.current
    return remember { ImagePickerState(context) }
}

class ImagePickerState(private val context: Context) {
    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set

    // Kita akan handle launchers di AddProductScreen nanti
    // Untuk sementara, buat function kosong dulu

    fun openGallery() {
        // Akan diimplementasi di AddProductScreen
    }

    fun openCamera() {
        // Akan diimplementasi di AddProductScreen
    }

    fun clearSelection() {
        selectedImageUri = null
    }

    fun updateSelectedImage(uri: Uri?) {
        selectedImageUri = uri
    }
}

// Function untuk membuat URI untuk gambar baru
private fun createImageUri(context: Context): Uri {
    val contentResolver = context.contentResolver
    val values = android.content.ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "product_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
}