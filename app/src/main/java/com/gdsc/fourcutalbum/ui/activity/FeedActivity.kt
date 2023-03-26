package com.gdsc.fourcutalbum.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gdsc.fourcutalbum.R
import com.gdsc.fourcutalbum.adapter.HashtagAdapter
import com.gdsc.fourcutalbum.data.model.Feed
import com.gdsc.fourcutalbum.databinding.ActivityFeedBinding


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
        setHashtagList()

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