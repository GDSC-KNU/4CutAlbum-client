package com.gdsc.fourcutalbum.data.model
//{
//    "uid": String,
//    "peopleCount": Long,
//    "comment": String,
//    "company": String,
//    "hashtags": String[]
//}
data class UpdateFeedRequestModel(
    val uid: String,
    val peopleCount: Int,
    val comment: String,
    val company: String,
    val hashtags: ArrayList<String>
)
