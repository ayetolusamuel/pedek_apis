package com.pedektech.ncym.util

import java.security.SecureRandom


object OTPUtil {
    private val secureRandom = SecureRandom()
    private const val OTP_LENGTH = 6

    fun generateOTP(): String {
        val otp = StringBuilder(OTP_LENGTH)
        for (i in 0 until OTP_LENGTH) {
            otp.append(secureRandom.nextInt(10))
        }
        return otp.toString()
    }
}
