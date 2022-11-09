package com.alkhatib.namazvakitleri.CacheStrategy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alkhatib.namazvakitleri.RetrofitApi.PrayersData
import kotlinx.coroutines.flow.Flow
@Dao
interface PrayersDao {

    // Query to fetch all the data from the
    // SQLite database
    // No need of suspend method here
    @Query("SELECT * FROM prayers")

    // Kotlin flow is an asynchronous stream of values
    fun getAllPrayers(): Flow<List<PrayersData>>

    // If a new data is inserted with same primary key
    // It will get replaced by the previous one
    // This ensures that there is always a latest
    // data in the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)

    // The fetching of data should NOT be done on the
    // Main thread. Hence coroutine is used
    // If it is executing on one one thread, it may suspend
    // its execution there, and resume in another one
    suspend fun insertPrayers(prayers: List<PrayersData>)

    // Once the device comes online, the cached data
    // need to be replaced, i.e. delete it
    // Again it will use coroutine to achieve this task
    @Query("DELETE FROM prayers")
    suspend fun deleteAllPrayers()
}