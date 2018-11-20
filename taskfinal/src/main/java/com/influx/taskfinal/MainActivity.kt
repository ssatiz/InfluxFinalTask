package com.influx.taskfinal

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.influx.taskfinal.data.Info
import com.influx.taskfinal.widget.ExpandableGridClickListner
import com.influx.taskfinal.widget.ExpandableGridView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ExpandableGridClickListner {

    val bookingTimeArray =
        arrayOf("7:15 AM", "6:15 AM", "8:15 AM", "3:15 AM", "10:15 PM", "3:15 PM", "11:15 PM", "12:15 AM")

    val bookingTime = ArrayList<Info>()

    var gridView: ExpandableGridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        gridView = ExpandableGridView(this)

        container.addView(gridView)

        init()
    }

    private fun init() {
        gridView?.setExpandableClickListner(this)
        gridView?.refreshIconInfoList(generateData())
    }


    private fun generateData(): ArrayList<Info> {
        for ((i, array) in bookingTimeArray.withIndex()) {
            bookingTime.add(Info(i, array, 1))
        }
        bookingTime.add(Info(bookingTime.size, "test", 0))
        return bookingTime
    }

    override fun onParentClick(value: String) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
    }

    override fun onPreviewClick() {
        Toast.makeText(this, "onPreviewClick", Toast.LENGTH_SHORT).show()
        //gridView?.showPreviewView()

    }

    override fun onBuyTicketClick() {
        Toast.makeText(this, "onBuyTicketClick", Toast.LENGTH_SHORT).show()
    }

}
