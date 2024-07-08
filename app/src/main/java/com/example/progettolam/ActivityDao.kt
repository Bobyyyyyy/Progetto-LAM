package com.example.progettolam

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ActivityDao {

    @Query("SELECT * FROM Activity_table")
    fun getListOfActivities(): LiveData<List<Activity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(activity: Activity)

}
