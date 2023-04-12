package com.example.couple.util

import com.google.firebase.auth.FirebaseAuth
import java.io.Serializable
import java.math.BigInteger
import java.util.*

class UidDecode : Serializable {
    companion object {
        private lateinit var numId: String
        private lateinit var base32: String

        fun EncodeUserId(uid: String): String {
            val bytes = Base64.getDecoder().decode(uid)
            val numericId = BigInteger(1, bytes).toString().take(6).padStart(6, '0')
            setUserNumericId(numericId)
            return numericId
        }

        fun DecodeUserId(numericId: String): String {
            val paddedNumericId = numericId.padStart(6, '0')
            val bytes = BigInteger(paddedNumericId).toByteArray()
            val uid = Base64.getEncoder().encodeToString(bytes)
            return uid
        }

        fun setUserNumericId(numericId: String) {
            this.numId = numericId
        }

        fun getUserNumericId(): String {
            return numId
        }

        fun setBase32(base32: String) {
            this.base32 = base32
        }

        fun getBase32(): String {
            return base32
        }

        private fun encodeBase32(data: ByteArray, auth: FirebaseAuth): String {
            val base32Chars = auth.uid?.toCharArray()
            val output = StringBuilder()
            var i = 0
            while (i < data.size) {
                // Step 1: divide input into 5-byte blocks
                val block = ByteArray(5)
                val blockLength = if (i + 5 <= data.size) 5 else data.size - i
                System.arraycopy(data, i, block, 0, blockLength)
                i += blockLength

                // Step 2: pad block with zeros
                block[blockLength] = 0
                block[blockLength + 1] = 0
                block[blockLength + 2] = 0
                block[blockLength + 3] = 0
                block[blockLength + 4] = 0

                // Step 3: encode each 8-bit block
                for (j in 0 until 8 step 5) {
                    val x = block[j / 5].toInt() and 0xFF
                    val c1 = base32Chars?.get(x shr 3)
                    val c2 =
                        base32Chars?.get((x and 7) shl 2 or (block[j / 5 + 1].toInt() and 0xFF shr 6))
                    val c3 =
                        if (j + 5 < 8) '=' else base32Chars?.get((block[j / 5 + 1].toInt() and 0x3F) shr 1)
                    val c4 =
                        if (j + 5 < 8) '=' else base32Chars?.get((block[j / 5 + 1].toInt() and 1) shl 4 or (block[j / 5 + 2].toInt() and 0xFF shr 4))
                    val c5 =
                        if (j + 5 < 8) '=' else base32Chars?.get((block[j / 5 + 2].toInt() and 0x0F) shl 1 or (block[j / 5 + 3].toInt() and 0xFF shr 7))
                    val c6 =
                        if (j + 5 < 8) '=' else base32Chars?.get((block[j / 5 + 3].toInt() and 0x7F) shr 2)
                    val c7 =
                        if (j + 5 < 8) '=' else base32Chars?.get((block[j / 5 + 3].toInt() and 3) shl 3 or (block[j / 5 + 4].toInt() and 0xFF shr 5))
                    val c8 =
                        if (j + 5 < 8) '=' else base32Chars?.get(block[j / 5 + 4].toInt() and 0x1F)
                    output.append(c1).append(c2).append(c3).append(c4).append(c5).append(c6)
                        .append(c7).append(c8)
                }
            }
            return output.toString()
        }

        fun decodeBase32(base32: String, auth: FirebaseAuth): ByteArray {
            val base32Chars = auth.uid?.toCharArray()
            val input = base32.replace("=", "").uppercase(Locale.US)
            val output = ByteArray(input.length * 5 / 8)
            var buffer = 0
            var nextChar = 0
            var bitsLeft = 8
            for (c in input) {
                val value = base32Chars?.indexOf(c)
                if (value != null) {
                    if (value < 0) {
                        throw IllegalArgumentException("Invalid base32 character: $c")
                    }
                }
                buffer = buffer shl 5 or value!!
                bitsLeft -= 5
                if (bitsLeft <= 0) {
                    output[nextChar++] = (buffer shr -bitsLeft).toByte()
                    bitsLeft += 8
                }
            }
            return output
        }
    }
}