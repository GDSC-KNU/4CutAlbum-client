package com.gdsc.fourcutalbum.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Feed(
    val feed_id: Int,
    val image : String,
    val people_count: Int,
    val company_name: String,
    val hashtag: ArrayList<String>
) : Serializable

