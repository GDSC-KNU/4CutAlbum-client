package com.gdsc.fourcutalbum.data.model
/*
* {
	"uid" : string,
	"image" : string, //base64인코딩
	"image_name": string //ex) test.jpg
	"hashtags" : string[],
	"people_count" : int,
	"company" : string,
	"comment" : string,
}
* */
data class CreateFeedRequestModel(
    val uid: String,
    val image: String,
    val image_name : String,
    val hashtags: ArrayList<String>,
    val people_count: Int,
    val company: String,
    val comment : String,
)
