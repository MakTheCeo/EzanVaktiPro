package com.alkhatib.namazvakitleri.CacheStrategy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.alkhatib.namazvakitleri.RetrofitApi.PrayersData

@Database(entities = [PrayersData::class], version = 1)
abstract class PrayersDatabase : RoomDatabase() {
    abstract fun PrayersDao(): PrayersDao
}