package com.gdsc.fourcutalbum.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.gdsc.fourcutalbum.R
import com.gdsc.fourcutalbum.common.Constants
import com.gdsc.fourcutalbum.data.model.SignupRequestModel
import com.gdsc.fourcutalbum.data.model.SignupResponseModel
import com.gdsc.fourcutalbum.databinding.ActivityMainBinding
import com.gdsc.fourcutalbum.databinding.ActivityRegisterBinding
import com.gdsc.fourcutalbum.service.HttpService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val model = intentToModel()

        binding.btnOk.setOnClickListener {
            if(binding.etNick.text.toString().isNotEmpty()){
                val nick = binding.etNick.text.toString()
                model.nickName = nick
                signupAPI(model)
            }

        }
    }

    private fun intentToModel() : SignupRequestModel{
        val res = SignupRequestModel(intent.getStringExtra("UID").toString(), "", intent.getStringExtra("EMAIL").toString())
        return res
    }

    private fun signupAPI(signupParam: SignupRequestModel){
        try {
            val data =  HttpService.create(Constants.SERVER_URL).getSignup(signupParam)
            Log.d("DBG:RETRO", "SENDED" + signupParam.toString())

            data.enqueue(object : Callback<SignupResponseModel?> {
                override fun onResponse(call: Call<SignupResponseModel?>, response: Response<SignupResponseModel?>) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("DBG:RETRO", "response : " + response.body().toString())

                        if(response.body()!!.message == "SUCCESS"){
                            Toast.makeText(applicationContext, "환영합니다!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                        } else{
                            Toast.makeText(applicationContext, "회원생성을 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }

                    }else{
                        Log.d("DBG:RETRO", "response : " + response.toString())
                    }
                }
                override fun onFailure(call: Call<SignupResponseModel?>, t: Throwable) {
                    t.printStackTrace()
                }
            })

        } catch (e:Exception){
            e.printStackTrace()
            Toast.makeText(applicationContext,"서버와 연결이 불안정합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
        }
    }
}