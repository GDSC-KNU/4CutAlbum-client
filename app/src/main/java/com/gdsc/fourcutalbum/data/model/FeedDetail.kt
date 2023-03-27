package com.gdsc.fourcutalbum.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/*
{
	"feed_id": int,
	"like": int,
	"people_count": int,
	"s3_key": string,
	"company_name": string,
	"comment": string,
	"nick_name": string
}
 */
data class FeedDetail(
    val feed_id: Int,
    val like : Int,
    val people_count: Int,
    val company_name: String,
    val comment: String,
    val nick_name: String
)

