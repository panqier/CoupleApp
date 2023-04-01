package com.example.couple.ui.square

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.couple.R

class SquareFragment : Fragment() {

    private lateinit var squareViewModel: SquareViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        squareViewModel =
                ViewModelProvider(this).get(SquareViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_square, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        squareViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}