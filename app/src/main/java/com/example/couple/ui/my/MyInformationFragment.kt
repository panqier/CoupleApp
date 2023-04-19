package com.example.couple.ui.my

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.couple.databinding.MyInformationFragmentBinding

class MyInformationFragment: NavHostFragment() {
    private lateinit var myViewModel: MyViewModel
    private lateinit var binding: MyInformationFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MyInformationFragmentBinding.inflate(inflater, container, false).apply {
            viewModel = myViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.let {
            myViewModel =
                ViewModelProvider(this)[MyViewModel::class.java]
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel.getDataFiledFromFirestore("userId")
        observers()
        context?.let { setUpCardViews(it) }
    }

    private fun setUpCardViews(context: Context) {
        listOf(
            binding.includeMyInfoEmailCard.myInfoEmailCard to binding.includeMyInfoEmailCard.myInfoEmailText,
            binding.includeMyInfoIdCard.myInfoIdCard to binding.includeMyInfoIdCard.myInfoIdText,
            binding.includeMyInfoNameCard.myInfoNameCard to binding.includeMyInfoNameCard.myInfoNameText
        ).forEach { (cardView, textView) ->
            cardView.setOnClickListener  {
                val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", textView.text)
                clipboardManager.setPrimaryClip(clipData)
            }
        }
    }

    private fun observers() {
        myViewModel.isDataFieldRetrieved.observe(viewLifecycleOwner) {
            if(it) {
                binding.includeMyInfoIdCard.myInfoIdText.text = myViewModel.dataFieldVal
            } else {
                myViewModel.getDataFiledFromFirestore("userId")
            }
        }
    }

}