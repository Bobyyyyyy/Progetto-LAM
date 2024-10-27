package com.example.progettolam.DB

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

@Entity(tableName = "geofence_info")
class GeofenceInfo(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    var id: String,

    @SerializedName("latitude")
    var latitude: Double?,

    @SerializedName("longitude")
    var longitude: Double?,

    @SerializedName("color")
    var color: Int?,

    @SerializedName("radius")
    var radius: Int?,

)