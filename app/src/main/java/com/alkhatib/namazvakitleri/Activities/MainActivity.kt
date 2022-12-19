package com.alkhatib.namazvakitleri.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.alkhatib.namazvakitleri.Fragments.CalenderFragment
import com.alkhatib.namazvakitleri.Fragments.CompassFragment
import com.alkhatib.namazvakitleri.Fragments.PrayersFragment
import com.alkhatib.namazvakitleri.R
import com.alkhatib.namazvakitleri.RetrofitApi.SharedPrefs
import com.alkhatib.namazvakitleri.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import android.app.*
import com.alkhatib.namazvakitleri.Notifications.channelID
import com.alkhatib.namazvakitleri.Notifications.messageExtra
import com.alkhatib.namazvakitleri.Notifications.notificationID
import com.alkhatib.namazvakitleri.Notifications.titleExtra
import java.util.*
import android.os.Bundle
import android.widget.Toast
import com.alkhatib.namazvakitleri.Fragments.PrayersFragment.ConnectivityUtils.isOnline

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    lateinit var bottomNav : BottomNavigationView
    //view binding

    // TODO (STEP 1: Add a variable for SharedPreferences)
    private lateinit var mSharedPreferences: SharedPreferences

    // TODO (STEP 2: Add the SharedPreferences name and key name for storing the response data in it.)
    val PREFERENCE_NAME = "LocationPreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO (STEP 3: Initialize the SharedPreferences variable.)
        mSharedPreferences =
            this.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        if (mSharedPreferences.contains("District")) {

            binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //notifications
          /*  createNotificationChannel()
            binding.submitButton.setOnClickListener{
                scheduleNotification()
            }
*/
        //default home fragment
        loadFragment(PrayersFragment())

        //assigning value to bottom nav
        bottomNav = binding.bottomNav as BottomNavigationView

        //loading the fragment based on navigation bar clicks...
        bottomNav.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.prayers -> {
                    loadFragment(PrayersFragment())
                    return@OnItemSelectedListener true
                }
                R.id.calender -> {
                    loadFragment(CalenderFragment())
                    return@OnItemSelectedListener true
                }
                R.id.compass -> {
                    loadFragment(CompassFragment())
                    return@OnItemSelectedListener true
                }
            }
            false
        })


    }
    else{
            moveToLocationActivity()}

    }
    //inflate menu
    override fun onCreatePanelMenu(featureId: Int, menu: Menu): kotlin.Boolean {
    var inflater:MenuInflater=getMenuInflater()
    inflater.inflate(R.menu.options_menu,menu)
    return true
    }

    override fun onOptionsItemSelected(item: MenuItem): kotlin.Boolean {

        if(item.itemId==R.id.locationMi && isOnline(this))
        {

        val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }
        else{
            Toast.makeText(this, "İnternet bağlantısı yok", Toast.LENGTH_SHORT)
                .show()
        }
        return true
    }
    //switch to Location activity
    fun moveToLocationActivity() {
        // use an intent to travel from one activity to another.
        val intent = Intent(this, LocationActivity::class.java)
        startActivity(intent)

    }


    //loading fragment function
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    //schedule a notification
   /* private fun scheduleNotification()
    {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = "Ezan vakti pro"
        val message = "Akşam"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String)
    {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->}
            .show()
    }

    private fun getTime(): Long
    {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
*/
}