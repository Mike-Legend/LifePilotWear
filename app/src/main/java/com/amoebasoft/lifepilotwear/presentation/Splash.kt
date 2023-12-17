package com.amoebasoft.lifepilotwear.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import com.amoebasoft.lifepilotwear.R

class Splash : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        //Splash Screen
        Handler().postDelayed(Runnable() {

            val intent = Intent(this@Splash, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}