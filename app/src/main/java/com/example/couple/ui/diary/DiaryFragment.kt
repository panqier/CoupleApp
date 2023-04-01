package com.example.couple.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.couple.R

class DiaryFragment : Fragment(){

            private lateinit var dairyViewModel: DiaryViewModel

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                dairyViewModel =
                    ViewModelProvider(this).get(DiaryViewModel::class.java)
                val root = inflater.inflate(R.layout.fragment_account, container, false)
                val textView: TextView = root.findViewById(R.id.text_dashboard)
                dairyViewModel.text.observe(viewLifecycleOwner, {
                    textView.text = it
                })
                return root
            }
        }
