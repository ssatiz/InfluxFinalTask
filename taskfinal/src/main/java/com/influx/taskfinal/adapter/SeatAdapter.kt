package com.influx.taskfinal.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.influx.taskfinal.R

class SeatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.seat, parent, false)
        return SeatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 120
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {

    }

    class SeatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun onBind(){

        }
    }

}