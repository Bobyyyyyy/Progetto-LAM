package com.example.progettolam.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


@Database(entities = [BaseActivity:: class,WalkingActivity::class,RunningActivity::class,SittingActivity::class,DrivingActivity::class, GeofenceInfo::class], version = 2, exportSchema = false)
@TypeConverters(TimeConverter::class)
abstract class ActivityDatabase: RoomDatabase() {

    abstract fun activityDao(): ActivityDao
    abstract fun geofenceDao(): GeofenceInfoDao

    companion object {

        @Volatile
        private var INSTANCE: ActivityDatabase? = null
        val scope = CoroutineScope(Dispatchers.IO)

        fun getDB(context: Context): ActivityDatabase {
            return INSTANCE ?: synchronized (this) {
                val _INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    ActivityDatabase::class.java,
                    "Activity_database"
                ).build()
                INSTANCE = _INSTANCE
                _INSTANCE
            }
        }







    }


}
