package com.aut.covid.helper

object Log {
    fun LogInfo(message: String?) {
        println("Log Info : $message")
    }

    fun LogError(methodName: String, className: String, message: String?) {
        println("Log Error : $message | Class : $className and in method : $methodName")
    }
}