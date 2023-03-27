package com.gdsc.fourcutalbum.common

/*
 * Author :: Glacier Han
 * Title  :: 앱에 필요한 상수 정의 클래스
 */

class Constants{
    companion object {

        // 서버 URL
        const val SERVER_URL = "http://54.180.148.255:8080/"

        // APIs
        const val API_SIGNUP_CHECK = "signup-check" // 회원가입 체크 API
        const val API_SIGNUP       = "signup"       // 회원가입 API
        const val FEEDS            = "feeds/list"        // 전체 피드 불러오기 API
        const val FEEDDETAIL       = "feeds"        // 전체 피드 불러오기 API

    }
}