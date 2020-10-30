package com.aut.covid.model

import com.aut.covid.RequestProcessor
import java.io.Serializable
import java.util.*

class Timeline(date: String, day: Int, totalCases: String, totalDeaths: String, details: List<Data>) : Serializable, Comparable<Any?> {
    var date: Date?
    var day: Int
    var details: List<Data>
    var totalCases: Int
    var totalDeaths: Int

    override fun compareTo(o: Any?): Int {
        val timeline = o as Timeline?
        return totalCases - timeline!!.totalCases
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val timeline = o as Timeline
        return totalCases == timeline.totalCases &&
                totalDeaths == timeline.totalDeaths
    }

    override fun hashCode(): Int {
        return Objects.hash(date, day, details, totalCases, totalDeaths)
    }

    init {
        this.date = RequestProcessor.formatDate(date)
        this.day = day
        this.details = details
        this.totalCases = totalCases.toInt()
        this.totalDeaths = totalDeaths.toInt()
    }
}