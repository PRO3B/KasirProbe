package com.probe.kasirprobe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.probe.kasirprobe.model.Produk
import com.probe.kasirprobe.model.ProdukDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProdukViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ProdukDatabase.getDatabase(application).produkDao()

    private val _produkList = MutableStateFlow<List<Produk>>(emptyList())
    val produkList: StateFlow<List<Produk>> = _produkList.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getAllProduk().collect {
                _produkList.value = it
            }
        }
    }

    fun tambahProduk(produk: Produk) {
        viewModelScope.launch {
            dao.insertProduk(produk)
        }
    }

    fun hapusProduk(produk: Produk) {
        viewModelScope.launch {
            dao.deleteProduk(produk)
        }
    }
}
