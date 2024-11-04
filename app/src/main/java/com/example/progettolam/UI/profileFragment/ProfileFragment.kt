package com.example.progettolam.UI.profileFragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.R
import com.example.progettolam.UI.preferencesActivity.PreferencesFragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate


class ProfileFragment: Fragment() {
    private lateinit var profileModel: ProfileViewModel
    private lateinit var textView: TextView
    private lateinit var settings: ImageView
    private lateinit var heightView: TextView
    private lateinit var weightView: TextView
    private lateinit var chart: LineChart
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    private lateinit var pieChart2: PieChart

    private val activityViewModel by lazy {
        val factory = ActivityViewModelFactory(ActivityRepository(requireActivity().application))
        ViewModelProvider(this, factory)[ActivityViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        heightView = view.findViewById(R.id.heightValue)
        weightView = view.findViewById(R.id.weightValue)

        textView = view.findViewById(R.id.username)
        settings = view.findViewById(R.id.settings)
        //todaySteps = view.findViewById(R.id.todaySteps)

        barChart = view.findViewById(R.id.chart2)
        pieChart = view.findViewById(R.id.chart3)


        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireActivity())

        val value = resources.getString(R.string.preferences_username_default)
        val storedName = sharedPref?.getString(getString(R.string.preferences_username), value)

        val storedHeight = sharedPref?.getString(getString(R.string.preferences_height),resources.getString(R.string.preferences_height_default))
        val storedWeight = sharedPref?.getString(getString(R.string.preferences_weight),resources.getString(R.string.preferences_weight_default))
        heightView.text = storedHeight.toString()
        weightView.text = storedWeight.toString()

        profileModel.changeUsername(storedName.toString())

        profileModel.username.observe(viewLifecycleOwner) {
            textView.text = it
        }



        activityViewModel.getCurrentWeekActivities(LocalDate.now()).observe(viewLifecycleOwner) {
            for (data in it) {
                Log.i("Attività", data.type.toString() + " " + data.number.toString())
            }

            val pieData = createPieCharDate(it)

            pieChart.setData(pieData)
            pieChart.description = null
            pieChart.animateXY(1000, 1500)
            pieChart.invalidate()

        }

        activityViewModel.getCurrentWeekSteps(LocalDate.now()).observe(viewLifecycleOwner) {

            if(it != null) {
                /*
                val lineData = createLineCharDate("Steps", Color.RED, it)
                chart.data = lineData
                setupChart(chart)

                 */

                val barData = createBarCharDate("Steps", Color.MAGENTA,it)

                setupChart(barChart)

                barChart.data = barData
                barChart.animateY(1000)
                barChart.setFitBars(true)
                barChart.invalidate()


            }

        }

        settings.setOnClickListener{
            changeFragment(PreferencesFragment(), R.id.settings.toString())
        }

    }


    private fun createLineCharDate(label: String, color: Int, dataObjects: Array<StepsData>): LineData {

        // Create an empty list of Entry objects
        val entries = mutableListOf<Entry>()

        // Convert DataExample objects to MPAndroidChart Entry objects
        for (data in dataObjects) {
            entries.add(Entry(data.day_of_week.toFloat(), data.daily_steps.toFloat()))
        }

        // Create a map to store daily steps for easy access
        val dailyStepsMap = mutableMapOf<Int, Float>()
        for (entry in entries) {
            dailyStepsMap[entry.x.toInt()] = entry.y
        }

        // Ensure every day of the week is represented
        for (day in 0..6) {
            // Add entry for the day if not present
            val steps = dailyStepsMap.getOrDefault(day, 0f) // Use 0 if no steps recorded
            entries.add(Entry(day.toFloat(), steps))
        }

        // Sort the entries to ensure they are in order
        entries.sortBy { it.x }

        // Create the dataset
        val dataSet = LineDataSet(entries, label)
        dataSet.setColor(color)  // Color.parseColor("#304567")

        return LineData(dataSet)
    }


    private fun setupChart(chart: BarChart) {
        chart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return when (value.toInt()) {
                    0 -> "Dom"
                    1 -> "Lun"
                    2 -> "Mar"
                    3 -> "Mer"
                    4 -> "Gio"
                    5 -> "Ven"
                    6 -> "Sab"
                    else -> ""
                }
            }
        }
        chart.description = null
        chart.xAxis.granularity = 1f // Imposta la granularità a 1 per visualizzare solo i valori interi
        chart.xAxis.isGranularityEnabled = true // Abilita la granularità
        chart.axisLeft.isEnabled = true // Disabilita l'asse Y sinistro se non serve
        chart.axisRight.isEnabled = true // Disabilita l'asse Y destro se non serve
        chart.animateX(1000)
        chart.invalidate() // Refresh the chart
    }


    private fun createBarCharDate(label: String, color: Int, dataObjects: Array<StepsData>): BarData {



        val entries: MutableList<BarEntry> = ArrayList()

        for (data in dataObjects) {
            entries.add(BarEntry(data.day_of_week.toFloat(), data.daily_steps.toFloat()))
        }

        val dailyStepsMap = mutableMapOf<Int, Float>()
        for (entry in entries) {
            dailyStepsMap[entry.x.toInt()] = entry.y
        }

        for (day in 0..6) {
            val steps = dailyStepsMap.getOrDefault(day, 0f)
            entries.add(BarEntry(day.toFloat(), steps))
        }

        entries.sortBy { it.x }
        val set = BarDataSet(entries, label)
        val data = BarData(set)
        data.barWidth = 0.7f

        return data
    }

    private fun createPieCharDate(data: Array<ActivitiesGraphData>): PieData {


        val entries: MutableList<PieEntry> = ArrayList()

        val sumActivities = data.sumOf { it.number }



        for ( entry in data ) {

            entries.add(PieEntry(((entry.number.toFloat() / sumActivities.toFloat()) * 100), entry.type))
        }
        val set = PieDataSet(entries, "Activity Types")

        set.colors = arrayListOf(Color.GREEN, Color.YELLOW, Color.WHITE, Color.GRAY)


        return PieData(set)
    }


    private fun changeFragment(fragment: Fragment, tag: String) {

        val currentFragment = parentFragmentManager.findFragmentById(R.id.fragmentContainerView)

        if (currentFragment != null && currentFragment.tag == tag) {
            return
        }


        val storedFragment = parentFragmentManager.findFragmentByTag(tag)

        if ( storedFragment != null ) {
            parentFragmentManager.beginTransaction().run {
                if (currentFragment != null) {
                    detach(currentFragment)
                }
                attach(storedFragment)
                commit()
            }
        }

        else {

            parentFragmentManager.beginTransaction().run {
                setReorderingAllowed(true)
                if (currentFragment != null) {
                    detach(currentFragment)
                }
                add(R.id.fragmentContainerView,fragment,tag)
                addToBackStack(null)
                commit()
            }
        }
    }


}
