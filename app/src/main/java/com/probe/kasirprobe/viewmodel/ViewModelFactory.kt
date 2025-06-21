package com.probe.kasirprobe.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProdukViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProdukViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
