package com.gdsc.fourcutalbum.data.model
import java.io.Serializable

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FourCuts(
    val title: String?,
    val photo: Uri,
    val friends: List<String>?,
    val place: String?,
    val comment: String?
): Serializable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
