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
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
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

        chart = view.findViewById(R.id.chart)
        barChart = view.findViewById(R.id.chart2)
        pieChart = view.findViewById(R.id.chart3)
        pieChart2 = view.findViewById(R.id.chart4)

        val lineData = createLineCharDate("Steps", Color.RED)
        val barData = createBarCharDate("Calories", Color.MAGENTA)
        val pieData = createPieCharDate()
        val pieData2 = createPieCharDate()

        // Assuming you have a LineChart instance named `lineChart`
        chart.data = lineData
        chart.animateX(1000)
        chart.invalidate() // Refresh the chart

        barChart.data = barData
        barChart.animateY(1000)
        barChart.setFitBars(true)
        barChart.invalidate()

        pieChart.setData(pieData)
        pieChart.animateXY(1000, 1500)
        pieChart.invalidate()

        pieChart2.setData(pieData2)
        pieChart2.invalidate()


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

        activityViewModel.getAllStepsFromDay(LocalDate.now()).observe(viewLifecycleOwner) {
        }

        settings.setOnClickListener{
            changeFragment(PreferencesFragment(), R.id.settings.toString())
        }

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

    private fun createLineCharDate(label: String, color: Int): LineData {
        var dataObjects = arrayOf<DataExample>(
            DataExample(1, 5f),
            DataExample(2, 10f),
            DataExample(3, 7f),
            DataExample(4, 12f),
            DataExample(5, 6f),
            DataExample(6, 15f) ,
            DataExample(7, 10f)
        )
        // Create an empty list of Entry objects
        val entries = mutableListOf<Entry>()

        // Convert DataExample objects to MPAndroidChart Entry objects
        for (data in dataObjects) {
            entries.add(Entry(data.date.toFloat(), data.steps))
        }

        val dataSet = LineDataSet(entries, label)
        dataSet.setColor(color)  //Color.parseColor("#304567")

        return LineData(dataSet)
    }

    private fun createBarCharDate(label: String, color: Int): BarData {
        val entries: MutableList<BarEntry> = ArrayList()
        entries.add(BarEntry(0f, 30f))
        entries.add(BarEntry(1f, 80f))
        entries.add(BarEntry(2f, 60f))
        entries.add(BarEntry(3f, 50f))
        // gap of 2f
        entries.add(BarEntry(5f, 70f))
        entries.add(BarEntry(6f, 60f))
        val set = BarDataSet(entries, label)
        val data = BarData(set)
        data.barWidth = 0.9f // set custom bar width

        return data
    }

    private fun createPieCharDate(): PieData {
        val entries: MutableList<PieEntry> = ArrayList()
        entries.add(PieEntry(18.5f, "Green"))
        entries.add(PieEntry(26.7f, "Yellow"))
        entries.add(PieEntry(24.0f, "Red"))
        entries.add(PieEntry(30.8f, "Blue"))
        val set = PieDataSet(entries, "Election Results")

        //set.setColors(arrayListOf(Color.parseColor("#304567"), Color.parseColor("#501567"));
        set.setColors(arrayListOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW))
        return PieData(set)
    }
}