package com.gdsc.fourcutalbum.service

import com.gdsc.fourcutalbum.common.Constants
import com.gdsc.fourcutalbum.data.model.SignupRequestModel
import com.gdsc.fourcutalbum.data.model.SignupResponseModel
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

    @POST(Constants.API_SIGNUP)
    @Headers("accept: application/json","charset:utf-8")
    fun getSignup(
        @Body jsonParam: SignupRequestModel
    ): Call<SignupResponseModel>


    companion object {
        fun create(BASE_URL: String): HttpService {
            val gson : Gson = GsonBuilder().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(HttpService::class.java)
        }
    }
}