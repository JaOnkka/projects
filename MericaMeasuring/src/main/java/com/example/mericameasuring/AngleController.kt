package com.example.mericameasuring

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class AngleController(
    private val view: AngleView,
    private val sensorManager: SensorManager,
    private val accelerometer: Sensor
) : SensorEventListener {

    private var currentAngle: Double = 0.0

    init {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    //when the sensor senses a change, the angles get updated and sent to angleview to display results
    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        //horizontalangle is tilt of xz axis
        //y is gravity, pythagorean theorem: sqrt(x^2 + z^2) is the projection of gravity vector onto xz plane
        //then find angle between y and
        val horizontalAngle = Math.toDegrees(atan2(y.toDouble(), sqrt(x * x + z * z).toDouble()))
        //verticalangle is tilt of xy axis
        //same as horizontal except yz axis, so sqrt(x * x + y * y) is the magnitude of gravity vector
        val verticalAngle = Math.toDegrees(atan2(sqrt(x * x + y * y).toDouble(), z.toDouble()))

        //check if within 2.5 degrees to consider true
        val isHorizontalLevel = abs(horizontalAngle - currentAngle) < 2.5
        var isVerticalLevel = abs(verticalAngle - (90 - currentAngle)) < 2.5
        if (currentAngle > 0.0) isVerticalLevel = abs(verticalAngle - currentAngle) < 2.5   //edge case for vertical

        if (isHorizontalLevel && isVerticalLevel) {
            view.setStatusText("Perfectly Level")
            view.setBackgroundColor(R.color.blue)
            view.setLevelIndicatorColor(R.color.blue)
        } else if (isHorizontalLevel) {
            view.setStatusText("Horizontal Level")
            view.setBackgroundColor(R.color.green)
            view.setLevelIndicatorColor(R.color.green)
        } else if (isVerticalLevel) {
            view.setStatusText("Vertical Level")
            view.setBackgroundColor(R.color.green)
            view.setLevelIndicatorColor(R.color.green)
        } else {
            view.setStatusText("Not Level")
            view.setBackgroundColor(R.color.red)
            view.setLevelIndicatorColor(R.color.red)
        }

        view.setHorizontalAngleText("Horizontal: ${"%.2f".format(horizontalAngle)} degrees")
        view.setVerticalAngleText("Vertical: ${"%.2f".format(verticalAngle)} degrees")
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    fun updateAngle(angle: Double) {
        currentAngle = angle
    }

    fun unregisterListener() {
        sensorManager.unregisterListener(this)
    }
}
