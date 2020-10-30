package com.aut.covid.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aut.covid.R
import com.aut.covid.helper.Log
import com.aut.covid.model.Country
import com.aut.covid.model.Timeline
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import java.util.*

class CountryDetailsActivity : AppCompatActivity() {
    private var country: Country? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_details)
        init()
        createTimeline()
        buildCurrentStats()
    }

    private fun buildCurrentStats() {
        val cases = findViewById<View>(R.id.newCases) as TextView
        val deaths = findViewById<View>(R.id.newDeaths) as TextView
        val latestData = country!!.latestCasesData
        val casesString = latestData[0].key + " = " + latestData[0].value
        val deathString = latestData[1].key + " = " + latestData[1].value
        cases.text = casesString
        deaths.text = deathString
    }

    private fun init() {
        country = this.intent.getSerializableExtra("CountryDetails") as Country
        println(country!!.countryCode)
        title = country!!.countryName + "\'s Timelines"
    }

    private fun createTimeline() {
        val timelines = country!!.timelines
        Log.LogInfo("fetched timelines ")
        Collections.sort(timelines)
        Log.LogInfo("Sorted timelines")
        totalCasesTimeLine(timelines)
        Log.LogInfo("created Total cases timelines")
        totalDeathsTimeLine(timelines)
        Log.LogInfo("created Total deaths timelines")
    }

    private fun totalCasesTimeLine(timelines: List<Timeline>?) {
        val graph = findViewById<View>(R.id.graph) as GraphView
        val dp: MutableList<DataPoint> = ArrayList()
        var maxX = -1
        var lastVal = -1
        for (timeline in timelines!!) {
            if (lastVal == timeline.totalCases) {
                continue
            }
            if (lastVal == -1) {
                lastVal = timeline.totalCases
            }
            val d = DataPoint(timeline.day.toDouble(), timeline.totalCases.toDouble())
            dp.add(d)
            if (timeline.day > maxX) {
                maxX = timeline.day
            }
        }
        drawTimeline(graph, dp.toTypedArray(), maxX.toDouble())
    }

    private fun totalDeathsTimeLine(timelines: List<Timeline>?) {
        val graph = findViewById<View>(R.id.graph1) as GraphView
        val dp: MutableList<DataPoint> = ArrayList()
        var maxX = -1
        for (timeline in timelines!!) {
            val d = DataPoint(timeline.day.toDouble(), timeline.totalDeaths.toDouble())
            dp.add(d)
            if (timeline.day > maxX) {
                maxX = timeline.day
            }
        }
        drawTimeline(graph, dp.toTypedArray(), maxX.toDouble())
    }

    private fun drawTimeline(graphView: GraphView, dataPoints: Array<DataPoint>, maxX: Double) {
        val series = BarGraphSeries(dataPoints)
        series.isAnimated = true
        val dp = DataPoint(maxX + 200, 0.0)
        series.appendData(dp, true, 0, false)
        graphView.addSeries(series)
    }
}