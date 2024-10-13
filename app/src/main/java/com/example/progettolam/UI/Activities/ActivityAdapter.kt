package com.example.progettolam.UI.Activities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.progettolam.DB.Activity
import com.example.progettolam.R

class ActivityAdapter(var activities: List<Activity>?): RecyclerView.Adapter<ActivityViewHolder>() {
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
            activityTitle.text = activities?.get(position)?.id.toString()
            activityTitle2.text = activities?.get(position)?.startDate?.toString()
            activityTitle3.text = activities?.get(position)?.endDate?.toString()
        }
    }
}