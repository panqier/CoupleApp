package com.example.couple.ui.my

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.couple.data.data.*
import com.example.couple.databinding.MyRelationshipFragmentBinding
import com.example.couple.ui.days.DaysViewModel
import com.example.couple.util.InviteCodeHash
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyRelationshipFragment :Fragment(){
    private lateinit var binding: MyRelationshipFragmentBinding
    private lateinit var myViewModel: MyViewModel
    private lateinit var daysViewModel: DaysViewModel
    private lateinit var daysList: DaysList

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MyRelationshipFragmentBinding.inflate(inflater,container,false).apply {
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
            daysViewModel = ViewModelProvider(this)[DaysViewModel::class.java]
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCopyInviteCode()
        observers()
        myViewModel.getPairedStatus()
    }


    private fun setUpCopyInviteCode() {
        val inviteCode = myViewModel.userEmail?.let { InviteCodeHash.generateInviteCode(it) } ?: ""
        binding.myRelationshipInviteCopyCard.myRelationshipInviteCopySmallCard.myRelationshipCodeText.text = inviteCode
        binding.myRelationshipInviteCopyCard.myRelationshipInviteCopySmallCard.myRelationshipPairButton.setOnClickListener {
        val clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clipData = ClipData.newPlainText("text", inviteCode)
        clipboardManager?.setPrimaryClip(clipData)
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pairWithAnother() {
        binding.myRelationshipEnterInviteCodeCard.myRelationshipEnterCodeCard.myRelationshipEnterButton.setOnClickListener {
            val inputCode =
                binding.myRelationshipEnterInviteCodeCard.myRelationshipEnterCodeCard.myRelationshipPartnerCodeInput.text.toString()
                    .trim()
            if (inputCode.isEmpty()) {
                Toast.makeText(context, "Input cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val db = Firebase.firestore
            val usersRef = db.collection("User")
            val query = usersRef.whereEqualTo("inviteCode", inputCode)

            query.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {

                    if (document.getBoolean("paired") == true) {
                        Toast.makeText(
                            context,
                            "The user you try to pair is already paired with someone",
                            Toast.LENGTH_LONG
                        ).show()
                        return@addOnSuccessListener
                    } else {
                        val userId = document.getString("userId") ?: ""
                        val email = document.getString("email") ?: ""
                        val userName = document.getString("userName") ?: ""
                        val imageUrl = document.getString("imageUrl") ?: ""
                        val inviteCode = document.getString("inviteCode") ?: ""
                        val paired = false
                        // Show dialog to confirm pairing
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Pairing Confirmation")
                        builder.setMessage("Do you want to pair with user $userName (User ID: $userId)?")
                        builder.setPositiveButton("Yes") { dialog, which ->
                            // Update pair status for both users
                            myViewModel.updatePairStatus(true)
                            val currentUserProfile = myViewModel.getUserProfile().value
                            if (currentUserProfile != null) {
                                val currentUserProfileWithPairStatus =
                                    currentUserProfile.copy(paired = true)
                                db.collection("User").document(document.id)
                                    .set(currentUserProfileWithPairStatus)
                            }
                            val partnerProfile =
                                UserProfile(userId, email, userName, imageUrl, inviteCode, paired)
                            val partnerProfileWithPairStatus = partnerProfile.copy(paired = true)
                            db.collection("User").document(document.id).set(partnerProfileWithPairStatus)
                            val currentUserWithPairStatus = getUser(requireContext())!!.copy(paired = true)
                            // Create a new paired document in the "Paired" collection
                            db.collection("Paired").add(
                                Paired(
                                    currentUserWithPairStatus,
                                    partnerProfileWithPairStatus,
                                    daysList
                                )
                            ).addOnSuccessListener { documentReference ->
                                Toast.makeText(context, "Pairing successful!", Toast.LENGTH_SHORT)
                                    .show()
                                dialog.dismiss()

                            }.addOnFailureListener { exception ->
                                Toast.makeText(
                                    context,
                                    "Pairing failed: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()
                            }
                        }
                        builder.setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()
                        }
                        builder.show()
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Cannot find user: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun unpairWithAnother() {
        binding.myRelationshipUnlinkCard.myRelationshipUnlinkButton.setOnClickListener {
            val db = Firebase.firestore
            val pairedRef = db.collection("Paired")
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                pairedRef.whereEqualTo("user1.email", listOf(currentUser.email))
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            for (document in documents) {
                                // Delete the document here
                                unlinkDialog(document)
                            }
                        } else {
                            pairedRef.whereEqualTo("user2.email", currentUser.email)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (!documents.isEmpty) {
                                        for (document in documents) {
                                            // Handle matched document
                                            unlinkDialog(document)
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    // Handle failure
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors here
                        Toast.makeText(context, "Cannot find the paired data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun observers() {
        daysViewModel.daysList.observe(viewLifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {
                daysList = DaysList(list)
            }
        }
        myViewModel.isPaired.observe(viewLifecycleOwner) { isPaired ->
            if (isPaired) {
                unpairWithAnother()
            } else {
                pairWithAnother()
            }
        }
    }

    private fun unlinkDialog( document: QueryDocumentSnapshot) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Unlink Confirmation")
        builder.setMessage("Do you want to unlink this relationship?")
        builder.setPositiveButton("Yes") { dialog, which ->
            document.reference.delete()
            myViewModel.updatePairStatus(false)
            dialog.dismiss()
            Toast.makeText(
                context,
                "Unlink Successfully!",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

}