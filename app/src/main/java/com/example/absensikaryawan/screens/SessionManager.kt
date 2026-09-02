package com.example.absensikaryawan

import java.util.Calendar

object SessionManager {

    fun isPersistentSessionTime(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        return hour in 5..20
        //return false

    }

    fun shouldRequireLogin(): Boolean {
        return !isPersistentSessionTime()
    }
}