package com.example.mericameasuring

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var controller: AngleController    //handles all angle measuring
    private lateinit var button0: Button
    private lateinit var button30: Button
    private lateinit var button45: Button
    private lateinit var button60: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = AngleView(
            findViewById(android.R.id.content),
            findViewById(R.id.statusTextView),
            findViewById(R.id.horizontalAngleTextView),
            findViewById(R.id.verticalAngleTextView),
            findViewById(R.id.levelIndicator),
            listOf(findViewById(R.id.button0), findViewById(R.id.button30), findViewById(R.id.button45), findViewById(R.id.button60))
        )


        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer != null) {
            controller = AngleController(view, sensorManager, accelerometer)
        } else {
            view.setStatusText("Sensor not available")
        }

        //angle buttons, user clicks, set button active and set the angle
        button0 = findViewById(R.id.button0)
        button30 = findViewById(R.id.button30)
        button45 = findViewById(R.id.button45)
        button60 = findViewById(R.id.button60)

        view.setSelectedButton(button0)

        button0.setOnClickListener {
            view.setSelectedButton(button0)
            controller.updateAngle(0.0)
        }
        button30.setOnClickListener {
            view.setSelectedButton(button30)
            controller.updateAngle(30.0)
        }
        button45.setOnClickListener {
            view.setSelectedButton(button45)
            controller.updateAngle(45.0)
        }
        button60.setOnClickListener {
            view.setSelectedButton(button60)
            controller.updateAngle(60.0)
        }
    }

    override fun onPause() {
        super.onPause()
        controller.unregisterListener()
    }
}

