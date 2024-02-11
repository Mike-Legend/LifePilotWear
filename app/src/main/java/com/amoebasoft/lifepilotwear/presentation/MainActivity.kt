package com.amoebasoft.lifepilotwear.presentation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.viewpager2.widget.ViewPager2
import com.amoebasoft.lifepilotwear.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity(), View.OnClickListener, SensorEventListener {

    //Google Sign in variables
    //var gso: GoogleSignInOptions? = null
    //var gsc: GoogleSignInClient? = null
    //var account: GoogleSignInAccount? = null
    //private var mAuth: FirebaseAuth? = null
    //var user: FirebaseUser? = null
    //var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    //Initialize Sensor Data
    private lateinit var mSensorManager : SensorManager
    private var mHeartRateSensor : Sensor ?= null
    override fun onAccuracyChanged(sensor: Sensor?, bpm: Int) {
        return
    }
    //Sensor Updates RealTime
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
                ViewPagerAdapter.latestSensorValue = event.values[0]
                sensorMethod()
            }
        }
    }
    //OnStartup for App
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setContentView(R.layout.home)

            //slider views
            val images = mutableListOf(
                R.layout.quickdata,
                R.layout.sync,
                R.layout.buttons
            )
            val adapter = ViewPagerAdapter(images)
            findViewById<ViewPager2>(R.id.viewPager).adapter = adapter
            findViewById<ViewPager2>(R.id.viewPager).currentItem = 1

            //set home time
            val timeEdit = findViewById<EditText>(R.id.time)
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val curTime = LocalDateTime.now().format(formatter)
            timeEdit.setText(curTime)

            //Google Sign In variables
            //gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //    .requestIdToken(getString(R.string.server_client_id)).requestEmail().build()
            //gsc = GoogleSignIn.getClient(this, gso)
            //mAuth = FirebaseAuth.getInstance()
            //user = mAuth.getCurrentUser() //is null if user is not signed in
            //account = GoogleSignIn.getLastSignedInAccount(this) //is null if user is not signed in
        }

        //Sensor Requirements
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        //val mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        //val mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        //calorie tracker inputs / data pulls from firebase
        //menBMR = 66.47 + (6.24 x weight) + (12.7 x height) - (6.755 x age)
        //womenBMR = 655.1 + (4.35 x weight) + (4.7 x height) - (4.7 x age)
    }
    //Sensor start and Stops
    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }
    //OnClicks for home buttons
    override fun onClick(view: View?) {
        val id = view?.id
        if(id == R.id.button5) {
            setContentView(R.layout.exercises)
        }
    }
    //Update Sensor UI with PageViewer from Sensor Updates
    private fun sensorMethod() {
        setContentView(R.layout.home)

        // Get ViewPager reference
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        // Set adapter for ViewPager
        val images = mutableListOf(
            R.layout.quickdata,
            R.layout.sync,
            R.layout.buttons
        )
        val adapter = ViewPagerAdapter(images)
        viewPager.adapter = adapter
        viewPager.currentItem = 1

        // Set home time
        val timeEdit = findViewById<EditText>(R.id.time)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val curTime = LocalDateTime.now().format(formatter)
        timeEdit.setText(curTime)
    }
}
