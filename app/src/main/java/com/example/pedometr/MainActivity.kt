package com.example.pedometr

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.example.pedometr.step.StepDetector
import com.example.pedometr.step.StepListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SensorEventListener, StepListener {
    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    private var accel: Sensor? = null
    private var numSteps = 0
    private var defaultCount = 0
    private var distance1 = 0
    private var paused = true
    private var timeWhenStopped: Long = 0
    private var METRIC_RUNNING_FACTOR = 1.02784823
    private var METRIC_WALKING_FACTOR = 0.708
    private var mCalories = 0.0
    var mIsRunning = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?
        accel = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        simpleStepDetector = StepDetector()
        simpleStepDetector?.registerListener(this)


        chronometer.setOnChronometerTickListener {
            val time = SystemClock.elapsedRealtime() - chronometer.base
            val h = (time / 3600000).toInt()
            val m = (time - h * 3600000).toInt() / 60000
            val s = (time - h * 3600000 - m * 60000).toInt() / 1000
            val t =
                (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
            chronometer.text = t
        }
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.text = "00:00:00"
        btn.setOnClickListener {

            if (paused) {
                if (tv_stepsTaken.text == "$defaultCount") {
                    btn.text = "Рестарт"
                    distance.text = "0.0"
                    numSteps = 0
                    sensorManager?.registerListener(
                        this,
                        accel,
                        SensorManager.SENSOR_DELAY_FASTEST
                    )
                    chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
                    chronometer.start()
                    paused = false
                }

            } else {
                paused = true
                sensorManager?.unregisterListener(this)
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.stop()
                distance.text = "0.0"
                calories.text = "0.0"
                btn.text = "Старт"
                tv_stepsTaken.text = "0"
            }
        }

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector?.updateAccel(
                event.timestamp, event.values[0], event.values[1], event.values[2]
            )
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun step(timeNs: Long) {
        numSteps++
        tv_stepsTaken.text = ("$numSteps")
        mCalories += (70 * numSteps / 1000000.0)
        val a = "$mCalories".substring(0, 4)
        calories.text = a
        distance.text = getDistanceRun(numSteps.toDouble()).toString().substring(0,4)

    }

    private fun getDistanceRun(steps: Double): Double {
        return (steps * 78) / 100000.toDouble()
    }


    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            this,
            accel,
            SensorManager.SENSOR_DELAY_FASTEST
        )

    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }


}
