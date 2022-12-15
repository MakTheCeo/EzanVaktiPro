package com.alkhatib.namazvakitleri.Notifications

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.widget.ImageView
import com.alkhatib.namazvakitleri.R

class ConfirmationDialog(context: Context) : AlertDialog.Builder(context) {
    // TODO (STEP 1: Add a variable for SharedPreferences)
    private lateinit var mSharedPreferences: SharedPreferences

    // TODO (STEP 2: Add the SharedPreferences name and key name for storing the response data in it.)
    val PREFERENCE_NAME = "LocationPreference"


    lateinit var onResponse: (r : ResponseType) -> Unit

    enum class ResponseType {
        YES, NO, CANCEL
    }

    fun show(title: String, message: String,view:ImageView, listener: (r : ResponseType) -> Unit) {
        // TODO (STEP 3: Initialize the SharedPreferences variable.)
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        if (mSharedPreferences.getBoolean("${view.id}",true))
            {
        builder.setIcon(R.drawable.ic_baseline_notifications_off_24)}
            else
        {  builder.setIcon(R.drawable.ic_baseline_notifications_active_24)}

    onResponse = listener

        // performing positive action
        builder.setPositiveButton("Evet") { _, _ ->
            onResponse(ResponseType.YES)
            val editor = mSharedPreferences.edit()
            if (mSharedPreferences.getBoolean("${view.id}",true)) {

                view.setImageResource(R.drawable.ic_baseline_notifications_off_24)

                //update shared prefs
                editor.putBoolean("${view.id}", false)

            } else
            {
                view.setImageResource(R.drawable.ic_baseline_notifications_active_24)
                //update shared prefs
                editor.putBoolean("${view.id}",true)
            }
            editor.apply()
        }

        // performing negative action
        builder.setNegativeButton("Hayır") { _, _ ->
            onResponse(ResponseType.NO)
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()

        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}