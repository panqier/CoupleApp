package com.example.couple.ui.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.couple.databinding.ResetPasswordFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class ResetPasswordFragment : BottomSheetDialogFragment() {
    private lateinit var binding: ResetPasswordFragmentBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ResetPasswordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPeekHeight()
        resetPasswordLink()
        binding.resetPasswordCloseBtn.setOnClickListener { dismiss() }
    }

    private fun resetPasswordLink() {
        auth = Firebase.auth
        binding.resetPasswordBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val email = binding.resetPasswordEmail.text.toString().trim()
            if(email.isEmpty() || !isValidEmail(email)) {
                Toast.makeText(context, "Please enter valid email",
                    Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context, "Please check your email",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context, "Send failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun setUpPeekHeight() {
        dialog?.let {
            it.setOnShowListener { dialog ->
                val bottomSheetDialog = dialog as BottomSheetDialog
                val bottomSheet =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.let {
                    val behavior = BottomSheetBehavior.from(bottomSheet)
                    val layoutParams = bottomSheet.layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    behavior.peekHeight = requireContext().resources.displayMetrics.heightPixels
                    bottomSheet.layoutParams = layoutParams
                }
            }
        }
    }
}