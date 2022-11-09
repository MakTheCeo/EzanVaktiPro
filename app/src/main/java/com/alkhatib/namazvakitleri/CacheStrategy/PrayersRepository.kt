package com.alkhatib.namazvakitleri

import com.alkhatib.namazvakitleri.CacheStrategy.PrayersDatabase
import com.alkhatib.namazvakitleri.RetrofitApi.RetrofitAPI
import javax.inject.Inject

//import com.alkhatib.namazvakitleri.util.networkBoundResource


class PrayersDataRepository @Inject constructor(
    private val api: RetrofitAPI,
    private val db: PrayersDatabase
) {
    private val prayersDao = db.PrayersDao()

/*    suspend fun getPrayers() = networkBoundResource(

        // Query to return the list of all prayers
        query = {
            prayersDao.getAllPrayers()
        },

        // Just for testing purpose,
        // a delay of 2 second is set.
        fetch = {
            api.getPrayersData(SharedPrefs.getInteger("District",9206))
        },

        // Save the results in the table.
        // If data exists, then delete it
        // and then store.
         saveFetchResult = { PrayersData ->
            db.withTransaction {
                prayersDao.deleteAllPrayers()
                prayersDao.insertPrayers(PrayersData)
            }
        }
    )*/
}