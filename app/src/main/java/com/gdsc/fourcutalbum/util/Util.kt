package com.gdsc.fourcutalbum.util

class Util {
    fun peopleToValue(people: String) : Int{
        return when(people){
            "혼자서" -> 1
            "둘이서" -> 2
            "셋이서" -> 3
            "넷이서" -> 4
            "다섯명 이상" -> 5
            else -> 1
        }
    }
}