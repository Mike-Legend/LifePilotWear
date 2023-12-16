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
    private var layout =+ 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        if(start == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.sync, parent, false)
            start += 1
            return ViewPagerViewHolder(view)
        }
        if (layout == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.pageviewermain, parent, false)
            return ViewPagerViewHolder(view)
        } else if (layout == 1) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.sync, parent, false)
            return ViewPagerViewHolder(view)
        } else if (layout == 2) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.buttons, parent, false)
            return ViewPagerViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sync, parent, false)
        return ViewPagerViewHolder(view)
    }
    override fun getItemCount(): Int {
        return images.size
    }
    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val curImage = images[position]
        if (start == 1) {
            layout = 1
            start += 1
        } else {
            layout = images[position]
        }
        holder.itemView.setBackgroundResource(curImage)
    }
}

