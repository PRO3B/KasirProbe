package com.probe.kasirprobe.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Produk::class], version = 1, exportSchema = false)
abstract class ProdukDatabase : RoomDatabase() {
    abstract fun produkDao(): ProdukDao

    companion object {
        @Volatile
        private var INSTANCE: ProdukDatabase? = null

        fun getDatabase(context: Context): ProdukDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProdukDatabase::class.java,
                    "produk_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
