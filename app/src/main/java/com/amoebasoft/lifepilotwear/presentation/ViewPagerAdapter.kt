package com.amoebasoft.lifepilotwear.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amoebasoft.lifepilotwear.R

class ViewPagerAdapter (
    val images: List<Int>
) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private var start =+ 0
    private var active = R.layout.quickdata
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        if(start == 0) {
            val view = LayoutInflater.from(parent.context).inflate(active, parent, false)
            start += 1
            return ViewPagerViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(active, parent, false)
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

