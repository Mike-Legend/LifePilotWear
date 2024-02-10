package com.amoebasoft.lifepilotwear.presentation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amoebasoft.lifepilotwear.R

class ViewPagerAdapter (
    val images: List<Int>
) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private var start =+ 0
    private var active = R.layout.quickdata
    //private var editor = R.layout.home:ImageView;

    fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
                //setContentView(R.layout.quickdata)
                findViewById<TextView>(R.id.bpmtext).text = event.values[0].toString()
                //val str: String = textView.text.toString()
                //Integer.toString(sensorEvent.sensor.getType())
                //showHeartRate.setText(heartRateValue)

                //val bp: TextView = findViewById(R.id.bpmtext)
                //bp.setOnClickListener {
                //bp.setText(event.values[0].toString())

                //}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        if(start == 0) {
            val view = LayoutInflater.from(parent.context).inflate(active, parent, false)
            start += 1
            return ViewPagerViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(active, parent, false)
        /*if (active == R.layout.sync) {
            home = (context as MainActivity).findViewById<ImageView>(R.id.maindot2)
        }*/
        return ViewPagerViewHolder(view)
    }
    override fun getItemCount(): Int {
        return images.size
    }
    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val curImage = images[position]
        if (curImage == images[0]) {
            active = R.layout.sync
        } else if (curImage == images[1]) {
            active = R.layout.buttons
        } else if (curImage == images[2]) {
            active = R.layout.sync
        }
        holder.itemView.setBackgroundResource(curImage)
    }
}

