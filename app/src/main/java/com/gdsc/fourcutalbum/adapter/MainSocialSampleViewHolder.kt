package com.gdsc.fourcutalbum.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.gdsc.fourcutalbum.R
import com.gdsc.fourcutalbum.data.model.Feed
import com.gdsc.fourcutalbum.databinding.ListItemFeedBinding
import com.gdsc.fourcutalbum.ui.activity.FeedActivity
import com.google.android.material.chip.Chip

// SocialFragment
class MainSocialSampleViewHolder(private var binding: ListItemFeedBinding, context: Context ) : RecyclerView.ViewHolder(binding.root) {
    private val con = context
    fun bind(data: Feed) {
        binding.root.setOnClickListener {
            val intent = Intent(binding.root.context, FeedActivity::class.java)
            intent.putExtra("data", data)
            binding.root.context.startActivity(intent)
        }


        data.image?.let {
            try{
                if(it.equals("")){
                    Glide.with(binding.root.context).load(R.drawable.ic_baseline_broken_image_24)
                        .into(binding.feedSampleImage)
                }else{
                    Glide.with(binding.root.context).load(it)
                        .override(SIZE_ORIGINAL)
                        .apply(RequestOptions().override(600, 600))
                        .into(binding.feedSampleImage)
                }
            }catch(e:Exception){
                Glide.with(binding.root.context).load(R.drawable.ic_baseline_broken_image_24)
                    .into(binding.feedSampleImage)
            }
        }

        val chipList : Array<Chip> = arrayOf(binding.tag1, binding.tag2, binding.tag3)
        var i = 0
        for (tag: String in data.hashtag!!) {
            chipList[i++].apply {
                text = "# " + tag
                visibility = View.VISIBLE
            }
            Log.d("tag ::: ", chipList[i].toString())
        }

    }

}