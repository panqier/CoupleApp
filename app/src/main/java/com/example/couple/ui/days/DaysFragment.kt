package com.example.couple.ui.days

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.couple.R
import com.example.couple.data.days.days
import com.example.couple.databinding.FragmentDaysBinding

class DaysFragment : Fragment() {

    private lateinit var daysViewModel: DaysViewModel
    private lateinit var daysListBindingAdapter: DaysListBindingAdapter
    private var _binding: FragmentDaysBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun setUpDaysListRecyclerView() {
        binding.daysListRecyclerView.apply {
            val mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
        }
        daysViewModel.daysList.observe(viewLifecycleOwner) {list ->
            if(!list.isNullOrEmpty()){
                daysListBindingAdapter = DaysListBindingAdapter(list)
                binding.daysListRecyclerView.visibility = View.VISIBLE
                binding.daysListRecyclerView.adapter = daysListBindingAdapter
            }
        }
    }
}