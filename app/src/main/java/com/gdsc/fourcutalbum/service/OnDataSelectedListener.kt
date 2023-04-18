package com.gdsc.fourcutalbum.service

// BottomSheetFragment에서 사용할 인터페이스 정의
interface OnDataSelectedListener {
    fun onDataSelected(people: Int, studio: String, hashtags: ArrayList<String>)
}