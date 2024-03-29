package com.alkhatib.namazvakitleri.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.alkhatib.namazvakitleri.MainActivity
import com.alkhatib.namazvakitleri.RetrofitApi.City
import com.alkhatib.namazvakitleri.RetrofitApi.District
import com.alkhatib.namazvakitleri.RetrofitApi.RetrofitAPI
import com.alkhatib.namazvakitleri.RetrofitApi.SharedPrefs
import com.alkhatib.namazvakitleri.databinding.ActivityLocationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Boolean


class LocationActivity : AppCompatActivity() {
    val website = "https://ezanvakti.herokuapp.com/"

    // TODO (STEP 1: Add a variable for SharedPreferences)
    private lateinit var mSharedPreferences: SharedPreferences

    // TODO (STEP 2: Add the SharedPreferences name and key name for storing the response data in it.)
    val PREFERENCE_NAME = "LocationPreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //data binding
        var binding: ActivityLocationBinding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO (STEP 3: Initialize the SharedPreferences variable.)
        mSharedPreferences =
            this.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

        binding.checkboxTextTV.setOnClickListener{
                var gourl= Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freeprivacypolicy.com/live/121ad82e-acbd-49f6-a3c7-cd317816039b"))
                startActivity(gourl)
        }
        if (mSharedPreferences.contains("Executed") )
        {
            binding.checkBox.visibility = View.GONE
            binding.warningTV.visibility = View.GONE
            binding.checkboxTextTV.visibility = View.GONE
        }

        // get data from api using retrofit.
        getCity(binding, this)


        //switch to main activity from now on.
        binding.nextBtn.setOnClickListener {

            if (binding.checkBox.isChecked or !binding.checkBox.isVisible) {

                moveToMain()
                if (!mSharedPreferences.contains("Executed"))
                {
                    val editor = mSharedPreferences.edit()

                    editor.putBoolean(
                        "Executed",
                        true
                        )
                    editor.apply()
                }

            }
            else if (!binding.warningTV.isVisible){
                binding.warningTV.visibility = View.VISIBLE
            }

            //reset location
            if (mSharedPreferences.contains("PrayersDataList") )
            {

            val editor = mSharedPreferences.edit()

            editor.remove(
                "PrayersDataList",

            )
            editor.apply()
            }

        }
    }



    //switch to main activity
    fun moveToMain() {
        // use an intent to travel from one activity to another.
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    //get cities using retrofit and fill the city spinner
    private fun getCity(binding: ActivityLocationBinding, context: Context): Int {

        //declaring variables
        var CityList: ArrayList<City>?
        var cityName: ArrayList<String>?
        var selectedCityId: Int = -1

        //initializing variables
        CityList = ArrayList()
        cityName = ArrayList()

        // creating a retrofit builder and passing our base url
        val retrofit = Retrofit.Builder()
            .baseUrl("$website")

            //calling add Converter factory as GSON converter factory.
            // at last we are building our retrofit builder.
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        // below line is to create an instance for our retrofit api class.
        val retrofitAPI = retrofit.create(RetrofitAPI::class.java)

        val callCity: Call<ArrayList<City>?>? = retrofitAPI.getCity()

        // on below line we are making a call.
        callCity!!.enqueue(object : Callback<ArrayList<City>?> {
            override fun onResponse(
                call: Call<ArrayList<City>?>?,
                response: Response<ArrayList<City>?>
            ) {
                if (response.isSuccessful()) {

                    CityList = response.body()

                    for (i in 0..CityList!!.size - 1) {
                        cityName.add(CityList?.get(i)?.SehirAdi.toString())
                    }

                    val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_spinner_item, cityName
                    )

                    binding.citySpinner.adapter = adapter

                    selectedCityId =
                        CityList!!.get(binding.citySpinner.selectedItemPosition).SehirID!!.toInt()

                    binding.citySpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(p0: AdapterView<*>?) {
                                getDistrict(binding, context, 2)
                            }

                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {


                                selectedCityId = CityList?.get(p2)?.SehirID!!.toInt()

                                //calling get district to fill the spinner with the correct district
                                getDistrict(binding, context, selectedCityId)

                                //save city name to shared preferences
                                val editor = mSharedPreferences.edit()
                                editor.putString("City", CityList?.get(p2)?.SehirAdi.toString())
                                editor.apply()
                            }
                        }
                }
            }

            override fun onFailure(call: Call<ArrayList<City>?>?, t: Throwable?) {
                // displaying an error message in toast
                Toast.makeText(context, "Fail to get the city data..", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        return selectedCityId
    }

    //get the district data by retrofit and fill the district spinner
    private fun getDistrict(binding: ActivityLocationBinding, context: Context, cityId: Int) {
        //declare vars
        var DistrictList: ArrayList<District>?
        var districtName: ArrayList<String>?

        //initialize vars
        DistrictList = ArrayList()
        districtName = ArrayList()


        // on below line we are creating a retrofit builder and passing our base url
        val retrofit = Retrofit.Builder()
            .baseUrl("$website")

            //  calling add Converter factory as GSON converter factory.
            // at last we are building our retrofit builder.
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // below line is to create an instance for our retrofit api class.
        val retrofitAPI = retrofit.create(RetrofitAPI::class.java)

        val callDistrict: Call<ArrayList<District>?>? = retrofitAPI.getDistrict(cityId)

        //making a call.
        callDistrict!!.enqueue(object : Callback<ArrayList<District>?> {
            override fun onResponse(
                call: Call<ArrayList<District>?>?,
                response: Response<ArrayList<District>?>
            ) {
                if (response.isSuccessful()) {

                    DistrictList = response.body()

                    for (i in 0..DistrictList!!.size - 1) {
                        districtName.add(DistrictList?.get(i)?.IlceAdi.toString())
                    }

                    val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_spinner_item, districtName
                    )

                    //fill spinner with districts names
                    binding.districtSpinner.adapter = adapter



                    binding.districtSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val editor = mSharedPreferences.edit()
                            editor.putInt(
                                "DistrictCode",
                                DistrictList?.get(binding.districtSpinner.selectedItemPosition)?.IlceID!!.toInt()
                            )
                            editor.putString(
                                "District",
                                binding.districtSpinner.selectedItem.toString()
                            )
                            editor.apply()



                }
            }}}
            //what if retrofit failed?
            override fun onFailure(call: Call<ArrayList<District>?>?, t: Throwable?) {
                // displaying an error message in toast
                Toast.makeText(context, "Fail to get the district data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        finish()

    }
}