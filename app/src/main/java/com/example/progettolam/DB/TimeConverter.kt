package com.example.progettolam.DB

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class TimeConverter {
    @TypeConverter
    fun fromDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun dateToString(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun timeToString(time: LocalTime?): String? {
        return time?.toString()
    }
}