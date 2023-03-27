package com.gdsc.fourcutalbum.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.gdsc.fourcutalbum.R
import com.gdsc.fourcutalbum.adapter.HashtagAdapter
import com.gdsc.fourcutalbum.common.Constants
import com.gdsc.fourcutalbum.data.model.Feed
import com.gdsc.fourcutalbum.data.model.FeedDetail
import com.gdsc.fourcutalbum.data.model.FeedList
import com.gdsc.fourcutalbum.databinding.ActivityFeedBinding
import com.gdsc.fourcutalbum.service.HttpService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FeedActivity: AppCompatActivity() {
    lateinit var binding: ActivityFeedBinding
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: HashtagAdapter
    lateinit var viewManager: RecyclerView.LayoutManager

    val data by lazy {
        intent.getSerializableExtra("data") as Feed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setInitialize()
        setServerData()
        setHashtagList()

    }

    fun setServerData(){
        try {
            val dataservice =  HttpService.create(Constants.SERVER_URL).getFeedDetail(data.feed_id)
            Log.d("DBG:RETRO", "SENDED")

            dataservice.enqueue(object : Callback<FeedDetail?> {
                override fun onResponse(call: Call<FeedDetail?>, response: Response<FeedDetail?>) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("DBG:RETRO", "response success: " + response.body().toString())

                        binding.tvComment.text = response.body()?.comment
                        binding.tvNickname.text = response.body()?.nick_name

                    }else{
                        Log.d("DBG:RETRO", "response else: " + response.toString())
                    }
                }
                override fun onFailure(call: Call<FeedDetail?>, t: Throwable) {
                    t.printStackTrace()
                }
            })

        } catch (e:Exception){
            e.printStackTrace()
            Toast.makeText(applicationContext,"서버와 연결이 불안정합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
        }

    }

    fun setHashtagList(){
        viewManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.hashtagList.layoutManager = viewManager
        viewAdapter = HashtagAdapter(data.hashtag.toTypedArray(),
            R.layout.list_item_social_hashtag
        )
        binding.hashtagList.adapter = viewAdapter
    }

    fun setInitialize(){
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        Log.d("DBG::FEED", data.toString())
        Glide.with(binding.root.context).load(data.image)
            .into(binding.feedImage)

        binding.studioText.text = data.company_name

        when(data.people_count){
            1 -> binding.peopleText.text = "혼자서"
            2 -> binding.peopleText.text = "둘이서"
            3 -> binding.peopleText.text = "셋이서"
            4 -> binding.peopleText.text = "넷이서"
            5 -> binding.peopleText.text = "다섯이서"
            6 -> binding.peopleText.text = "6명에서"
            7 -> binding.peopleText.text = "7명에서"
        }


    }

}