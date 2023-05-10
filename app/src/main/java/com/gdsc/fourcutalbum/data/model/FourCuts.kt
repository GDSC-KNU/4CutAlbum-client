package com.gdsc.fourcutalbum.data.model
import java.io.Serializable

import android.net.Uri
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FourCuts(
    val title: String?,
    val photo: Uri,
    val friends: List<String>?,
    val place: String?,
    val comment: String?,
    @ColumnInfo(defaultValue = "N") @NonNull
    val public_yn: String,
    val people: Int?,
    val hashtag: List<String>?,
    val feed_id: String?
): Serializable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
