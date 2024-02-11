package com.amoebasoft.lifepilotwear.presentation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.amoebasoft.lifepilotwear.R

class ViewPagerAdapter (
    var images: MutableList<Int>
) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private var start =+ 0
    private var active = R.layout.quickdata
    //private var editor = R.layout.home:ImageView;

    companion object {
        var latestSensorValue: Float = 0f
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
        val activeLayoutRes = when (curImage) {
            R.layout.quickdata -> R.layout.quickdata
            R.layout.sync -> R.layout.sync
            R.layout.buttons -> R.layout.buttons
            else -> R.layout.quickdata // Default to quickdata layout if unknown
        }

        val inflater = LayoutInflater.from(holder.itemView.context)
        val inflatedView = inflater.inflate(activeLayoutRes, holder.itemView as ViewGroup?, false)
        (holder.itemView as? ViewGroup)?.removeAllViews()
        (holder.itemView as? ViewGroup)?.addView(inflatedView)

        // Update UI based on sensor data
        if (activeLayoutRes == R.layout.quickdata) {
            val bpmTextView = inflatedView.findViewById<TextView>(R.id.bpmtext)
            bpmTextView.text = latestSensorValue.toString()
        }
    }
    fun updateData(newImages: List<Int>) {
        //images = newImages.toMutableList()
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }
}

