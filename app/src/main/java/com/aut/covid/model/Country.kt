package com.aut.covid.model

import java.io.Serializable
import java.util.*

data class Country(var countryCount: Int, var countryName: String, var countryCode: String, var url: String, var casesData: List<Data>, var latestCasesData: List<LatestData>, var totalCase: Int) : Comparable<Any?>, Serializable {
    var timelines: List<Timeline>? = null

    override fun compareTo(o: Any?): Int {
        val c1 = o as Country?
        return totalCase - c1!!.totalCase
    }

    override fun equals(o: Any?): Boolean {
        val c1 = o as Country?
        return countryCount == c1!!.countryCount
    }

    override fun hashCode(): Int {
        return Objects.hash(countryCount, countryName, countryCode, url, totalCase, casesData)
    }

}