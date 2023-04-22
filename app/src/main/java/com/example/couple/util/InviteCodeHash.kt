package com.example.couple.util

import java.io.Serializable
import java.security.MessageDigest

class InviteCodeHash: Serializable {

    companion object {
        fun generateInviteCode(email: String): String {
            // Get the bytes of the email string
            val emailBytes = email.toByteArray()

            // Create a MessageDigest object for the SHA-256 algorithm
            val digest = MessageDigest.getInstance("SHA-256")

            // Hash the email bytes
            val hashedBytes = digest.digest(emailBytes)

            // Convert the hashed bytes to a hexadecimal string
            val hexString = StringBuilder()
            for (byte in hashedBytes) {
                hexString.append(String.format("%02x", byte))
            }

            // Return the first 8 characters of the hexadecimal string
            return hexString.substring(0, 8)
        }
    }
}