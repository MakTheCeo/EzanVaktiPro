package com.alkhatib.namazvakitleri

import android.accounts.AccountManager.get
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.*
import com.alkhatib.namazvakitleri.Activities.LocationActivity
import com.alkhatib.namazvakitleri.Fragments.CalenderFragment
import com.alkhatib.namazvakitleri.Fragments.CompassFragment
import com.alkhatib.namazvakitleri.Fragments.PrayersFragment
import com.alkhatib.namazvakitleri.Fragments.PrayersFragment.ConnectivityUtils.isOnline
import com.alkhatib.namazvakitleri.RetrofitApi.PrayersData

import com.alkhatib.namazvakitleri.databinding.ActivityMainBinding
import com.alkhatib.namazvakitleri.databinding.FragmentPrayersBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Array.get
import java.lang.reflect.Type
import java.nio.file.Paths.get
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    lateinit var bottomNav: BottomNavigationView
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
        if (mSharedPreferences.contains("District") and mSharedPreferences.contains("Executed")) {

            binding = ActivityMainBinding.inflate(layoutInflater)
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


        } else {
            moveToLocationActivity()
        }

    }

    //inflate menu
    override fun onCreatePanelMenu(featureId: Int, menu: Menu): kotlin.Boolean {
        var inflater: MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): kotlin.Boolean {

        if (item.itemId == R.id.locationMi && isOnline(this)) {

            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.privacyPolicyMi && isOnline(this)) {

            var gourl = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.freeprivacypolicy.com/live/121ad82e-acbd-49f6-a3c7-cd317816039b")
            )
            startActivity(gourl)

        } else {
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
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }



    //when back btn pressed
    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}