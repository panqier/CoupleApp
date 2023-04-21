package com.example.couple.ui.my

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.couple.R
import com.example.couple.data.data.saveUserName
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
        setUpListeners()
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
        setUpUserHeader()
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
                binding.myProfileId.text = getString(R.string.profile_edit_my_id_prefix) + myViewModel.dataFieldVal
            } else {
                myViewModel.getDataFiledFromFirestore("userId")
            }
        }
    }

    private fun setUpUserHeader() {
        binding.myProfileNameTextview.addTextChangedListener {
            myViewModel.userName.value = it.toString().trim()
            myViewModel.updateUserName(it.toString().trim())
            saveUserName(requireContext(), it.toString().trim())
        }
        binding.myProfilePhoto.setOnClickListener {
            selectImage()
        }
    }

    private fun setUpListeners() {
        binding.includeMyInfoCard.root.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_myFragment_to_myInformationFragment)
        }
        binding.includeRelationshipCard.root.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_navigation_my_to_navigation_my_relationship)
        }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            binding.myProfilePhoto.setImageURI(it)
            myViewModel.uploadUserImage(it)
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getContent.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}