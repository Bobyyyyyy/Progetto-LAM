package com.example.progettolam.UI.Activities

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.progettolam.DB.ActivityJoin
import com.example.progettolam.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ActivityAdapter(var activities: List<ActivityJoin>?, var listener: (String) -> Unit): RecyclerView.Adapter<ActivityViewHolder>() {
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
            val duration = Duration.between(start,end)
            val time: String

            val days = duration.toDays().toInt()
            val hours = (duration.toHours() % 24).toInt()
            val minutes = (duration.toMinutes() % 60).toInt()
            val seconds = (duration.seconds % 60).toInt()

            time = if (days != 0) {
                "${"%02d".format(days)}:${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
            } else {
                // If days are zero, exclude them
                "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
            }

            typeActivity.text = activities?.get(position)?.baseActivity?.activityType.toString()
            durationActivity.text = time

            btnOpenDetails.setOnClickListener{
                listener(activities?.get(position)?.baseActivity?.id.toString())
            }
        }
    }
}