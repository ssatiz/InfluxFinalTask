package com.influx.taskfinal

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.Toast
import com.google.gson.Gson
import com.influx.taskfinal.data.DatesItem
import com.influx.taskfinal.data.Info
import com.influx.taskfinal.widget.ExpandableGridClickListner
import com.influx.taskfinal.widget.ExpandableGridView
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.lnr_container.view.*
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity(), ExpandableGridClickListner {

    val bookingTimeArray =
        arrayOf("7:15 AM", "6:15 AM", "8:15 AM", "3:15 AM", "10:15 PM", "3:15 PM", "11:15 PM", "12:15 AM")

    val bookingTime = ArrayList<Info>()

//    var gridView: ExpandableGridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

//        gridView = ExpandableGridView(this)
//
//        container.addView(gridView)

        init()
    }

    private fun init() {
//        gridView?.setExpandableClickListner(this)
//        gridView?.refreshIconInfoList(generateData())

        val infos = generateData()
        for (i in 0 until infos.size){
            val viewMovies = LayoutInflater.from(this).inflate(R.layout.lnr_container, container,
                    false)
            viewMovies.lblMovieName.text = infos[i].timing.moviewName
            val gridView = ExpandableGridView(this)
            gridView.setExpandableClickListner(this)
            val timings = infos[i].timing.timings!!
            for (i in 0 until  timings.size){
                timings[i].expand = 1
            }
            gridView.refreshIconInfoList(timings)
            viewMovies.lnrExpand.addView(gridView)
            container.addView(viewMovies)
        }
    }


    private fun generateData(): ArrayList<Info> {
//        for ((i, array) in bookingTimeArray.withIndex()) {
//            bookingTime.add(Info(i, array, 1))
//        }
        val testData = JSONObject(loadJSONFromAsset())
        if (testData.has("result")){
            val resultObj = testData.getJSONObject("result")
            if (resultObj.has("dates")){
                val datesArrays = resultObj.getJSONArray("dates")
                val dateObj = Gson().fromJson(datesArrays.getJSONObject(0).toString(),
                        DatesItem::class.java)
                dateObj.timings?.let {
                    for (i in 0 until it.size){
                        val timing = it[i]
                        bookingTime.add(Info(i, timing, 1))
                    }
                }

            }

        }
//        bookingTime.add(Info(bookingTime.size, MoviewItem("No Show Available", listOf()), 0))
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

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            val inputStream = assets.open("test_data.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }

}
