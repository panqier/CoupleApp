package com.example.couple.ui.days

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.couple.R
import com.example.couple.data.data.Days
import com.example.couple.data.data.UserProfileRepository
import com.example.couple.data.data.getDays
import com.example.couple.data.data.getStartDate
import com.example.couple.databinding.FragmentDaysBinding
import com.example.couple.util.DateUtil
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

class DaysFragment : Fragment() ,DatePickerDialog.OnDateSetListener{
    private var calendar = Calendar.getInstance()
    private lateinit var daysViewModel: DaysViewModel
    private lateinit var daysListBindingAdapter: DaysListBindingAdapter
    private var _binding: FragmentDaysBinding? = null
    private val binding get() = _binding!!
    private lateinit var updatedList : MutableList<Days>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            daysViewModel = ViewModelProvider(it).get(DaysViewModel::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDaysBinding.inflate(inflater, container, false).apply {
            viewModel = daysViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpDaysListRecyclerView()
        setUpListeners()
        setUpCardView()
    }

    private fun setUpDaysListRecyclerView() {
        binding.daysListRecyclerView.visibility = View.VISIBLE
        binding.daysListRecyclerView.apply {
            val mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
        }
        if (getDays(requireContext()).isEmpty()) {
            daysViewModel.daysList.observe(viewLifecycleOwner) { list ->
                if (!list.isNullOrEmpty()) {
                    daysListBindingAdapter = DaysListBindingAdapter(list, daysViewModel)
                    binding.daysListRecyclerView.adapter = daysListBindingAdapter
                }
            }
        } else {
            refreshDaysList()
        }
    }


    private fun setUpListeners() {
        binding.daysFloatingAddButton.setOnClickListener {
            daysViewModel.selectedDay.value = null
            Navigation.findNavController(it).navigate(R.id.action_navigation_days_to_navigation_days_edit)
            daysViewModel.isNavigateFromCard.postValue(false)
        }

        binding.daysCardLayout.root.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            val currentDate = Calendar.getInstance()
            datePickerDialog.datePicker.maxDate = currentDate.timeInMillis
            datePickerDialog.show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        val startDate = DateUtil.getRequestDateStringFormat(calendar.time)
        binding.daysCardLayout.daysCardStartDate.text = startDate
        binding.daysCardLayout.daysCardDaysTogetherText.text = requireContext().resources.getString(
            R.string.days_card_together_days,
            kotlin.math.abs(differentDays(DateUtil.getRequestDateStringFormat(calendar.time))).toString()
        )
        daysViewModel.updateStartDate(startDate, requireContext())
    }

    private fun differentDays(date: String): Int {
        return DateUtil.differenceInDates(
            LocalDate.now().toString(),
            date
        )
    }

    private fun setUpCardView() {
        binding.daysCardLayout.daysCardStartDate.text = getStartDate(requireContext())
        binding.daysCardLayout.daysCardDaysTogetherText.text = requireContext().resources.getString(
            R.string.days_card_together_days,
            kotlin.math.abs(differentDays(getStartDate(requireContext())?: "1")).toString()
        )
    }

    private fun refreshDaysList() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                UserProfileRepository.retrieveCurrentDays(requireContext()).await()
                updatedList = getDays(requireContext())
                daysListBindingAdapter = DaysListBindingAdapter(updatedList, daysViewModel)
                binding.daysListRecyclerView.adapter = daysListBindingAdapter
            } catch (e: Exception) {
                // Handle the exception here
            }
        }
    }

}