package com.alkhatib.namazvakitleri.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alkhatib.namazvakitleri.RetrofitApi.PrayersData
import com.alkhatib.namazvakitleri.R
import com.alkhatib.namazvakitleri.RetrofitApi.RetrofitAPI
import com.alkhatib.namazvakitleri.RetrofitApi.website
import com.alkhatib.namazvakitleri.databinding.FragmentPrayersBinding
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class PrayersFragment : Fragment() {

    // TODO (STEP 1: Add a variable for SharedPreferences)
    private lateinit var mSharedPreferences: SharedPreferences

    //declare vars
    private lateinit var binding: FragmentPrayersBinding
    private var districtId: Int = 0
    private var City: String = ""
    private var District: String = ""

    // TODO (STEP 2: Add the SharedPreferences name and key name for storing the response data in it.)
    val PREFERENCE_NAME = "LocationPreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO (STEP 3: Initialize the SharedPreferences variable.)
        mSharedPreferences = requireActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

        //get location from the shared preferences
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

        //get the prayers data of the month
        getPrayersData(binding, districtId)

        //view binding ending
        binding.locationTv.setText(City + "," + District)
        return binding.root
    }


    private fun getPrayersData(binding: FragmentPrayersBinding, districtId: Int) {

        //declare and initialize vars
        var PrayersDataList: ArrayList<PrayersData>?
        PrayersDataList = ArrayList()

        //creating a retrofit builder and passing our base url
        val retrofit = Retrofit.Builder()
            .baseUrl("$website")

            //calling add Converter factory as GSON converter factory.
            // at last we are building our retrofit builder.
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // below line is to create an instance for our retrofit api class.
        val retrofitAPI = retrofit.create(RetrofitAPI::class.java)

        val callPrayersData: Call<ArrayList<PrayersData>?>? = retrofitAPI.getPrayersData(districtId)

        //making a call.
        callPrayersData!!.enqueue(object : Callback<ArrayList<PrayersData>?> {
            override fun onResponse(
                call: Call<ArrayList<PrayersData>?>?,
                response: Response<ArrayList<PrayersData>?>
            ) {
                if (response.isSuccessful()) {
                    //get the list of the data
                    PrayersDataList = response.body()!!
                    var PrayersList = PrayersDataList?.get(0)

                    //find the current day to display its data
                    var i=0
                    var status=true
                    while (status)
                    {
                        if(PrayersDataList?.get(i)?.MiladiTarihUzun?.split(" ")
                                ?.get(0).equals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd"))))
                        {
                            PrayersList=PrayersDataList?.get(i)
                            status=false
                        }
                        else
                        {
                            i++
                        }
                    }

                    //set data to the views
                    binding.dayTv.setText(
                        PrayersList?.MiladiTarihUzun?.split(" ")
                            ?.get(0)
                    //LocalDate.now().format(DateTimeFormatter.ofPattern("dd"))
                    )
                    binding.monthWeekTv.setText(
                        PrayersList?.MiladiTarihUzun?.split(" ")?.get(1) + ","
                                + PrayersList?.MiladiTarihUzun?.split(" ")?.get(3)
                    )
                    binding.hijriTV.setText(
                        PrayersList?.HicriTarihKisa?.split(".")?.get(1) + "/" +
                                PrayersList?.HicriTarihKisa?.split(".")?.get(2)
                    )
                    binding.vakit1.text = PrayersList?.Imsak.toString()
                    binding.vakit2.text = PrayersList?.Gunes.toString()
                    binding.vakit3.text = PrayersList?.Ogle.toString()
                    binding.vakit4.text = PrayersList?.Ikindi.toString()
                    binding.vakit5.text = PrayersList?.Aksam.toString()
                    binding.vakit6.text = PrayersList?.Yatsi.toString()


                    timeLeftTillNextPrayer(binding, PrayersDataList)


                    // creating a variable for editor to
                    // store data in shared preferences.
                    val editor: SharedPreferences.Editor = mSharedPreferences.edit()

                    // creating a new variable for gson.
                    val gson = Gson()

                    // getting data from gson and storing it in a string.
                    val json = gson.toJson(PrayersDataList)

                    //save data in shared
                    // prefs in the form of string.
                    editor.putString("prayersDataList", json)

                    // apply changes
                    // and save data in shared prefs.
                    editor.apply()

                }
            }

            override fun onFailure(call: Call<ArrayList<PrayersData>?>?, t: Throwable?) {
                // displaying an error message in toast
                Toast.makeText(context, "Failed to get the prayers data..", Toast.LENGTH_SHORT)
                    .show()
            }

        })

    }

    //how much time left till the Ezan?
    fun timeLeftTillNextPrayer(
        binding: FragmentPrayersBinding,
        PrayersDataList: ArrayList<PrayersData>?
    ) {
        //declare vars
        var PrayersList = PrayersDataList?.get(3)

        //setting up the scope
        val scope = MainScope() // could also use an other scope such as viewModelScope
        var job: Job? = null
        job?.cancel()
        job = null
        job = scope.launch {
            val formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss")
            val formatter2 = DateTimeFormatter.ofPattern("HH:mm")
            var nextprayer = LocalTime.parse(PrayersList?.Imsak, formatter2)

          //always run
            while (true) {
                //vars calculations
                val current = LocalTime.now()
                val currentInSeconds = current.toSecondOfDay()
                val timeLeft = nextprayer.minusSeconds(currentInSeconds.toLong()).format(formatter1)

                if ((current.toSecondOfDay() > LocalTime.parse(PrayersList?.Yatsi, formatter2).toSecondOfDay()) or
                    (current.toSecondOfDay() < LocalTime.parse(PrayersList?.Imsak, formatter2).toSecondOfDay()) )
                {
                    nextprayer = LocalTime.parse(PrayersList?.Imsak, formatter2)
                    binding.nextPrayerTv.setText("İmsak")
                    binding.nextPrayerIv.setImageResource(R.drawable.prayer1_white)
                } else if (current.toSecondOfDay() < LocalTime.parse(
                        PrayersList?.Ogle,
                        formatter2
                    ).toSecondOfDay()
                ) {
                    nextprayer = LocalTime.parse(PrayersList?.Ogle, formatter2)
                    binding.nextPrayerTv.setText("Öğle")
                    binding.nextPrayerIv.setImageResource(R.drawable.prayer2_white)

                } else if (current.toSecondOfDay() < LocalTime.parse(
                        PrayersList?.Ikindi,
                        formatter2
                    ).toSecondOfDay()
                ) {
                    nextprayer = LocalTime.parse(PrayersList?.Ikindi, formatter2)
                    binding.nextPrayerTv.setText("İkindi")
                    binding.nextPrayerIv.setImageResource(R.drawable.prayer3_white)
                } else if (current.toSecondOfDay() < LocalTime.parse(
                        PrayersList?.Aksam,
                        formatter2
                    ).toSecondOfDay()
                ) {
                    nextprayer = LocalTime.parse(PrayersList?.Aksam, formatter2)
                    binding.nextPrayerTv.setText("Akşam")
                    binding.nextPrayerIv.setImageResource(R.drawable.prayer4_white)

                } else if (current.toSecondOfDay() < LocalTime.parse(
                        PrayersList?.Yatsi,
                        formatter2
                    ).toSecondOfDay()
                ) {
                    nextprayer = LocalTime.parse(PrayersList?.Yatsi, formatter2)
                    binding.nextPrayerTv.setText("Yatsı")
                    binding.nextPrayerIv.setImageResource(R.drawable.prayer5_white)

                }
                binding.timeLeftForNextPrayerTv.setText(timeLeft.toString())
                delay(1000)
            }
        }
    }

}