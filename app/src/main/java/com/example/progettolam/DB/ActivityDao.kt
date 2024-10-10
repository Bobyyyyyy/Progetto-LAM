package com.example.progettolam.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.LocalTime

@Dao
interface ActivityDao {

    @Query("SELECT * FROM Activity_table WHERE startTime < :startTime")
    fun getListOfActivities(startTime: LocalTime): LiveData<List<Activity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(activity: Activity)


}
