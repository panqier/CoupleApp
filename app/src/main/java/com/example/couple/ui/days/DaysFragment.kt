package com.example.couple.ui.days

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.couple.R
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
        setUpListeners()
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

    private fun setUpListeners() {
        binding.daysFloatingAddButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_navigation_days_to_navigation_days_edit)
        }
    }
}