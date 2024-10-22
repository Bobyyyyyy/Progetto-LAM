package com.example.progettolam.UI.Activities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.progettolam.DB.ActivityJoin
import com.example.progettolam.DB.BaseActivity
import com.example.progettolam.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.LocalTime
import java.time.LocalDate

class ActivityAdapter(var activities: List<ActivityJoin>?): RecyclerView.Adapter<ActivityViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        return ActivityViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_card, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return activities?.size ?: 0
    }


    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.apply {
            val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("k:m:s")


            val startTime = activities?.get(position)?.baseActivity?.startTime

            val endTime = activities?.get(position)?.baseActivity?.endTime
            val startDate = activities?.get(position)?.baseActivity?.startDate
            val endDate = activities?.get(position)?.baseActivity?.endDate


            val start: LocalDateTime = LocalDateTime.of(startDate,startTime)
            val end: LocalDateTime = LocalDateTime.of(endDate,endTime)
            var duration = Duration.between(start,end)
            val days = duration.toDays()
            val time: String

            if(days.toInt() != 0) {
                duration = duration.minusDays(days)
                time = "${"%2d".format(days)}:${"%02d".format(duration.toDays())}:${"%02d".format(duration.toMinutes())}:${"%02d".format(duration.seconds)}"
            }

            else {
                time = "${"%02d".format(duration.toDays())}:${"%02d".format(duration.toMinutes())}:${"%02d".format(duration.seconds)}"
            }

            activityTitle2.text = activities?.get(position)?.baseActivity?.activityType.toString()
            activityTitle3.text = activities?.get(position)?.runningActivity?.avgSpeed?.toString() ?: "0.0"
            activityTitle.text = activities?.get(position)?.runningActivity?.steps?.toString() ?: "Ciao"

        }
    }
}