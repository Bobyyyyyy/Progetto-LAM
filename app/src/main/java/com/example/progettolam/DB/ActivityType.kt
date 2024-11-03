package com.example.progettolam.DB

enum class ActivityType {
    WALKING, RUNNING, DRIVING, STILL
}

fun String.toActivityType(): ActivityType? {
    return when (this.uppercase()) {  // Converte la stringa in maiuscolo per un confronto non sensibile al caso
        "WALKING" -> ActivityType.WALKING
        "RUNNING" -> ActivityType.RUNNING
        "DRIVING" -> ActivityType.DRIVING
        "STILL" -> ActivityType.STILL
        else -> null // Restituisce null se non c'è corrispondenza
    }
}



data object ActivityString {
    const val ACTIVITY_TYPE = "ACTIVITY_TYPE"
    const val WALKING = "WALKING"
    const val RUNNING = "RUNNING"
    const val DRIVING = "DRIVING"
    const val STILL = "STILL"
}