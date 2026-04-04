// DISABLED - Missing zxing dependency
// package com.example.know_it_all.util

import android.graphics.Bitmap
import android.graphics.Color

object QRCodeGenerator {
    // TODO: Add com.google.zxing:core:3.5.1 to build.gradle.kts for full QR implementation
    // For now, generating placeholder QR code pattern
    fun generateQRCode(data: String, size: Int = 512): Bitmap? {
        return try {
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            // Simple checkerboard pattern as placeholder
            for (x in 0 until size) {
                for (y in 0 until size) {
                    val isBlack = (x / 20 + y / 20) % 2 == 0
                    bitmap.setPixel(x, y, if (isBlack) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    fun generateSwapQRCode(swapId: String, userId: String, size: Int = 512): Bitmap? {
        // Format: swapId|userId|timestamp for verification
        val qrData = "$swapId|$userId|${System.currentTimeMillis()}"
        return generateQRCode(qrData, size)
    }
}
