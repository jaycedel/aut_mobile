package com.aut.covid

import android.app.ProgressDialog
import android.os.AsyncTask
import com.aut.covid.RequestProcessor.getData
import com.aut.covid.activity.MainActivity
import com.aut.covid.activity.MainActivity.RequestTypes
import com.aut.covid.helper.Log
import com.aut.covid.model.Country
import com.aut.covid.model.Data
import com.aut.covid.model.LatestData
import com.aut.covid.model.Timeline
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class RequestTask : AsyncTask<String?, String?, String> {
    var data: String? = null
        private set
    private var vType: RequestTypes? = null
    private var progressDialog: ProgressDialog? = null
    private var mainActivity: MainActivity

    constructor(mainActivity: MainActivity) {
        this.mainActivity = mainActivity
    }

    constructor(mainActivity: MainActivity, vType: RequestTypes?) {
        this.mainActivity = mainActivity
        this.vType = vType
    }

    protected override fun doInBackground(vararg uri: String?): String? {
        val response = StringBuffer()
        // Create connection
        try {
            // Create URL
            val githubEndpoint = URL(uri[0])
            val con = githubEndpoint.openConnection() as HttpsURLConnection
            con.requestMethod = "GET"
            val responseCode = con.responseCode
            //   System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                val `in` = BufferedReader(InputStreamReader(
                        con.inputStream))
                var inputLine: String?
                while (`in`.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                `in`.close()
                // print result
                //  System.out.println(response.toString());
                if (vType === RequestTypes.WORLD) {
                    populateWorldData(response.toString())
                } else if (vType === RequestTypes.COUNTRIES) {
                    populateCountriesData(response.toString())
                } else if (vType === RequestTypes.INDIA || vType === RequestTypes.COUNTRY) {
                    populateCountryData(response.toString())
                } else if (vType === RequestTypes.COUNTRYTIMELINE) {
                    populateCountryTimeline(response.toString())
                }
            } else {
                println("GET request not worked")
            }
            con.disconnect()
            //JSON PARSING ENDS
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //  System.out.println("Do backgroudn resp : "+response);
        return response.toString()
    }

    override fun onPostExecute(result: String) {
        //Do anything with response.
        data = result
        if (vType === RequestTypes.WORLD) {
            Log.LogInfo("calling WORLD data")
            getData(mainActivity, RequestTypes.COUNTRIES, null)
        } else if (vType === RequestTypes.COUNTRIES) {
            Log.LogInfo("calling India data")
            getData(mainActivity, RequestTypes.INDIA, "IN")
        } else if (vType === RequestTypes.INDIA) {
            Log.LogInfo("Building frames")
            mainActivity.buildFrame()
        } else if (vType === RequestTypes.COUNTRY) {
            Log.LogInfo("Requried data " + mainActivity.searchedCountry)
            mainActivity.countryData()
        } else if (vType === RequestTypes.COUNTRYTIMELINE) {
            Log.LogInfo("updated timeline data")
            mainActivity.showCountryDetails()
        }
        progressDialog!!.dismiss()
    }

    override fun onPreExecute() {
        //  System.out.println("Pre execute");
        progressDialog = ProgressDialog(mainActivity)
        progressDialog!!.max = 100
        progressDialog!!.setMessage("Getting data....")
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.show()
    }



    private fun populateWorldData(response: String) {
        //pasrse JSON
        var jsonObj: JSONObject? = null
        try {
            jsonObj = JSONObject(response)

            // Getting JSON Array node
            val results = jsonObj.getJSONArray("results")
            val dataList = ArrayList<Data>()
            dataList.add(Data("total_cases", results.getJSONObject(0).getString("total_cases")))
            dataList.add(Data("total_affected_countries", results.getJSONObject(0).getString("total_affected_countries")))
            dataList.add(Data("total_active_cases", results.getJSONObject(0).getString("total_active_cases")))
            dataList.add(Data("total_recovered", results.getJSONObject(0).getString("total_recovered")))
            dataList.add(Data("total_unresolved", results.getJSONObject(0).getString("total_unresolved")))
            dataList.add(Data("total_serious_cases", results.getJSONObject(0).getString("total_serious_cases")))
            dataList.add(Data("total_deaths", results.getJSONObject(0).getString("total_deaths")))
            val latestDataList = ArrayList<LatestData>()
            latestDataList.add(LatestData("total_new_cases_today", results.getJSONObject(0).getString("total_new_cases_today")))
            latestDataList.add(LatestData("total_new_deaths_today", results.getJSONObject(0).getString("total_new_deaths_today")))
            mainActivity.WORLDDATA = Country(0, "WORLD", "", "", dataList, latestDataList, 0)
            Log.LogInfo(mainActivity.WORLDDATA.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.LogError("populateWorldData", "RequestTask", e.message)
        }
    }

    private fun populateCountriesData(response: String) {
        //pasrse JSON
        var jsonObj: JSONObject? = null
        try {
            jsonObj = JSONObject(response)

            // Getting JSON Array node
            val obj = jsonObj.getJSONArray("countryitems")
            val vData = obj[0] as JSONObject
            var i = 1
            try {
                i = 1
                while (true) {
                    val temp = vData.getString(i.toString()) as String
                    jsonObj = JSONObject(temp)
                    val dataList = ArrayList<Data>()
                    dataList.add(Data("total_cases", jsonObj.getString("total_cases")))
                    dataList.add(Data("total_active_cases", jsonObj.getString("total_active_cases")))
                    dataList.add(Data("total_recovered", jsonObj.getString("total_recovered")))
                    dataList.add(Data("total_unresolved", jsonObj.getString("total_unresolved")))
                    dataList.add(Data("total_serious_cases", jsonObj.getString("total_serious_cases")))
                    dataList.add(Data("total_deaths", jsonObj.getString("total_deaths")))

                    //latest data
                    val latestDataList = ArrayList<LatestData>()
                    latestDataList.add(LatestData("total_new_cases_today", jsonObj.getString("total_new_cases_today")))
                    latestDataList.add(LatestData("total_new_deaths_today", jsonObj.getString("total_new_deaths_today")))
                    val c = Country(jsonObj.getString("ourid").toInt(), jsonObj.getString("title"), jsonObj.getString("code"), jsonObj.getString("source"), dataList, latestDataList, jsonObj.getString("total_cases").toInt())
                    mainActivity.countryNamesVsCountryCode?.put(c.countryName.toLowerCase(), c.countryCode)

                    mainActivity.countriesDataList!!.add(c)
                    i++
                }
            } catch (e: Exception) {
                i--
                println("total countries found : $i")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.LogError("populateCountriesData", "RequestTask", e.message)
        }
    }

    private fun populateCountryData(response: String) {
        //pasrse JSON
        var jsonObj: JSONObject? = null
        try {
            jsonObj = JSONObject(response)
            // Getting JSON Array node
            val obj = jsonObj.getJSONArray("countrydata")
            var info = obj.getString(0)
            jsonObj = JSONObject(info)
            info = jsonObj.getString("info")
            val dataList = ArrayList<Data>()
            dataList.add(Data("total_cases", jsonObj.getString("total_cases")))
            dataList.add(Data("total_active_cases", jsonObj.getString("total_active_cases")))
            dataList.add(Data("total_recovered", jsonObj.getString("total_recovered")))
            dataList.add(Data("total_unresolved", jsonObj.getString("total_unresolved")))
            dataList.add(Data("total_serious_cases", jsonObj.getString("total_serious_cases")))
            dataList.add(Data("total_deaths", jsonObj.getString("total_deaths")))
            //add to latest data
            val latestDataList = ArrayList<LatestData>()
            latestDataList.add(LatestData("total_new_cases_today", jsonObj.getString("total_new_cases_today")))
            latestDataList.add(LatestData("total_new_deaths_today", jsonObj.getString("total_new_deaths_today")))
            jsonObj = JSONObject(info)
            if (vType === RequestTypes.COUNTRY) {
                mainActivity.searchedCountry = Country(jsonObj.getString("ourid").toInt(), jsonObj.getString("title"), jsonObj.getString("code"), jsonObj.getString("source"), dataList, latestDataList, 0)
            } else {
                mainActivity.searchedCountry = Country(jsonObj.getString("ourid").toInt(), jsonObj.getString("title"), jsonObj.getString("code"), jsonObj.getString("source"), dataList, latestDataList, 0)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.LogError("populateCountryData", "RequestTask", e.message)
        }
    }

    private fun populateCountryTimeline(response: String) {
        //pasrse JSON
        var jsonObj: JSONObject? = null
        try {
            jsonObj = JSONObject(response).getJSONArray("timelineitems")[0] as JSONObject
            val keys = jsonObj.keys()
            val timelines: MutableList<Timeline> = ArrayList()
            var i = 0
            while (keys.hasNext()) {
                i++
                val key = keys.next()
                if (key.trim { it <= ' ' }.equals("stat", ignoreCase = true)) break
                val `val` = jsonObj!![key] as JSONObject
                //System.out.println("===Key : "+key + " | val : "+val);
                val dataList = ArrayList<Data>()
                dataList.add(Data("total_cases", `val`.getString("total_cases")))
                dataList.add(Data("total_deaths", `val`.getString("total_deaths")))
                dataList.add(Data("total_recoveries", `val`.getString("total_recoveries")))
                dataList.add(Data("new_daily_cases", `val`.getString("new_daily_cases")))
                dataList.add(Data("new_daily_deaths", `val`.getString("new_daily_deaths")))
                val timeline = Timeline(key, i, `val`.getString("total_cases"), `val`.getString("total_deaths"), dataList)
                timelines.add(timeline)
            }
            mainActivity.searchedCountry!!.timelines = timelines
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.LogError("populateCountryTimeline", "RequestTask", e.message)
        }
    }
}