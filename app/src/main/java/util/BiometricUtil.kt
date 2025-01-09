package util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt

object BiometricUtil {

    fun hasBiometricCapability(context: Context): Int {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate()
    }

    fun isBiometricReady(context: Context) =
            hasBiometricCapability(context) == BiometricManager.BIOMETRIC_SUCCESS

    fun setBiometricPromptInfo(
            title: String,
            subtitle: String,
            description: String,
            allowDeviceCredential: Boolean
    ): BiometricPrompt.PromptInfo {
        val builder = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)

        // Use Device Credentials if allowed, otherwise show Cancel Button
        builder.apply {
            if (allowDeviceCredential) setDeviceCredentialAllowed(true)
            else setNegativeButtonText("Cancel")
        }

        return builder.build()
    }


}