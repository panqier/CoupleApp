package com.example.couple.ui.my

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.couple.R
import com.example.couple.ui.signin.LoginActivity
import com.example.couple.databinding.FragmentMyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MyFragment : Fragment() {
    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentMyBinding
    private lateinit var myViewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyBinding.inflate(inflater, container, false).apply {
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
        fetchLogOut()
        observers()
        myViewModel.getDataFiledFromFirestore("userId")
    }

    private fun fetchLogOut() {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        binding.myProfileLogOutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observers() {
        myViewModel.isDataFieldRetrieved.observe(viewLifecycleOwner) {
            if(it) {
                binding.myProfileId.text = getString(R.string.profile_edit_my_id_prefix)+ myViewModel.dataFieldVal
            } else {
                myViewModel.getDataFiledFromFirestore("userId")
            }
        }
    }

}