package com.example.pedometr.utils

import android.content.SharedPreferences

class PedometerSettings {
    var mSettings: SharedPreferences? = null

    fun PedometerSettings(settings: SharedPreferences) {
        mSettings = settings
    }

    fun isMetric(): Boolean {
        return mSettings!!.getString("units", "imperial") == "metric"
    }

    fun getStepLength(): Float {
        return try {
            java.lang.Float.valueOf(mSettings!!.getString("step_length", "20")!!.trim { it <= ' ' })
        } catch (e: NumberFormatException) {
            0f
        }
    }



    fun shouldTellDistance(): Boolean {
        return (mSettings!!.getBoolean("speak", false)
                && mSettings!!.getBoolean("tell_distance", false))
    }



    fun shouldTellCalories(): Boolean {
        return (mSettings!!.getBoolean("speak", false)
                && mSettings!!.getBoolean("tell_calories", false))
    }

}