package com.example.know_it_all.util

import java.security.MessageDigest

object HashUtil {
    fun sha256(input: String): String {
        val bytes = input.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digestedBytes = messageDigest.digest(bytes)
        return digestedBytes.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun generateTransactionHash(
        transactionData: String,
        previousHash: String
    ): String {
        val combined = transactionData + previousHash
        return sha256(combined)
    }
}
