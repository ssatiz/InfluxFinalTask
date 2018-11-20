package com.influx.influxtask

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.influx.influxtask.widget.ExpandableGridView

class MainActivity : AppCompatActivity(), ExpandableGridView.OnExpandItemClickListener {

    var countryGridView: ExpandableGridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        initComponent()
    }


    private fun initComponent() {

        val countryData = arrayOf("7:15 AM", "6:15 AM", "8:15 AM", "3:15 AM", "10:15 PM", "3:15 PM", "11:15 PM", "12:15 AM")

        countryGridView = findViewById(R.id.country_grid)
        val countryAdapter = ArrayAdapter(baseContext,
            R.layout.grid_item, R.id.grid_item, countryData)
        countryGridView?.adapter = countryAdapter
        countryGridView?.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, id ->
            countryGridView?.expandGridViewAtView(view)
        }
        countryGridView?.setOnExpandItemClickListener(this)
    }

    override fun onItemClick(position: Int, clickPositionData: Any) {
        Toast.makeText(this, clickPositionData.toString() + " clicked", Toast.LENGTH_LONG).show()
    }

    override fun onPreviewClick() {
        Toast.makeText(this, " onPreviewClick", Toast.LENGTH_LONG).show()
        //Thread.sleep(1000)
        countryGridView?.showPreviewView()
    }

    override fun onBuyTicketClick() {
        Toast.makeText(this, " onBuyTicketClick", Toast.LENGTH_LONG).show()
    }

}
