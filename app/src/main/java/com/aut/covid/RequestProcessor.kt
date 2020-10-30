package com.aut.covid

import com.aut.covid.activity.MainActivity
import com.aut.covid.activity.MainActivity.RequestTypes
import com.aut.covid.helper.Log
import java.text.SimpleDateFormat
import java.util.*

object RequestProcessor {
    @JvmStatic
    fun getData(mainActivity: MainActivity, value: RequestTypes, countryCode: String?) {
        val requestTask = RequestTask(mainActivity, value)
        if (value === RequestTypes.WORLD) {
            requestTask.execute("https://api.thevirustracker.com/free-api?global=stats")
        } else if (value === RequestTypes.COUNTRIES) {
            requestTask.execute("https://api.thevirustracker.com/free-api?countryTotals=ALL")
        } else if (value === RequestTypes.COUNTRYTIMELINE) {
            requestTask.execute("https://api.thevirustracker.com/free-api?countryTimeline=$countryCode")
        } else if ((value === RequestTypes.INDIA || value === RequestTypes.COUNTRY) && countryCode != null && countryCode.trim { it <= ' ' } != "") {
            requestTask.execute("https://api.thevirustracker.com/free-api?countryTotal=" + countryCode.trim { it <= ' ' })
        }
    }

    fun makeFirstLetterCaps(value: String): String {
        var toRet = ""
        for (k in value.split("_").toTypedArray()) {
            toRet += k[0].toString().toUpperCase() + k.substring(1) + " "
        }
        return toRet.trim { it <= ' ' }
    }

    fun formatDate(value: String?): Date? {
        var toRet: Date? = null
        try {
            toRet = SimpleDateFormat("MM/dd/yyyy").parse(value)
        } catch (e: Exception) {
            toRet = null
            Log.LogError("formatDate", "RequestProcessor", e.message)
        }
        return toRet
    }

}