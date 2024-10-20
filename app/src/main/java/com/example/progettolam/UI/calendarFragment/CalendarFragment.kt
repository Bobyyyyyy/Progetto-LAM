package com.example.progettolam.UI.calendarFragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.R
import com.example.progettolam.UI.Activities.ActivityAdapter
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class CalendarFragment: Fragment() {
    private lateinit var viewModel: ActivityViewModel
    private lateinit var calendarView: com.kizitonwose.calendar.view.CalendarView
    private lateinit var recyclerActivity: RecyclerView
    private lateinit var monthView: TextView


    val activityViewModel by lazy {
        val factory = ActivityViewModelFactory(ActivityRepository(requireActivity().application))
        ViewModelProvider(this, factory)[ActivityViewModel::class.java]
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

        val activityAdapter = ActivityAdapter(listOf())


        calendarView = view.findViewById(R.id.calendarView)
        monthView = view.findViewById(R.id.monthView)
        recyclerActivity = view.findViewById(R.id.recyclerActivity)
        recyclerActivity.adapter = activityAdapter
        recyclerActivity.layoutManager = LinearLayoutManager(requireActivity())

        val currentMonth = activityViewModel.currentMonth
        val startMonth = currentMonth.minusMonths(100) // Adjust as needed
        val endMonth = currentMonth.plusMonths(100) // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        calendarView.setup(startMonth, endMonth,firstDayOfWeek)

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



                        container.textView.setTextColor(Color.WHITE)
                        container.textView.setBackgroundColor(Color.BLACK)
                    } else if (data.date == LocalDate.now()) {
                        container.textView.setTextColor(Color.RED)
                        container.textView.setBackgroundResource(R.drawable.border_custom_rectangle)
                        }
                        else {
                            // If this is NOT the selected date, remove the background and reset the text color.
                            container.textView.setTextColor(Color.BLACK)
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
                // Listener per cambiare scritta di anno + mese
                val yearMonth =  p1.yearMonth.month.toString() + ", " + p1.yearMonth.year.toString()
                monthView.text = yearMonth
                activityViewModel.currentMonth = p1.yearMonth
            }
        }



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

}