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
import android.os.Build
import android.os.Bundle
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.fixedRateTimer


@Suppress("DEPRECATION")
class PrayersFragment : Fragment(){



    // declaring notification variables
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    //declare vars
    private lateinit var binding: FragmentPrayersBinding
    private var districtId: Int = 0
    private var City: String = ""
    private var District: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //initialize shared prefs
        SharedPrefs.init(requireContext())

        //get location from the shared preferences
        districtId = SharedPrefs.getInteger("DistrictCode", 0)
        City = SharedPrefs.getString("City", "")!!
        District = SharedPrefs.getString("District", "")!!


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
        var PrayersDataList: ArrayList<PrayersData>?
        var editedPrayersDataList: ArrayList<PrayersData>?
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

        if (SharedPrefs.contains("prayersDataList") and (1==2))
        {
            // creating a variable for gson.
            val gson = Gson()

            // get to string present from our
            // shared prefs if not present setting it as null.
            val json: String = SharedPrefs.getString("prayersDataList", null)!!
            // get the type of our array list.
            val type: Type = object : TypeToken<ArrayList<PrayersData?>?>() {}.type

            //getting data from gson
            // and saving it to our array list
            PrayersDataList = gson.fromJson<Any>(json, type) as ArrayList<PrayersData>
        }
          else {
            Toast.makeText(context, "Failed to get the prayers data..", Toast.LENGTH_SHORT)
                .show()
            callPrayersData!!.enqueue(object : Callback<ArrayList<PrayersData>?> {
                override fun onResponse(
                    call: Call<ArrayList<PrayersData>?>?,
                    response: Response<ArrayList<PrayersData>?>
                ) {

                    if (response.isSuccessful()) {
                        //get the list of the data
                        PrayersDataList = response.body()!!
                        binding.prayer3.text=PrayersDataList.toString()
                    }
                }


                override fun onFailure(call: Call<ArrayList<PrayersData>?>?, t: Throwable?) {
                    // displaying an error message in toast
                    Toast.makeText(context, "Failed to get the prayers data..", Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }
    //call function to display time left
    //timeLeftTillNextPrayer(binding, PrayersDataList)

    editedPrayersDataList=PrayersDataList
    for (i in 0..editedPrayersDataList!!.size - 1) {
        editedPrayersDataList?.get(i)?.MiladiTarihUzun =
            editedPrayersDataList?.get(i)?.MiladiTarihUzun?.split(" ")!!.get(0) + " " +
                    editedPrayersDataList?.get(i)?.MiladiTarihUzun?.split(" ")!!.get(1)
    }
        // store data in shared preferences.
        // creating a new variable for gson.
        val gson = Gson()

        // getting data from gson and storing it in a string.
        var json = gson.toJson(editedPrayersDataList)

        //save data in shared
        // prefs in the form of string.
        SharedPrefs.putString("calenderPrayersDataList", json)


        // getting data from gson and storing it in a string.
        json = gson.toJson(PrayersDataList)

        //save data in shared
        // prefs in the form of string.
        SharedPrefs.putString("prayersDataList", json)

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

    //what to do when notification button is clicked
    fun NotificationButtonIsClicked(binding: FragmentPrayersBinding) {
       binding.notification1.setOnClickListener{
           NotificationOnClickManager(binding.notification1)
           // it is a class to notify the user of events that happen.
           // This is how you tell the user that something has happened in the
           // background.
           notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


           // checking if android version is greater than oreo(API 26) or not
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
               notificationChannel.enableLights(true)
               notificationChannel.lightColor = Color.GREEN
               notificationChannel.enableVibration(false)
               notificationManager.createNotificationChannel(notificationChannel)

               builder = Notification.Builder(requireContext(), channelId)
                   .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                   .setLargeIcon(BitmapFactory.decodeResource(requireActivity().resources, R.drawable.launch_screen))
           } else {

               builder = Notification.Builder(requireContext())
                   .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                   .setLargeIcon(BitmapFactory.decodeResource(requireActivity().resources, R.drawable.launch_screen))
           }
           notificationManager.notify(1234, builder.build())
       }
        binding.notification2.setOnClickListener{
            NotificationOnClickManager(binding.notification2)
        }
        binding.notification3.setOnClickListener{
            NotificationOnClickManager(binding.notification3)
        }
        binding.notification4.setOnClickListener{
            NotificationOnClickManager(binding.notification4)
        }
        binding.notification5.setOnClickListener{
            NotificationOnClickManager(binding.notification5)
        }
        binding.notification6.setOnClickListener{
            NotificationOnClickManager(binding.notification6)
        }

    }

        //what to do when notification button is clicked
        fun NotificationOnClickManager(view: ImageView) {
        if (view.getDrawable().getConstantState() == getResources().getDrawable( R.drawable.ic_baseline_notifications_active_24).getConstantState())
        {
            view.setImageResource(R.drawable.ic_baseline_notifications_off_24)
        }
        else
            view.setImageResource(R.drawable.ic_baseline_notifications_active_24)
    }




}