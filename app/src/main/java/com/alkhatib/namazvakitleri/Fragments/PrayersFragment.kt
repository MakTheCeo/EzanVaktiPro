package com.alkhatib.namazvakitleri.Fragments

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.alkhatib.namazvakitleri.R
import com.alkhatib.namazvakitleri.RetrofitApi.PrayersData
import com.alkhatib.namazvakitleri.RetrofitApi.RetrofitAPI
import com.alkhatib.namazvakitleri.RetrofitApi.SharedPrefs
import com.alkhatib.namazvakitleri.RetrofitApi.website
import com.alkhatib.namazvakitleri.databinding.FragmentPrayersBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate


@Suppress("DEPRECATION")
class PrayersFragment : Fragment() {


    // declaring notification variables
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

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
        mSharedPreferences =
            requireActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

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


        //set text
        binding.locationTv.setText(City + "," + District)


        //calling notification onclicklistener
        NotificationButtonIsClicked(binding)

        //get the monthly prayers data
        getPrayersData(binding, districtId)


        //view binding ending
        return binding.root
    }


    private fun getPrayersData(binding: FragmentPrayersBinding, districtId: Int) {

        //declare and initialize vars
        var PrayersDataList: ArrayList<PrayersData>? = null

        if (mSharedPreferences.contains("PrayersDataList")) {

            // creating a variable for gson.
            // creating a variable for gson.
            val gson = Gson()

            // get to string present from our
            // shared prefs if not present setting it as null.
            val json: String = mSharedPreferences.getString("PrayersDataList", null)!!

            // get the type of our array list.
            val type: Type = object : TypeToken<ArrayList<PrayersData?>?>() {}.type

            //getting data from gson
            // and saving it to our array list
            PrayersDataList = gson.fromJson<Any>(json, type) as ArrayList<PrayersData>

            var PrayersList = PrayersDataList?.get(0)

            //find the current day to display its data
            var i = 0
            var status = true
            while (status) {
                if (PrayersDataList?.get(i)?.MiladiTarihUzun?.split(" ")
                        ?.get(0)
                        .equals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd")))
                ) {
                    PrayersList = PrayersDataList?.get(i)
                    status = false

                } else {
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


            //call function to display time left
            timeLeftTillNextPrayer(binding, PrayersDataList)

            if ((PrayersDataList?.get(15)?.MiladiTarihUzun?.split(" ")
                    ?.get(0)?.toInt()!! <= LocalDate.now().format(DateTimeFormatter.ofPattern("dd"))
                    .toInt()) and isOnline(requireContext())
            ) {
                retrofitGetPrayersData(binding, districtId)
            }
        } else {
            retrofitGetPrayersData(binding, districtId)
        }


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

                if ((current.toSecondOfDay() > LocalTime.parse(PrayersList?.Yatsi, formatter2)
                        .toSecondOfDay()) or
                    (current.toSecondOfDay() < LocalTime.parse(PrayersList?.Imsak, formatter2)
                        .toSecondOfDay())
                ) {
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

    //what to do when notification button is clicked
    fun NotificationButtonIsClicked(binding: FragmentPrayersBinding) {
        binding.notification1.setOnClickListener {
            NotificationOnClickManager(binding.notification1)
            // it is a class to notify the user of events that happen.
            // This is how you tell the user that something has happened in the
            // background.
            notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


            // checking if android version is greater than oreo(API 26) or not
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel =
                    NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(false)
                notificationManager.createNotificationChannel(notificationChannel)

                builder = Notification.Builder(requireContext(), channelId)
                    .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            requireActivity().resources,
                            R.drawable.launch_screen
                        )
                    )
            } else {

                builder = Notification.Builder(requireContext())
                    .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            requireActivity().resources,
                            R.drawable.launch_screen
                        )
                    )
            }
            notificationManager.notify(1234, builder.build())
        }
        binding.notification2.setOnClickListener {
            NotificationOnClickManager(binding.notification2)
        }
        binding.notification3.setOnClickListener {
            NotificationOnClickManager(binding.notification3)
        }
        binding.notification4.setOnClickListener {
            NotificationOnClickManager(binding.notification4)
        }
        binding.notification5.setOnClickListener {
            NotificationOnClickManager(binding.notification5)
        }
        binding.notification6.setOnClickListener {
            NotificationOnClickManager(binding.notification6)
        }

    }

    //what to do when notification button is clicked
    fun NotificationOnClickManager(view: ImageView) {
        if (view.getDrawable()
                .getConstantState() == getResources().getDrawable(R.drawable.ic_baseline_notifications_active_24)
                .getConstantState()
        ) {
            view.setImageResource(R.drawable.ic_baseline_notifications_off_24)
        } else
            view.setImageResource(R.drawable.ic_baseline_notifications_active_24)
    }

    //checking if connected to internet
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun retrofitGetPrayersData(binding: FragmentPrayersBinding, districtId: Int) {

        //declare and initialize vars
        var PrayersDataList: ArrayList<PrayersData>? = null
        var editedPrayersDataList: ArrayList<PrayersData>? = null
        PrayersDataList = ArrayList()
        editedPrayersDataList = ArrayList()

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
                    var i = 0
                    var status = true
                    while (status) {
                        if (PrayersDataList?.get(i)?.MiladiTarihUzun?.split(" ")
                                ?.get(0).equals(
                                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd"))
                                )
                        ) {
                            PrayersList = PrayersDataList?.get(i)
                            status = false
                        } else {
                            i++
                        }
                    }

                    //set data to the views
                    binding.dayTv.setText(
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd"))
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


                    //call function to display time left
                    timeLeftTillNextPrayer(binding, PrayersDataList)

                    // store data in shared preferences.
                    // creating a new variable for gson.
                    var gson = Gson()

                    // getting data from gson and storing it in a string.
                    var json2 = gson.toJson(PrayersDataList)

                    // creating a variable for editor to
                    // store data in shared preferences.
                    val editor2: SharedPreferences.Editor = mSharedPreferences.edit()

                    //save data in shared
                    // prefs in the form of string.
                    editor2.putString("PrayersDataList", json2)

                    // apply changes
                    // and save data in shared prefs.
                    editor2.apply()

                    editedPrayersDataList = PrayersDataList
                    for (i in 0..editedPrayersDataList!!.size - 1) {
                        editedPrayersDataList?.get(i)?.MiladiTarihUzun =
                            editedPrayersDataList?.get(i)?.MiladiTarihUzun?.split(" ")!!
                                .get(0) + " " +
                                    editedPrayersDataList?.get(i)?.MiladiTarihUzun?.split(" ")!!
                                        .get(1).slice(0..2)
                    }


                    // getting data from gson and storing it in a string.
                    var json1 = gson.toJson(editedPrayersDataList)


                    // creating a variable for editor to
                    // store data in shared preferences.
                    val editor1: SharedPreferences.Editor = mSharedPreferences.edit()

                    //save data in shared
                    // prefs in the form of string.
                    editor1.putString("calenderPrayersDataList", json1)

                    // apply changes
                    // and save data in shared prefs.
                    editor1.apply()


                }
            }

            override fun onFailure(call: Call<ArrayList<PrayersData>?>?, t: Throwable?) {
                // displaying an error message in toast
                Toast.makeText(context, "Failed to get the prayers data..", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }
}