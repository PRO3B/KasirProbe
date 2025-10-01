package com.probe.kasirprobe.data.db

import android.content.Context
import androidx.room.Room

object DatabaseModule {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "kasirprobe.db"
            )
                .fallbackToDestructiveMigration() // kalau versi DB berubah, reset tabel
                .build()
            INSTANCE = instance
            instance
        }
    }
}
