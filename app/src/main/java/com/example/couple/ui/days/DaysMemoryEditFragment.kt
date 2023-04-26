package com.example.couple.ui.days

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.couple.databinding.DaysMemoryEditBinding
import com.example.couple.util.DateUtil
import java.util.Calendar

class DaysMemoryEditFragment: Fragment(), DatePickerDialog.OnDateSetListener {
    private var calendar = Calendar.getInstance()
    private lateinit var daysViewModel: DaysViewModel
    private lateinit var binding: DaysMemoryEditBinding

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
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        binding.daysMemoryEditDate.text = DateUtil.getRequestDateStringFormat(calendar.time)
    }
}