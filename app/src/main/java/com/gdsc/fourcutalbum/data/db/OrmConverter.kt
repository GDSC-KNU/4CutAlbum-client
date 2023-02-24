package com.gdsc.fourcutalbum.data.db

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson

class OrmConverter {
    @TypeConverter
    fun fromList(value: List<String>?) = Gson().toJson(value)
    @TypeConverter
    fun toList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()

    @TypeConverter
    fun fromUri(value: Uri) = value.toString()
    @TypeConverter
    fun toUri(value: String) = Uri.parse(value)
}