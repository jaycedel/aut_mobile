package com.aut.covid.model

import com.aut.covid.RequestProcessor
import java.io.Serializable

class LatestData(key: String, value: String) : Serializable {
    var key: String
    var value: String

    init {
        var key = key
        key = RequestProcessor.makeFirstLetterCaps(key)
        this.key = key
        this.value = value
    }
}