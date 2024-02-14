package com.amoebasoft.lifepilotwear.presentation

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.amoebasoft.lifepilotwear.R
import com.google.android.play.core.integrity.v
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
    private var mStepCountSensor : Sensor ?= null
    private var mStepDetectSensor : Sensor ?= null
    private val PERMISSION_BODY_SENSORS = Manifest.permission.BODY_SENSORS
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i("Permission: ", "Granted")
            findViewById<Button>(R.id.buttonRuntimePermission).visibility = View.GONE
        } else {
            Log.i("Permission: ", "Denied")
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, bpm: Int) {
        return
    }
    //Sensor Updates RealTime
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
                ViewPagerAdapter.heartRateSensorValue = event.values[0]
                sensorMethod()
            }
            if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                ViewPagerAdapter.stepSensorValue = event.values[0]
            }
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                ViewPagerAdapter.accelSensorValue = event.values[0]
            }
        }
    }
    fun requestPermission() {
        if (ContextCompat.checkSelfPermission(this, PERMISSION_BODY_SENSORS)
            == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            findViewById<Button>(R.id.buttonRuntimePermission).visibility = View.GONE
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION_BODY_SENSORS)) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("This app requires BODY_SENSORS permission for particular features to work as expected.")
                .setTitle("Permission Required")
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(PERMISSION_BODY_SENSORS), 100)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
            builder.show()
        } else {
            requestPermissionLauncher.launch(PERMISSION_BODY_SENSORS)
        }
    }

    //OnStartup for App
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setContentView(R.layout.home)

            sensorMethod()

            /*//slider views
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
            timeEdit.setText(curTime)*/

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
        mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        //calorie tracker inputs / data pulls from firebase
        //menBMR = 66.47 + (6.24 x weight) + (12.7 x height) - (6.755 x age)
        //womenBMR = 655.1 + (4.35 x weight) + (4.7 x height) - (4.7 x age)
    }
    //Sensor start and Stops
    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_NORMAL)
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
        else if(id == R.id.buttonRuntimePermission) {
            requestPermission()
            findViewById<Button>(R.id.buttonRuntimePermission).visibility = View.GONE
        }
    }
    //Update Sensor UI with PageViewer from Sensor Updates
    private fun sensorMethod() {
        setContentView(R.layout.home)

        if (ContextCompat.checkSelfPermission(this, PERMISSION_BODY_SENSORS)
            == PackageManager.PERMISSION_GRANTED) {
            findViewById<Button>(R.id.buttonRuntimePermission).visibility = View.GONE
        }

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
