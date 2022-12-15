package com.alkhatib.namazvakitleri.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import java.lang.Boolean

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView

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
        if(item.itemId==R.id.locationMi)
        {
        val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
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

}