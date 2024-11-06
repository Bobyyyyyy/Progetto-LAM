package com.example.progettolam.DB

enum class ActivityType {
    WALKING, RUNNING, DRIVING, STILL
}

fun String.toActivityType(): ActivityType? {
    return when (this.uppercase()) { // Convert the string to uppercase for a non-case-sensitive comparison
        "WALKING" -> ActivityType.WALKING
        "RUNNING" -> ActivityType.RUNNING
        "DRIVING" -> ActivityType.DRIVING
        "STILL" -> ActivityType.STILL
        else -> null
    }
}

data object ActivityString {
    const val ACTIVITY_TYPE = "ACTIVITY_TYPE"
    const val WALKING = "WALKING"
    const val RUNNING = "RUNNING"
    const val DRIVING = "DRIVING"
    const val STILL = "STILL"
}