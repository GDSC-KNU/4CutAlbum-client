package com.gdsc.fourcutalbum.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gdsc.fourcutalbum.R
import com.gdsc.fourcutalbum.adapter.HashtagAdapter
import com.gdsc.fourcutalbum.databinding.ActivityFeedBinding


class FeedActivity: AppCompatActivity() {
    lateinit var binding: ActivityFeedBinding
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: HashtagAdapter
    lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setInitialize()
        setHashtagList()

    }

    fun setHashtagList(){
        viewManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.hashtagList.layoutManager = viewManager
        viewAdapter = HashtagAdapter(resources.getStringArray(R.array.hashtag),
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

    }

}