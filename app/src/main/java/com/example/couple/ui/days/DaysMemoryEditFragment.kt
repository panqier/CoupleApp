package com.example.couple.ui.days

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.couple.R
import com.example.couple.databinding.DaysMemoryEditBinding
import com.example.couple.util.DateUtil
import java.util.Calendar

class DaysMemoryEditFragment: Fragment(), DatePickerDialog.OnDateSetListener {
    private var calendar = Calendar.getInstance()
    private lateinit var daysViewModel: DaysViewModel
    private lateinit var binding: DaysMemoryEditBinding
    private var position = 0

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
        binding = DaysMemoryEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
        observers()
    }

    private fun setUpListeners(){
        binding.daysMemoryEditDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
        binding.daysMemoryCellSaveBtn.setOnClickListener {
            val navController = findNavController()
            val eventText = binding.daysMemoryEditEventText.text.toString().trim()
            val date = binding.daysMemoryEditDate.text.toString().trim()
            if (eventText.isEmpty() || date.isEmpty()) {
                Toast.makeText(context, "Input cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (daysViewModel.isNavigateFromCard.value == true) {
                daysViewModel.updateDay(eventText, date, requireContext(), navController)
            } else {
                daysViewModel.addNewDay(eventText, date, requireContext(), navController)
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        binding.daysMemoryEditDate.text = DateUtil.getRequestDateStringFormat(calendar.time)
    }

    private fun observers() {
        daysViewModel.selectedDay.observe(viewLifecycleOwner) { day ->
            if (day != null) {
                binding.daysMemoryEditEventText.setText(day.type)
                binding.daysMemoryEditDate.setText(day.date)
            } else {
                binding.daysMemoryEditEventText.setHint(R.string.add_event)
                binding.daysMemoryEditDate.setText(R.string.days_memory_edit_time)
            }
        }

        daysViewModel.selectedPosition.observe(viewLifecycleOwner) { currPosition ->
            if(currPosition != null) {
                position = currPosition
            }
        }
    }

}