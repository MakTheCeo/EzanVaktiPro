package com.alkhatib.namazvakitleri

import android.R
import android.content.Context
import android.content.SharedPreferences
import android.icu.util.TimeUnit.values
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.alkhatib.namazvakitleri.databinding.ActivityLocationBinding
import com.alkhatib.namazvakitleri.databinding.FragmentPrayersBinding
import com.alkhatib.namazvakitleri.databinding.TestingBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Time
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.chrono.JapaneseEra.values
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAmount

class PrayersFragment : Fragment() {

    // TODO (STEP 1: Add a variable for SharedPreferences)
    // START
    // A global variable for the SharedPreferences
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var binding: FragmentPrayersBinding
    private var districtId: Int = 0
    private var City: String = ""
    private var District: String = ""

    // END
    // TODO (STEP 2: Add the SharedPreferences name and key name for storing the response data in it.)
    // START Note: add it to constants object
    val PREFERENCE_NAME = "LocationPreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO (STEP 3: Initialize the SharedPreferences variable.)
        // START
        // Initialize the SharedPreferences variable
        mSharedPreferences =
            requireActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

        districtId = mSharedPreferences.getInt("DistrictCode", 0)
        City = mSharedPreferences.getString("City", "")!!
        District = mSharedPreferences.getString("District", "")!!

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPrayersBinding.inflate(layoutInflater)

        getPrayersData(binding, districtId)
        binding.locationTv.setText(City + "," + District)
        return binding.root
    }


    private fun getPrayersData(binding: FragmentPrayersBinding, districtId: Int) {
        var PrayersDataList: ArrayList<PrayersData>?

        PrayersDataList = ArrayList()

        // on below line we are creating a retrofit
        // builder and passing our base url
        // on below line we are creating a retrofit
        // builder and passing our base url
        val retrofit = Retrofit.Builder()
            .baseUrl("$website")

            // on below line we are calling add Converter
            // factory as GSON converter factory.
            // at last we are building our retrofit builder.
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // below line is to create an instance for our retrofit api class.
        // below line is to create an instance for our retrofit api class.
        val retrofitAPI = retrofit.create(RetrofitAPI::class.java)

        val callPrayersData: Call<ArrayList<PrayersData>?>? = retrofitAPI.getPrayersData(districtId)

        // on below line we are making a call.
        callPrayersData!!.enqueue(object : Callback<ArrayList<PrayersData>?> {
            override fun onResponse(
                call: Call<ArrayList<PrayersData>?>?,
                response: Response<ArrayList<PrayersData>?>
            ) {
                if (response.isSuccessful()) {

                    PrayersDataList = response.body()

                    binding.dayTv.setText(
                        PrayersDataList?.get(1)?.MiladiTarihUzun?.split(" ")?.get(0)
                    )
                    binding.monthWeekTv.setText(
                        PrayersDataList?.get(1)?.MiladiTarihUzun?.split(" ")?.get(1) + ","
                                + PrayersDataList?.get(1)?.MiladiTarihUzun?.split(" ")?.get(3)
                    )
                    binding.hijriTV.setText(
                        PrayersDataList?.get(1)?.HicriTarihKisa?.split(".")?.get(1) + "/" +
                                PrayersDataList?.get(1)?.HicriTarihKisa?.split(".")?.get(2)
                    )



                    timeLeftTillNextPrayer(binding, PrayersDataList)


                }
            }

            override fun onFailure(call: Call<ArrayList<PrayersData>?>?, t: Throwable?) {
                // displaying an error message in toast

            }
        })
    }

    fun timeLeftTillNextPrayer(
        binding: FragmentPrayersBinding,
        PrayersDataList: ArrayList<PrayersData>?
    ) {

        val scope = MainScope() // could also use an other scope such as viewModelScope if available
        var job: Job? = null
        job?.cancel()
        job = null
        job = scope.launch {
            val formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss")
            val formatter2=DateTimeFormatter.ofPattern("HH:mm")
            var nextprayer = LocalTime.parse(PrayersDataList?.get(1)?.Imsak, formatter2)
            while (true) {

                val current = LocalTime.now()
                val currentInSeconds = current.toSecondOfDay()
                val timeLeft = nextprayer.minusSeconds(currentInSeconds.toLong()).format(formatter1)

                if (current.toSecondOfDay() < LocalTime.parse(
                        PrayersDataList?.get(1)?.Imsak,
                        formatter2
                    ).toSecondOfDay()
                ) {
                    nextprayer = LocalTime.parse(PrayersDataList?.get(1)?.Imsak, formatter2)
                    binding.nextPrayerTv.setText("İmsak")
                }
                else if (current.toSecondOfDay() < LocalTime.parse(
                        PrayersDataList?.get(1)?.Ogle ,
                        formatter2
                    ).toSecondOfDay()
                ) {
                    nextprayer = LocalTime.parse(PrayersDataList?.get(1)?.Ogle , formatter2)
                    binding.nextPrayerTv.setText("Öğle")
                }
              else  if (current.toSecondOfDay() < LocalTime.parse(
                        PrayersDataList?.get(1)?.Ikindi,
                        formatter2
                    ).toSecondOfDay()
                ) {
                    nextprayer = LocalTime.parse(PrayersDataList?.get(1)?.Ikindi, formatter2)
                    binding.nextPrayerTv.setText("İkindi")

                } else if (current.toSecondOfDay() < LocalTime.parse(
                        PrayersDataList?.get(1)?.Aksam,
                        formatter2
                    ).toSecondOfDay()
                ) {
                    nextprayer = LocalTime.parse(PrayersDataList?.get(1)?.Aksam, formatter2)
                    binding.nextPrayerTv.setText("Akşam")
                } else if (current.toSecondOfDay() < LocalTime.parse(
                        PrayersDataList?.get(1)?.Yatsi,
                        formatter2
                    ).toSecondOfDay()
                ) {
                    nextprayer = LocalTime.parse(PrayersDataList?.get(1)?.Yatsi, formatter2)
                    binding.nextPrayerTv.setText("Yatsı")

                }
                binding.timeLeftForNextPrayerTv.setText(timeLeft.toString())
                delay(1000)
            }
        }
    }

}