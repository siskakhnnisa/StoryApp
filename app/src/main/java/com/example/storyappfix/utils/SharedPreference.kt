package com.example.storyappfix.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.securepreferences.SecurePreferences

class SharedPreference(context: Context) {
    private var pref: SharedPreferences = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val spec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        val masterKey = MasterKey.Builder(context)
            .setKeyGenParameterSpec(spec)
            .build()
        EncryptedSharedPreferences
            .create(
                context,
                SHARED_PREFERENCE_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    } else {
        SecurePreferences(context, SHARED_PREFERENCE_PASSWORD, SHARED_PREFERENCE_FILE_NAME)
    }

    private val editor: SharedPreferences.Editor = pref.edit()

    private fun setString(key: String, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    private fun setInt(key: String, value: Int?) {
        editor.putInt(key, value ?: 0)
        editor.apply()
    }

    private fun setBoolean(key: String, value: Boolean?) {
        editor.putBoolean(key, value ?: false)
        editor.apply()
    }

    fun setToken(token: String?) {
        setString(TOKEN_SHARED_PREF_KEY, token)
    }

    fun setUserId(id: String?) {
        setString(USER_ID_SHARED_PREF_KEY, id)
    }

    fun setName(name: String?) {
        setString(NAME_SHARED_PREF_KEY, name)
    }

    fun getToken() = pref.getString(TOKEN_SHARED_PREF_KEY, null)

    fun getUserId() = pref.getString(USER_ID_SHARED_PREF_KEY, null)

    fun getName() = pref.getString(NAME_SHARED_PREF_KEY, null)

    fun clearPreference() = editor.clear().apply()

    companion object{
        const val SHARED_PREFERENCE_FILE_NAME = "SHARED_PREFERENCE_FILE_NAME"
        const val SHARED_PREFERENCE_PASSWORD = "283b65a4-cd14-46d7-921f-1f99f656a76b"
        const val NAME_SHARED_PREF_KEY = "NAME_SHARED_PREF_KEY"
        const val USER_ID_SHARED_PREF_KEY = "USER_ID_SHARED_PREF_KEY"
        const val TOKEN_SHARED_PREF_KEY = "TOKEN_SHARED_PREF_KEY"
    }
}