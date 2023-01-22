package com.alkhatib.namazvakitleri.Fragments

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alkhatib.namazvakitleri.R
import com.alkhatib.namazvakitleri.RetrofitApi.PrayersData
import com.alkhatib.namazvakitleri.databinding.RecyclerItemBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class PrayersAdapter(private val prayersList: ArrayList<PrayersData>) : RecyclerView.Adapter<PrayersAdapter.PrayersHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayersHolder {
        val itemBinding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PrayersHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PrayersHolder, position: Int) {
        val prayersData: PrayersData
            prayersData = prayersList[position]
            holder.bind(prayersData)



    }

    override fun getItemCount(): Int = prayersList.size

    class PrayersHolder(private val itemBinding: RecyclerItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(prayersData: PrayersData) {



       /*         if (prayersData.MiladiTarihUzun?.split(" ")
                        ?.get(0).equals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd")))
                )
                {   itemBinding.apply { recyclerItem.setBackgroundColor(R.color.lightblue_app) }

            }*/



                itemBinding.apply {
                    dateTv.text = prayersData.MiladiTarihUzun
                    FajrTv.text = prayersData.Imsak
                    SunriseTv.text = prayersData.Gunes
                    DuhrTv.text = prayersData.Ogle
                    AsrTv.text = prayersData.Ikindi
                    MaghrebTv.text = prayersData.Aksam
                    IshaaTv.text = prayersData.Yatsi
                }
            }
        }
    }
