package com.gdsc.fourcutalbum.service

import com.gdsc.fourcutalbum.common.Constants
import com.gdsc.fourcutalbum.data.model.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*


interface HttpService {

    @GET(Constants.API_SIGNUP_CHECK)
    @Headers("accept: application/json","charset:utf-8")
    fun getSignupCheck(
        @Query("uid") uid: String
    ): Call<SignupResponseModel>

    @GET(Constants.FEEDDETAIL)
    @Headers("accept: application/json","charset:utf-8")
    fun getFeedDetail(
        @Query("id") id: Int
    ): Call<FeedDetail>

    @POST(Constants.API_SIGNUP)
    @Headers("accept: application/json","charset:utf-8")
    fun getSignup(
        @Body jsonParam: SignupRequestModel
    ): Call<SignupResponseModel>

    @GET(Constants.FEEDS) // 요청 메소드: GET(파라미터들이 URL에 추가됨), baseUrl에 연결될 Endpoint
    @Headers("accept: application/json","charset:utf-8")
    fun getFeedList(
        @Query("company") company: String, // request시 담을 파라미터. 아무것도 없으면 all하고 검색은 파라미터 담기로 했었나...??
        @Query("people-count") people_count : Int,
        @Query("hashtags") hashtag : String,
        @Query("page-number") page_number : Int
    ): Call<FeedList> // Call: 응답이 왔을 때 Callback으로 불려질 타입

    @POST(Constants.CREATE_FEED)
    @Headers("accept: application/json","charset:utf-8")
    fun createFeed(
        @Body jsonParam: CreateFeedRequestModel
    ): Call<String>

    @DELETE(Constants.DELETE_FEED)
    @Headers("accept: application/json","charset:utf-8")
    fun deleteFeed(
        @Query("id") feedId: String
    ): Call<Void>

    @GET(Constants.GET_HASHTAGS)
    @Headers("accept: application/json","charset:utf-8")
    fun getHashtags(): Call<GetHashtagsModel>

    @GET(Constants.GET_COMPANY)
    @Headers("accept: application/json","charset:utf-8")
    fun getCompanyName(): Call<GetCompanyModel>

    companion object {
        fun create(BASE_URL: String): HttpService {
            val gson : Gson = GsonBuilder().create();

            return Retrofit.Builder()   // Retrofit 인스턴스 생성
                .baseUrl(BASE_URL)      // base_url 등록(반드시/로 끝나야함.)
                .addConverterFactory(ScalarsConverterFactory.create())   //컨버터 등록(응답결과를 String자체로 받아서 보여주는 Converter(사용자가 직접 변환시 사용) 그 외에 Moshi / Jackson 등등 여러 컨버터가 존재)
                .addConverterFactory(GsonConverterFactory.create(gson))    //컨버터 등록(JSON 타입의 응답결과를 객체로 매핑(변환)해주는 Converter) Gson converter는 마지막에 넣는게 좋음. 변환이 불가능해도 가능하다고 반응기 때문
                .build()
                .create(HttpService::class.java)        // Retrofit 인스턴스로 인터페이스 객체 구현 - 여기선 create에 base_url만 넣으면 바로 생성까지 해준다.
        }
    }
}