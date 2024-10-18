package com.example.progettolam.DB

enum class ActivityType {
    WALKING, RUNNING, DRIVING, STILL
}

data object ActivityString {
    const val ACTIVITY_TYPE = "ACTIVITY_TYPE"
    const val WALKING = "WALKING"
    const val RUNNING = "RUNNING"
    const val DRIVING = "DRIVING"
    const val STILL = "STILL"

}