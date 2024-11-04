package com.example.progettolam.UI.calendarFragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.R
import com.example.progettolam.UI.Activities.ActivityAdapter
import com.example.progettolam.UI.Activities.OldActivityInsertFragment
import com.example.progettolam.UI.Activities.ResumeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.channels.FileLock
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class CalendarFragment: Fragment() {
    private lateinit var viewModel: ActivityViewModel
    private lateinit var calendarView: com.kizitonwose.calendar.view.CalendarView
    private lateinit var recyclerActivity: RecyclerView
    private lateinit var monthView: TextView
    private lateinit var addActivityButton: FloatingActionButton

    private lateinit var createFileLauncher: ActivityResultLauncher<String>
    private lateinit var getFileLauncher: ActivityResultLauncher<String>




    val activityViewModel by lazy {
        val factory = ActivityViewModelFactory(ActivityRepository(requireActivity().application))
        ViewModelProvider(this, factory)[ActivityViewModel::class.java]
    }

    companion object {
        const val NEW_ACTIVITY_PAGE_INSERT = "newActivityPage"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.calendar_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activityAdapter = ActivityAdapter(listOf()) { id -> handleOpenDetailActivity(id) }


        createFileLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
            uri?.let {
                // Chiamata alla funzione di esportazione dei dati
                CoroutineScope(Dispatchers.IO).launch {
                    activityViewModel.exportDBtoCSV(requireContext(),it) // Passa l'uri corretto
                }
            }
        }


        getFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    activityViewModel.importCSVtoDB(requireContext(),it)
                }
            }
        }


        calendarView = view.findViewById(R.id.calendarView)
        monthView = view.findViewById(R.id.monthView)
        addActivityButton = view.findViewById(R.id.floatingActionButton)
        recyclerActivity = view.findViewById(R.id.recyclerActivity)
        recyclerActivity.adapter = activityAdapter
        recyclerActivity.layoutManager = LinearLayoutManager(requireActivity())

        val currentMonth = activityViewModel.currentMonth
        val startMonth = currentMonth.minusMonths(100) // Adjust as needed
        val endMonth = currentMonth.plusMonths(100) // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        calendarView.setup(startMonth, endMonth,firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        activityViewModel.selectedDate?.let {
            calendarView.notifyDateChanged(it)

        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view) {
                day -> onDateClicked(day)
            }

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate) {
                    if(activityViewModel.selectedDate == null && activityAdapter.activities != null) {
                        activityAdapter.apply {
                            activities = null
                            notifyDataSetChanged()
                        }

                    }

                    container.textView.visibility = View.VISIBLE

                    if (data.date == activityViewModel.selectedDate) {

                        activityViewModel.getAllActivites(activityViewModel.selectedDate).observe(requireActivity()) { newActivities ->
                            activityAdapter.apply {
                                activities = newActivities
                                notifyDataSetChanged()
                            }
                        }

                        container.textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        container.textView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent))
                        container.textView.setBackgroundResource(R.drawable.border_custom_circle)
                    } else if (data.date == LocalDate.now()) {
                        container.textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.current_data))
                        container.textView.setBackgroundResource(R.drawable.border_custom_rectangle)
                    } else {
                        // If this is NOT the selected date, remove the background and reset the text color.
                        container.textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))
                        container.textView.setBackgroundResource(R.drawable.border_custom_rectangle)
                    }
                } else {
                    container.textView.visibility = View.INVISIBLE
                }

            }
        }


        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                if (container.titlesContainer.tag == null) {
                    container.titlesContainer.tag = data.yearMonth
                    container.titlesContainer.children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            val daysOfWeek = data.weekDays.first().map { it.date.dayOfWeek }
                            val dayOfWeek = daysOfWeek[index]
                            val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            textView.text = title.uppercase()
                        }
                }
            }
        }


        calendarView.monthScrollListener = object : MonthScrollListener {
            override fun invoke(p1: CalendarMonth) {
                val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
                val yearMonth = p1.yearMonth.format(formatter)
                monthView.text = yearMonth.toString().uppercase()
                activityViewModel.currentMonth = p1.yearMonth
            }
        }

        addActivityButton.setOnClickListener {
            changeFragment(OldActivityInsertFragment(), NEW_ACTIVITY_PAGE_INSERT)
        }

    //   showSaveFileDialog()
      //  getFileLauncher.launch("*/*")

    }

    private fun onDateClicked(day: CalendarDay) {
        if (day.position == DayPosition.MonthDate) {
            val currentSelection = activityViewModel.selectedDate
            if (currentSelection == day.date) {
                activityViewModel.selectedDate = null
                calendarView.notifyDateChanged(currentSelection)
            } else {
                activityViewModel.selectedDate = day.date
                calendarView.notifyDateChanged(day.date)
                if (currentSelection != null) {
                    calendarView.notifyDateChanged(currentSelection)
                }
            }
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

    private fun handleOpenDetailActivity(id: String) {
        val resumeActivity = ResumeActivity()
        // Create a bundle to hold the id
        val bundle = Bundle()
        bundle.putString("idActivity", id)
        // Set the arguments for ResumeActivity
        resumeActivity.arguments = bundle
        // Using a different tag each time to avoid the use of a fragment which is already in the backstack but refers to a different activity
        changeFragment(resumeActivity,  "resumeActivity_$id")
    }



    private fun showSaveFileDialog() {
        createFileLauncher.launch("exported_activities.csv")
    }




}