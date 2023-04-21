package com.example.couple.ui.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.couple.databinding.MyRelationshipFragmentBinding

class MyRelationshipFragment :Fragment(){
    private lateinit var binding: MyRelationshipFragmentBinding
    private lateinit var myViewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MyRelationshipFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.let {
            myViewModel =
                ViewModelProvider(this)[MyViewModel::class.java]
        }
    }

}