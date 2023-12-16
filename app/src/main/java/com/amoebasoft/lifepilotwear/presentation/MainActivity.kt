package com.amoebasoft.lifepilotwear.presentation

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.viewpager2.widget.ViewPager2
import com.amoebasoft.lifepilotwear.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : ComponentActivity(), View.OnClickListener {

    //Google Sign in variables
    //var gso: GoogleSignInOptions? = null
    //var gsc: GoogleSignInClient? = null
    //var account: GoogleSignInAccount? = null
    //private var mAuth: FirebaseAuth? = null
    //var user: FirebaseUser? = null
    //var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setContentView(R.layout.home)
            val images = listOf(
                R.drawable.blank1,
                R.drawable.blank2,
                R.drawable.blank3
            )
            val adapter = ViewPagerAdapter(images)
            findViewById<ViewPager2>(R.id.viewPager).adapter = adapter

            //Google Sign In variables
            //gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //    .requestIdToken(getString(R.string.server_client_id)).requestEmail().build()
            //gsc = GoogleSignIn.getClient(this, gso)
            //mAuth = FirebaseAuth.getInstance()
            //user = mAuth.getCurrentUser() //is null if user is not signed in
            //account = GoogleSignIn.getLastSignedInAccount(this) //is null if user is not signed in
        }

        //Sensor Requirements
        val mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        val mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        //calorie tracker inputs / data pulls from firebase
        //menBMR = 66.47 + (6.24 x weight) + (12.7 x height) - (6.755 x age)
        //womenBMR = 655.1 + (4.35 x weight) + (4.7 x height) - (4.7 x age)

        //val test = findViewById<EditText>(R.id.time)
        //test.setHint(mHeartRateSensor.toString())
    }

    override fun onClick(view: View?) {
        val id = view?.id

        if(id == R.id.button5) {
            setContentView(R.layout.exercises)
        }

    }

}
