package com.alkhatib.namazvakitleri.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alkhatib.namazvakitleri.RetrofitApi.PrayersData
import com.alkhatib.namazvakitleri.databinding.FragmentCalenderBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


//@AndroidEntryPoint
class CalenderFragment : Fragment() {
    // TODO (STEP 1: Add a variable for SharedPreferences)
    private lateinit var mSharedPreferences: SharedPreferences

    // TODO (STEP 2: Add the SharedPreferences name and key name for storing the response data in it.)
    val PREFERENCE_NAME = "LocationPreference"

    private lateinit var binding: FragmentCalenderBinding

    // DaggerHilt will inject the view-model for us
   // val viewModel: PrayersDataViewModel by viewModels()

    //declaring adapter for recycler view
    lateinit var prayersAdapter: PrayersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO (STEP 3: Initialize the SharedPreferences variable.)
        mSharedPreferences = requireContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)


/*
        val prayersAdapter = PrayersRecyclerAdapter()

        binding.apply {
            calenderRecyclerView.apply {
                adapter = prayersAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            viewModel.prayers.observe(requireContext()) { result ->
                PrayersRecyclerAdapter.submitList(result.data)
            }

        }*/


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentCalenderBinding.inflate(layoutInflater)

        //recycler view
        val recyclerView= binding.calenderRecyclerView

//        // creating a variable for gson.
//        val gson = Gson()
//
//        // get to string present from our
//        // shared prefs if not present setting it as null.
//        val json: String = mSharedPreferences.getString("prayersDataList", null)!!
//
//        // get the type of our array list.
//        val type: Type = object : TypeToken<ArrayList<PrayersData?>?>() {}.type
//
//        //getting data from gson
//        // and saving it to our array list
//        var prayersDataList:ArrayList<PrayersData> = gson.fromJson<Any>(json, type) as ArrayList<PrayersData>
//
//        // checking below if the array list is empty or not
//        if (prayersDataList == null) {
//            // if the array list is empty
//            // creating a new array list.
//            prayersDataList = ArrayList()
//        }
        var prayersDataList:ArrayList<PrayersData>
        prayersDataList= ArrayList()
        prayersDataList.add(
            PrayersData("fsd","df","sdfs","sf","fsd","fds",
            "fs","df","sdf"))

        //recycler view set up
        recyclerView.layoutManager = LinearLayoutManager(context)
       prayersAdapter= PrayersAdapter(prayersDataList)
        recyclerView.adapter = prayersAdapter



        //return view binging root
        return binding.root
    }





}