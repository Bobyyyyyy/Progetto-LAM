package com.example.progettolam.UI.calendarFragment

import android.view.View
import android.widget.TextView
import com.example.progettolam.R
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View, private val onDateClicked: (CalendarDay) -> Unit) : ViewContainer(view) {
    val textView: TextView = view.findViewById(R.id.calendarDayText)
    lateinit var day: CalendarDay

    init {
        view.setOnClickListener {
            if (::day.isInitialized) {
                onDateClicked(day)
            }
        }
    }
}

