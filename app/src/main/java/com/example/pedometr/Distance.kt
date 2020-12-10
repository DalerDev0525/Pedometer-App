package com.example.pedometr

import com.example.pedometr.step.StepListener
import com.example.pedometr.utils.PedometerSettings


abstract class Distance : StepListener {


    interface Listener {
        fun valueChanged(value: Float)
        fun passValue()
    }

    private var mListener: Listener? = null
    private var mDistance: Float = 0f
    var mSettings: PedometerSettings? = null


    var mIsMetric = false
    var mStepLength = 0f

    fun DistanceNotifier(listener: Listener, settings: PedometerSettings) {
        mListener = listener
        mSettings = settings
        reloadSettings()
    }

    private fun reloadSettings() {
        mIsMetric = mSettings!!.isMetric()
        mStepLength = mSettings!!.getStepLength()
        notifyListener()
    }

    fun setDistance(distance: Float) {
        mDistance = distance
        notifyListener()
    }

    override fun step(timeNs: Long) {
        mDistance += if (mIsMetric) {
            (// kilometers
                    mStepLength // centimeters
                            / 100000.0) as Float; // centimeters/kilometer        }
        } else {
            (// miles
                    mStepLength // inches
                            / 63360.0) as Float
        }
        notifyListener()

    }

    private fun notifyListener() {
        mListener!!.valueChanged(mDistance)
    }


}