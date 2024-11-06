package com.example.progettolam.UI.Activities

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.progettolam.DB.ActivityJoin
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.R
import java.time.Duration
import java.time.LocalDateTime


class ActivityAdapter(val context: Context, var activities: List<ActivityJoin>?, var listener: (String) -> Unit): RecyclerView.Adapter<ActivityViewHolder>() {
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

            typeActivity.text = getFormattedTypeActivity(activities?.get(position)?.baseActivity?.activityType!!)
            durationActivity.text = time

            btnOpenDetails.setOnClickListener{
                listener(activities?.get(position)?.baseActivity?.id.toString())
            }
            if (activities?.get(position)?.baseActivity?.imported == true) {
                cardPreviewActivity.setCardBackgroundColor(context.getColor(R.color.other_users))
            } else {
                cardPreviewActivity.setCardBackgroundColor(context.getColor(R.color.secondary))
            }
        }
    }

    private fun getFormattedTypeActivity(type: ActivityType): String {
        val formattedType = when (type) {
            ActivityType.WALKING -> context.getString(R.string.walk_tag)
            ActivityType.RUNNING -> context.getString(R.string.run_tag)
            ActivityType.DRIVING -> context.getString(R.string.drive_tag)
            ActivityType.STILL -> context.getString(R.string.chilling_tag)
        }
        return formattedType
    }
}