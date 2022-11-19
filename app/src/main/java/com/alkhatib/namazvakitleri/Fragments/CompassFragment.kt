package com.alkhatib.namazvakitleri.Fragments

import android.content.Context
import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import com.alkhatib.namazvakitleri.R
import com.alkhatib.namazvakitleri.databinding.FragmentCompassBinding


class CompassFragment : Fragment(), SensorEventListener {
    //declare vars
    private var currentDegree=0f
    private lateinit var mvalue:Sensor
    //view binding
    private lateinit var binding: FragmentCompassBinding

    // device sensor manager
    private var mSensorManager:SensorManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
initData()
    }
private fun initData(){
    mSensorManager= requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager?
}
    override fun onResume() {
        super.onResume()

        @Suppress("DEPRECATION")
       mSensorManager?.registerListener(this,mSensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)
            ,SensorManager.SENSOR_DELAY_GAME)!!
        mvalue= mSensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)!!
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
        val rotateAnimation = RotateAnimation(currentDegree-165,(degree).toFloat(),Animation.RELATIVE_TO_SELF
        ,0.5f,Animation.RELATIVE_TO_SELF,0.5f)

        rotateAnimation.duration=500
        rotateAnimation.fillAfter=true

        binding.compassIv.startAnimation(rotateAnimation)

        binding.deviation.text=(currentDegree-164).toString()+"°"
        if(currentDegree-164>3)
            binding.arrowDirectionIv.setImageResource(R.drawable.ic_baseline_arrow_back_24)
        else if(currentDegree-164<-3)
            binding.arrowDirectionIv.setImageResource(R.drawable.ic_baseline_arrow_forward_24)
        else
            binding.arrowDirectionIv.setImageResource(R.drawable.ic_baseline_check_circle_24)



        currentDegree= (degree).toFloat()

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
        if (sensor === mvalue) {
            when (accuracy) {
                0 -> {
                    binding.accuracyTv.text="Güvenilmez"

                }
                1 -> {
                    binding.accuracyTv.text="Düşük"

                }
                2 -> {
                    binding.accuracyTv.text="Orta"

                }
                3 -> {
                    binding.accuracyTv.text="Yüksek"

                }
            }
        }
    }


}