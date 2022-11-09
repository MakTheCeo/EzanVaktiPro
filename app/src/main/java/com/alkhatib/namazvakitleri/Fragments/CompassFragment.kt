package com.alkhatib.namazvakitleri.Fragments

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat.getSystemService
import com.alkhatib.namazvakitleri.databinding.FragmentCompassBinding
import java.lang.reflect.Type


class CompassFragment : Fragment(), SensorEventListener {
    //declare vars
    private var currentDegree=0f

    //view binding
    private lateinit var binding: FragmentCompassBinding

    // device sensor manager
    private var mSensorManager:SensorManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
private fun initData(){
    mSensorManager= requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager?
}
    override fun onResume() {
        super.onResume()
        @Suppress("DEPRECATION")
        mSensorManager?.registerListener(this,mSensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        ,SensorManager.SENSOR_DELAY_GAME)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCompassBinding.inflate(layoutInflater)

        //return view binging root
        return binding.root
    }


    override fun onSensorChanged(event: SensorEvent?) {
    val degree =Math.round(event?.values?.get(0)!!)
        val rotateAnimation = RotateAnimation(currentDegree,(-degree).toFloat(),Animation.RELATIVE_TO_SELF
        ,0.5f,Animation.RELATIVE_TO_SELF,0.5f)

        rotateAnimation.duration=210
        rotateAnimation.fillAfter=true

        binding.compassIV.startAnimation(rotateAnimation)
        currentDegree= (-degree).toFloat()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


}