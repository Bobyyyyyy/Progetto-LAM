package com.example.progettolam.DB

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.progettolam.UI.profileFragment.ActivitiesGraphData
import com.example.progettolam.UI.profileFragment.StepsData
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalTime

class ActivityRepository(app: Application) {


    private var activityDao: ActivityDao

    init{
        val db = ActivityDatabase.getDB(app)
        activityDao = db.activityDao()
    }


    fun insertBaseActivity(baseActivity: BaseActivity): Long {
        return activityDao.insertBaseActivity(baseActivity)
    }

    fun insertWalkingActivity(walkingActivity: WalkingActivity) {
        return activityDao.insertWalkingActivity(walkingActivity)
    }

    fun insertRunningActivity(runningActivity: RunningActivity) {
        return activityDao.insertRunningActivity(runningActivity)
    }

    fun insertDrivingActivity(drivingActivity: DrivingActivity) {
        return activityDao.insertDrivingActivity(drivingActivity)
    }

    fun insertSittingActivity(sittingActivity: SittingActivity){
        return activityDao.insertSittingActivity(sittingActivity)
    }


    fun getAllActivities(startDate: LocalDate?, imported: Boolean): LiveData<List<ActivityJoin>> {
        return activityDao.getListOfActivities(startDate, imported)
    }

    fun getAllStepsFromDay(date: LocalDate?): LiveData<Int?> {
        return activityDao.getAllStepsFromDay(date)
    }

    fun getActivitiesFromType(activityType: ActivityType?): LiveData<List<ActivityJoin>> {
        return activityDao.getActivitiesFromType(activityType)
    }

    fun getInfoActivityByID(id: Long?): LiveData<ActivityJoin> {
        return activityDao.getInfoActivityByID(id)
    }

    fun getCurrentWeekSpinner(today: LocalDate?) : LiveData<Array<StepsData>> {
        return activityDao.getCurrentWeekSteps(today)
    }

    fun getCurrentWeekActivities(today: LocalDate?): LiveData<Array<ActivitiesGraphData>> {
        return activityDao.getCurrentWeekActivities(today)
    }


    fun exportActivitiesToCSV(uri: Uri, context: Context) {

        val csvBuilder = StringBuilder()

        val baseActivities = activityDao.getAllBaseActivities()

        if(baseActivities.isNotEmpty()) {
            csvBuilder.append("id,imported,author,activityType,startTime,startDate,endTime,endDate\n")
            baseActivities.forEach { activity ->
                csvBuilder.append("${activity.id},${activity.imported},${activity.author},${activity.activityType},${activity.startTime},${activity.startDate},${activity.endTime},${activity.endDate}")
                csvBuilder.append("\n")
            }
            csvBuilder.append("\n")
        }

        val walkingActivities = activityDao.getAllWalkingActivities()

        if(walkingActivities.isNotEmpty()) {
            csvBuilder.append("activityId,steps,avgSpeed\n")
            walkingActivities.forEach { activity ->
                csvBuilder.append("${activity.activityId},${activity.steps},${activity.avgSpeed}")
                csvBuilder.append("\n")
            }
            csvBuilder.append("\n")

        }


        val runningActivities = activityDao.getAllRunningActivities()

        if(runningActivities.isNotEmpty()) {
            csvBuilder.append("activityId,steps,avgSpeed\n")
            runningActivities.forEach { activity ->
                csvBuilder.append("${activity.activityId},${activity.steps},${activity.avgSpeed}")
                csvBuilder.append("\n")
            }
            csvBuilder.append("\n")

        }


        val sittingActivities = activityDao.getAllStillActivities()

        if(sittingActivities.isNotEmpty()) {
            csvBuilder.append("activityId\n")

            sittingActivities.forEach { activity ->
                csvBuilder.append("${activity.activityId}")
                csvBuilder.append("\n")
            }
            csvBuilder.append("\n")

        }


        val drivingActivities = activityDao.getAllDrivingActivities()


        if(drivingActivities.isNotEmpty()) {
            csvBuilder.append("activityId,avgSpeed\n")
            drivingActivities.forEach { activity ->
                csvBuilder.append("${activity.activityId}, ${activity.avgSpeed}")
            }
            csvBuilder.append("\n")
        }

        try {
            val contentResolver = context.contentResolver
            val outputStream = contentResolver.openOutputStream(uri)
            outputStream?.write(csvBuilder.toString().toByteArray())
            outputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    fun importActivitiesFromCSV(uri: Uri, context: Context) {
        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Leggi la prima riga per determinare le intestazioni
            var line = reader.readLine()
            if (line != null) {
                // Ignora l'intestazione della tabella delle attività di base
                line = reader.readLine()
                while (line != null && line.isNotEmpty()) {
                    val parts = line.split(",")
                    // Assumendo che i campi siano nell'ordine corretto
                    val activity = BaseActivity(
                        id = parts[0].toLong(),
                        imported = true,
                        author = parts[2],
                        activityType = parts[3].toActivityType(),
                        startTime = LocalTime.parse(parts[4]),
                        startDate = LocalDate.parse(parts[5]),
                        endTime = LocalTime.parse(parts[6]),
                        endDate = LocalDate.parse(parts[7])
                    )
                    activityDao.insertBaseActivity(activity) // Inserisci nel database
                    line = reader.readLine()
                }
            }

            // Ripeti il processo per le altre tabelle

            // Attività di camminata
            line = reader.readLine() // Ignora l'intestazione "activityId,steps,avgSpeed"
            line = reader.readLine()
            while (line != null && line.isNotEmpty()) {
                val parts = line.split(",")
                val walkingActivity = WalkingActivity(
                    activityId = parts[0].toLong(),
                    steps = parts[1].toInt(),
                    avgSpeed = parts[2].toFloat()
                )
                activityDao.insertWalkingActivity(walkingActivity)
                line = reader.readLine()
            }

            // Attività di corsa
            line = reader.readLine() // Ignora l'intestazione "activityId,steps,avgSpeed"
            line = reader.readLine()
            while (line != null && line.isNotEmpty()) {
                val parts = line.split(",")
                val runningActivity = RunningActivity(
                    activityId = parts[0].toLong(),
                    steps = parts[1].toInt(),
                    avgSpeed = parts[2].toFloat()
                )
                activityDao.insertRunningActivity(runningActivity)
                line = reader.readLine()
            }

            // Attività di seduta
            line = reader.readLine() // Ignora l'intestazione "activityId"
            line = reader.readLine()
            while (line != null && line.isNotEmpty()) {
                val parts = line.split(",")
                val sittingActivity = SittingActivity(
                    activityId = parts[0].toLong()
                )
                activityDao.insertSittingActivity(sittingActivity)
                line = reader.readLine()
            }

            // Attività di guida
            line = reader.readLine() // Ignora l'intestazione "activityId,avgSpeed"
            line = reader.readLine()
            while (line != null && line.isNotEmpty()) {
                val parts = line.split(",")
                val drivingActivity = DrivingActivity(
                    activityId = parts[0].toLong(),
                    avgSpeed = parts[1].toFloat()
                )
                activityDao.insertDrivingActivity(drivingActivity)
                line = reader.readLine()
            }

            reader.close()
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}


