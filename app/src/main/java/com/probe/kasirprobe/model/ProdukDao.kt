package com.probe.kasirprobe.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdukDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduk(produk: Produk)

    @Delete
    suspend fun deleteProduk(produk: Produk)

    @Query("SELECT * FROM produk ORDER BY nama ASC")
    fun getAllProduk(): Flow<List<Produk>>
}
