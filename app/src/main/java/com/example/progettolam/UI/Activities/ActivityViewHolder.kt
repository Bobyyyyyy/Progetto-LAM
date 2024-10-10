package com.example.progettolam.UI.Activities

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.progettolam.R

class ActivityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val activityTitle: TextView = itemView.findViewById(R.id.activityTitle)
    val activityTitle2: TextView = itemView.findViewById(R.id.activityTitle2)
    val activityTitle3: TextView = itemView.findViewById(R.id.activityTitle3)

}