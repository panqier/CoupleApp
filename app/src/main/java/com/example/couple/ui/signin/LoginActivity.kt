package com.example.couple.ui.signin

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.couple.MainActivity
import com.example.couple.data.data.userProfile
import com.example.couple.databinding.ActivityLoginBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginActivityViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityLoginBinding
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = this

        fetchSignInMethod()
        initListeners()
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null && currentUser.isEmailVerified){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setUpObservers(){
        viewModel.signInEmail.observe(this, Observer {
            binding.loginEditEmail.text
        })
    }

    private fun fetchSignInMethod(){
        auth = Firebase.auth
        firestore = Firebase.firestore
        binding.signInButton.setOnClickListener {
            val email = binding.loginEditEmail.text.toString().trim()
            val password = binding.loginEditPassword.text.toString().trim()
            if(email.isEmpty()) {
                Toast.makeText(this,"Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(password.isEmpty()) {
                Toast.makeText(this,"Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!isPasswordValid(password)) {
                Toast.makeText(this,"Please make sure your password has one Upper letter, one lower letter and one special symbol",
                Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            binding.loginProgressBar.visibility = View.VISIBLE
            binding.signInButton.visibility = View.GONE
            if (isValidEmail(email) && isPasswordValid(password)) {
                auth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val signInMethods = task.result?.signInMethods
                            if (signInMethods != null && signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                                LogInAccount(email, password)
                            } else {
                                createUserAccount(email, password)
                            }
                            binding.loginProgressBar.visibility = View.GONE
                            binding.signInButton.visibility = View.VISIBLE
                        } else {
                            // Failed to fetch sign-in methods for email
                            Toast.makeText(
                                baseContext, "sign in fetched failure",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener { exception ->
                        // Handle any exceptions that occurred during the operation
                        Log.e("TAG", "Error fetching sign-in methods for email", exception)
                        Toast.makeText(
                            baseContext,
                            "Error fetching sign-in methods for email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(baseContext, "Invalid email or password format", Toast.LENGTH_SHORT).show()
                binding.loginProgressBar.visibility = View.GONE
                binding.signInButton.visibility = View.VISIBLE
            }
        }
    }

    private fun createUserAccount(email: String, password: String) {
        email.let {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.sendEmailVerification()?.addOnSuccessListener {
                            Toast.makeText(baseContext, "Please verify your email",
                                Toast.LENGTH_SHORT).show()
                        }?.addOnFailureListener {
                            Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
                        }

                        //updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }
                }
        }
    }

    private fun LogInAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    val verification = user?.isEmailVerified
                    // Sign in success, update UI with the signed-in user's information
                    if(verification == true) {
                        auth.uid?.let {
                            firestore.collection("User").document(it).set(userProfile(email)) }
                        Toast.makeText(baseContext, "Log In Success",
                            Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Please verify your email",
                            Toast.LENGTH_SHORT).show()
                    }

                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Log In failed",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

    private fun initListeners() {
        binding.forgetPasswordBtn.setOnClickListener {
            ResetPasswordFragment().show(fragmentManager,"reset_password_dialog")
        }
    }
}