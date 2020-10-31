package com.aut.covid.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aut.covid.R
import com.aut.covid.RequestProcessor
import com.aut.covid.helper.CustomColorTemplate
import com.aut.covid.model.Country
import com.aut.covid.model.Data
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import java.util.*

class MainActivity : AppCompatActivity() {
    @JvmField
    var countriesDataList: ArrayList<Country>? = null
    @JvmField
    var countryNamesVsCountryCode: MutableMap<String, String>? = null
    @JvmField
    var WORLDDATA: Country? = null
    @JvmField
    var searchedCountry: Country? = null

    enum class RequestTypes {
        WORLD, COUNTRIES, INDIA, COUNTRY, WORLDTIMELINE, COUNTRYTIMELINE, MOST, LEAST
    }

    var pieChart: PieChart? = null
    var searchedText: EditText? = null
    var search: Button? = null
    var seeMore: Button? = null
    var entries: ArrayList<Entry>? = null
    var PieEntryLabels: ArrayList<String>? = null
    var pieDataSet: PieDataSet? = null
    var pieData: PieData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        processRequest()
    }

    private fun init() {
        countriesDataList = ArrayList()
        setContentView(R.layout.activity_main)
        searchedText = findViewById<View>(R.id.searchText) as EditText
        search = findViewById<View>(R.id.button) as Button
        seeMore = findViewById<View>(R.id.seeMore) as Button
        countryNamesVsCountryCode = HashMap()
        addListeners()
    }

    private fun addListeners() {
        search!!.setOnClickListener {
            val q = searchedText!!.text.toString()
            if (!countryNamesVsCountryCode!!.keys.contains(q.trim { it <= ' ' }.toLowerCase())) {
                Toast.makeText(applicationContext, "Stats Not Available for " + searchedText!!.text, Toast.LENGTH_LONG)
                println("$q Not Found")
            } else {
                Toast.makeText(applicationContext, "Searching stats of " + searchedText!!.text, Toast.LENGTH_LONG)
                println("$q Found")
                RequestProcessor.getData(this@MainActivity, RequestTypes.COUNTRY, countryNamesVsCountryCode!![q.toLowerCase()])
            }
        }
        seeMore!!.setOnClickListener {
            println("clicked See more for " + searchedCountry!!.countryName)
            RequestProcessor.getData(this@MainActivity, RequestTypes.COUNTRYTIMELINE, searchedCountry!!.countryCode)
        }
    }

    private fun processRequest() {
        RequestProcessor.getData(this, RequestTypes.WORLD, null)
    }

    fun buildFrame() {
        Collections.sort(countriesDataList)
        worldData()
        countryData()
       // buildCharts()
    }

    fun showCountryDetails() {
        val intent = Intent(this@MainActivity, CountryDetailsActivity::class.java)
        intent.putExtra("CountryDetails", searchedCountry)
        startActivity(intent)
    }

    private fun buildCharts() {
        val count = 10
        affectedCountries(count, RequestTypes.MOST)
        affectedCountries(count, RequestTypes.LEAST)
    }

    fun affectedCountries(count: Int, value: RequestTypes) {

        if (value == RequestTypes.MOST) {
            pieChart = findViewById<View>(R.id.chart1) as PieChart
        } else if (value == RequestTypes.LEAST) {
            pieChart = findViewById<View>(R.id.chart2) as PieChart
        }
        entries = ArrayList()
        PieEntryLabels = ArrayList()
        var start = 0
        var end = count
        if (value == RequestTypes.MOST) {
            start = countriesDataList!!.size - 1
            end = countriesDataList!!.size - (count + 1)
        }
        var i = start
        var temp = 0
        while (true) {
            val c = countriesDataList!![i]

            if (value == RequestTypes.MOST) {
                if (countriesDataList!![i].totalCase > 0) {

                    PieEntryLabels!!.add(c.countryName)
                    entries!!.add(BarEntry(c.totalCase.toFloat(), i))
                }
                if (i > end) i-- else break
            } else if (value == RequestTypes.LEAST) {
                if (countriesDataList!![i].totalCase > 0) {
                    //    System.out.println(c.getCountryName() + " count "+c.getTotalCase());
                    PieEntryLabels!!.add(c.countryName)
                    entries!!.add(BarEntry(c.totalCase.toFloat(), i))
                    temp++
                }
                if (i <= end) i++ else {

                    end += if (temp < count) {
                        count - temp
                    } else {
                        break
                    }
                }
            }
        }
        //System.out.println("entries "+ entries);
        pieDataSet = PieDataSet(entries, "")
        pieData = PieData(PieEntryLabels, pieDataSet)
        pieDataSet!!.setColors(CustomColorTemplate.COLORFUL_COLORS)
        pieChart!!.data = pieData
        pieChart!!.animateY(3000)
    }

    fun countryData() {
        val countryStats = findViewById<View>(R.id.countryStats) as TextView
        val t3 = findViewById<TextView>(R.id.textView31)
        val t4 = findViewById<TextView>(R.id.textView41)
        val t5 = findViewById<TextView>(R.id.textView51)
        val t6 = findViewById<TextView>(R.id.textView61)
        val t7 = findViewById<TextView>(R.id.textView71)
        val t8 = findViewById<TextView>(R.id.textView81)
        val dataList = searchedCountry!!.casesData
        countryStats.text = searchedCountry!!.countryName + " Statistics"
        t3.text = """
            ${dataList[0].key}

            ${dataList[0].value}
            """.trimIndent()
        t4.text = """
            ${dataList[1].key}

            ${dataList[1].value}
            """.trimIndent()
        t5.text = """
            ${dataList[2].key}

            ${dataList[2].value}
            """.trimIndent()
        t6.text = """
            ${dataList[3].key}

            ${dataList[3].value}
            """.trimIndent()
        t7.text = """
            ${dataList[4].key}

            ${dataList[4].value}
            """.trimIndent()
        t8.text = """
            ${dataList[5].key}

            ${dataList[5].value}
            """.trimIndent()
    }

    private fun worldData() {
        val t3 = findViewById<TextView>(R.id.textView3)
        val t4 = findViewById<TextView>(R.id.textView4)
        val t5 = findViewById<TextView>(R.id.textView5)
        val t6 = findViewById<TextView>(R.id.textView6)
        val t7 = findViewById<TextView>(R.id.textView7)
        val t8 = findViewById<TextView>(R.id.textView8)
        val t9 = findViewById<TextView>(R.id.textView9)


        val dataList = WORLDDATA!!.casesData as ArrayList<Data>
        t3.text = """
            ${dataList[0].key}

            ${dataList[0].value}
            """.trimIndent()
        t4.text = """
            ${dataList[1].key}

            ${dataList[1].value}
            """.trimIndent()
        t5.text = """
            ${dataList[2].key}

            ${dataList[2].value}
            """.trimIndent()
        t6.text = """
            ${dataList[3].key}

            ${dataList[3].value}
            """.trimIndent()
        t7.text = """
            ${dataList[4].key}

            ${dataList[4].value}
            """.trimIndent()
        t8.text = """
            ${dataList[5].key}

            ${dataList[5].value}
            """.trimIndent()
        t9.text = """
            ${dataList[6].key}

            ${dataList[6].value}
            """.trimIndent()
    }
}