package com.alkhatib.namazvakitleri.Activities

import android.os.Bundle
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

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //view binding
        var binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


    //loading fragment function
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}