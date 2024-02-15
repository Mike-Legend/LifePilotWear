package com.amoebasoft.lifepilotwear.presentation

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.amoebasoft.lifepilotwear.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity(), View.OnClickListener, SensorEventListener {

    //Google Sign in variables
    var gso: GoogleSignInOptions? = null
    var gsc: GoogleSignInClient? = null
    var account: GoogleSignInAccount? = null
    private var mAuth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    //Initialize Sensor Data
    private val ALPHA = 0.8f
    private val STEP_THRESHOLD = 8
    private val STEP_DELAY_NS = 250000000
    private lateinit var mSensorManager : SensorManager
    private var mHeartRateSensor : Sensor ?= null
    private var mStepDetectSensor : Sensor ?= null
    private val PERMISSION_BODY_SENSORS = Manifest.permission.BODY_SENSORS
    private var lastStepTimeNs: Long = 0
    private var stepCount: Int = 0
    private var start = 0
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
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val currentTimeNs = System.nanoTime()
                if (currentTimeNs - lastStepTimeNs >= STEP_DELAY_NS) {
                    // Low-pass filter to smooth out accelerometer data
                    val gravity = floatArrayOf(0f, 0f, 0f)
                    val linearAcceleration = floatArrayOf(0f, 0f, 0f)
                    gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0]
                    gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1]
                    gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2]
                    linearAcceleration[0] = event.values[0] - gravity[0]
                    linearAcceleration[1] = event.values[1] - gravity[1]
                    linearAcceleration[2] = event.values[2] - gravity[2]
                    // Magnitude of the acceleration vector
                    val accelerationMagnitude = Math.sqrt(
                        (linearAcceleration[0] * linearAcceleration[0] +
                                linearAcceleration[1] * linearAcceleration[1] +
                                linearAcceleration[2] * linearAcceleration[2]).toDouble()
                    ).toFloat()
                    // Check for step and update
                    if (accelerationMagnitude > STEP_THRESHOLD) {
                        stepCount++
                        lastStepTimeNs = currentTimeNs
                        ViewPagerAdapter.accelSensorValue = stepCount
                        sensorMethod()
                    }
                }
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

            //Google Sign In variables using dummy parameters for now
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id)).requestEmail().build()
            gsc = GoogleSignIn.getClient(this, gso!!)
            mAuth = FirebaseAuth.getInstance()
            user = mAuth!!.getCurrentUser() //is null if user is not signed in
            account = GoogleSignIn.getLastSignedInAccount(this) //is null if user is not signed in
        }

        //Sensor Requirements
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        //calorie tracker inputs / data pulls from firebase / using dummy data for now
        //menBMR = 66.47 + (6.24 x 160) + (12.7 x 70) - (6.755 x 28) = 1764.13
        //womenBMR = 655.1 + (4.35 x weight) + (4.7 x height) - (4.7 x age)
    }
    //Sensor start and Stops
    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }
    //OnClicks for home buttons
    override fun onClick(view: View?) {
        val id = view?.id
        if(id == R.id.buttonSync) {
            findViewById<Button>(R.id.buttonSync).visibility = View.GONE
            findViewById<FrameLayout>(R.id.workoutstart).visibility = View.VISIBLE
        }
        else if(id == R.id.checkbutton1) {
            findViewById<Button>(R.id.workoutbutton1).setBackgroundColor(getResources().getColor(R.color.passGreen))
        }
        else if(id == R.id.xbutton1) {
            findViewById<Button>(R.id.workoutbutton1).setBackgroundColor(getResources().getColor(R.color.deleteRed))
        }
        else if(id == R.id.checkbutton2) {
            findViewById<Button>(R.id.workoutbutton2).setBackgroundColor(getResources().getColor(R.color.passGreen))
        }
        else if(id == R.id.xbutton2) {
            findViewById<Button>(R.id.workoutbutton2).setBackgroundColor(getResources().getColor(R.color.deleteRed))
        }
        else if(id == R.id.buttonRuntimePermission) {
            requestPermission()
            findViewById<Button>(R.id.buttonRuntimePermission).visibility = View.GONE
        }
        else if(id == R.id.buttonRunning) {

        }
        else if(id == R.id.buttonStopwatch) {
            setContentView(R.layout.timer)

        }
        else if(id == R.id.buttonUser) {

        }
        else if(id == R.id.buttonSettings) {

        }
    }
    //Update Sensor UI with PageViewer from Sensor Updates
    private fun sensorMethod() {
        setContentView(R.layout.home)
        //permission recheck on load
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

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //navigation update
                if(position == 0) {
                    findViewById<ImageView>(R.id.maindot1).visibility = View.VISIBLE
                    findViewById<ImageView>(R.id.maindot2).visibility = View.GONE
                    findViewById<ImageView>(R.id.maindot3).visibility = View.GONE
                } else if (position == 1) {
                    findViewById<ImageView>(R.id.maindot1).visibility = View.GONE
                    findViewById<ImageView>(R.id.maindot2).visibility = View.VISIBLE
                    findViewById<ImageView>(R.id.maindot3).visibility = View.GONE
                } else if (position == 2) {
                    findViewById<ImageView>(R.id.maindot1).visibility = View.GONE
                    findViewById<ImageView>(R.id.maindot2).visibility = View.GONE
                    findViewById<ImageView>(R.id.maindot3).visibility = View.VISIBLE
                }
            }
        })
        adapter.notifyDataSetChanged()
        viewPager.adapter = adapter

        if(start == 0) {
            start += 1
            viewPager.currentItem = 1
        }

        // Set home time
        val timeEdit = findViewById<EditText>(R.id.time)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val curTime = LocalDateTime.now().format(formatter)
        timeEdit.setText(curTime)
    }
    private fun setupViewPagerListener() {
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sensorMethod()
            }
        })
    }
}
