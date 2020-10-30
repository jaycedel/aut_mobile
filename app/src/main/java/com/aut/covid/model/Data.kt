package com.aut.covid.model

import com.aut.covid.RequestProcessor.makeFirstLetterCaps
import java.io.Serializable

class Data(key: String?, value: String) : Serializable {
    var key: String
    var value: String

    init {
        var key = key
        key = makeFirstLetterCaps(key!!)
        this.key = key
        this.value = value
    }
}