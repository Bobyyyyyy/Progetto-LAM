package com.example.progettolam.UI.profileFragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.R
import com.example.progettolam.UI.preferencesActivity.PreferencesFragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate


class ProfileFragment: Fragment() {
    private lateinit var profileModel: ProfileViewModel
    private lateinit var textView: TextView
    private lateinit var settings: ImageView
    private lateinit var shareBtn: ImageView
    private lateinit var importBtn: ImageView
    private lateinit var heightView: TextView
    private lateinit var weightView: TextView
    private lateinit var valueWalkTextView: TextView
    private lateinit var valueRunTextView: TextView
    private lateinit var valueChillTextView: TextView
    private lateinit var valueDriveTextView: TextView
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart

    private lateinit var createFileLauncher: ActivityResultLauncher<String>
    private lateinit var getFileLauncher: ActivityResultLauncher<String>

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
        shareBtn = view.findViewById(R.id.shareBtn)
        importBtn = view.findViewById(R.id.importBtn)

        barChart = view.findViewById(R.id.chart2)
        pieChart = view.findViewById(R.id.chart3)

        valueWalkTextView = view.findViewById(R.id.valueWalkTextView)
        valueRunTextView = view.findViewById(R.id.valueRunTextView)
        valueChillTextView = view.findViewById(R.id.valueChillTextView)
        valueDriveTextView = view.findViewById(R.id.valueDriveTextView)

        createFileLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
            uri?.let {
                // Call to the data export functions
                CoroutineScope(Dispatchers.IO).launch {
                    activityViewModel.exportDBtoCSV(requireContext(),it) // Pass the correct uri
                }

                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "text/csv"
                }
                startActivity(Intent.createChooser(shareIntent, null))
            }
        }

        getFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    activityViewModel.importCSVtoDB(requireContext(),it)
                }
            }
        }

        shareBtn.setOnClickListener {
            showSaveFileDialog()
        }
        importBtn.setOnClickListener {
            getFileLauncher.launch("*/*")
        }

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
            val pieData = createPieCharDate(it)
            pieChart.setData(pieData)
            pieChart.description = null
            pieChart.animateXY(1000, 1500)
            pieChart.invalidate()
        }

        activityViewModel.getCurrentWeekSteps(LocalDate.now()).observe(viewLifecycleOwner) {
            if (it != null) {
                val barData = createBarCharDate(getString(R.string.steps_tag), it)
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

    private fun showSaveFileDialog() {
        createFileLauncher.launch("exported_activities.csv")
    }

    private fun setupChart(chart: BarChart) {
        chart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return when (value.toInt()) {
                    0 -> getString(R.string.monday)
                    1 -> getString(R.string.tuesday)
                    2 -> getString(R.string.wednesday)
                    3 -> getString(R.string.thursday)
                    4 -> getString(R.string.friday)
                    5 -> getString(R.string.saturday)
                    6 -> getString(R.string.sunday)
                    else -> ""
                }
            }
        }
        chart.description = null
        chart.xAxis.granularity = 1f // Set granularity to 1 to display only integer values
        chart.xAxis.isGranularityEnabled = true // Enables granularity
        chart.getAxis(YAxis.AxisDependency.LEFT).axisMinimum = 0f
        chart.getAxis(YAxis.AxisDependency.LEFT).granularity = 1f
        chart.axisLeft.isEnabled = true // Disable the left Y axis if not needed
        chart.axisRight.isEnabled = false // Disable the right Y-axis if not needed
        chart.animateX(1000)
        chart.invalidate() // Refresh the chart
    }

    private fun createBarCharDate(label: String, dataObjects: Array<StepsData>): BarData {
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

    private fun getFormattedTypeActivity(type: ActivityType): String {
        val formattedType = when (type) {
            ActivityType.WALKING -> getString(R.string.walk_tag)
            ActivityType.RUNNING -> getString(R.string.run_tag)
            ActivityType.DRIVING -> getString(R.string.drive_tag)
            ActivityType.STILL -> getString(R.string.chilling_tag)
        }
        return formattedType
    }

    private fun updateUiValueTypeActivity(type: ActivityType, value: Int) {
        when (type) {
            ActivityType.WALKING -> valueWalkTextView.text = value.toString()
            ActivityType.RUNNING -> valueRunTextView.text = value.toString()
            ActivityType.DRIVING -> valueDriveTextView.text = value.toString()
            ActivityType.STILL -> valueChillTextView.text = value.toString()
        }
    }

    private fun createPieCharDate(data: Array<ActivitiesGraphData>): PieData {
        val entries: MutableList<PieEntry> = ArrayList()
        val sumActivities = data.sumOf { it.number }

        for (entry in data) {
            entries.add(PieEntry(((entry.number.toFloat() / sumActivities.toFloat()) * 100), getFormattedTypeActivity(entry.type)))
            updateUiValueTypeActivity(entry.type, entry.number)
        }
        val set = PieDataSet(entries, "")
        set.colors = arrayListOf(Color.GREEN, Color.RED, Color.BLUE, Color.MAGENTA)
        set.valueTextSize = 15f
        set.valueTextColor = Color.WHITE
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
        } else {
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
