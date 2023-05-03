package com.magical.near.common.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object SharedManager {
    private var mSharedPref: SharedPreferences? = null
    var LOGIN_ID                    = "LOGIN_ID"                    // 현재 로그인 한 ID (이메일)
    var AUTH_TOKEN                  = "AUTH_TOKEN"                  // 현재 ID Token 값 (Uid)
    var USER_NAME                   = "USER_NAME"                   // 현재 로그인 한 계정의 닉네임

    fun init(context: Context) {
        if (mSharedPref == null) mSharedPref =
            context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    }

    fun read(key: String?, defValue: String?): String? {
        return mSharedPref!!.getString(key, defValue)
    }

    fun write(key: String?, value: String?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    fun read(key: String?, defValue: Boolean): Boolean {
        return mSharedPref!!.getBoolean(key, defValue)
    }

    fun write(key: String?, value: Boolean) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.apply()
    }

    fun read(key: String?, defValue: Int): Int {
        return mSharedPref!!.getInt(key, defValue)
    }

    fun write(key: String?, value: Int) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putInt(key, value).apply()
    }

    fun read(key: String?, defValue: Long): Long {
        return mSharedPref!!.getLong(key, defValue)
    }

    fun write(key: String?, value: Long) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putLong(key, value).apply()
    }

    fun clear() {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.clear()
        prefsEditor.apply()
    }
}