package com.example.progettolam.UI.Activities

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.progettolam.R

class ActivityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val typeActivity: TextView = itemView.findViewById(R.id.typeActivity)
    val durationActivity: TextView = itemView.findViewById(R.id.durationActivity)
    val btnOpenDetails: TextView = itemView.findViewById(R.id.btnOpenDetails)
    val cardPreviewActivity: CardView = itemView.findViewById(R.id.idCardPreviewDetail)
}