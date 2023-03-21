package com.gdsc.fourcutalbum.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gdsc.fourcutalbum.data.model.Feed
import com.gdsc.fourcutalbum.databinding.ListItemFeedBinding

// SocialFragment
class MainSocialSampleAdapter : RecyclerView.Adapter<MainSocialSampleViewHolder>() {
    private var datas: ArrayList<Feed>? = null

    init {
        datas = ArrayList()
    }

    fun setListInit(_lstBoardInfo : ArrayList<Feed>) {
        if (datas?.isNotEmpty() == true)
            datas?.clear()
        datas?.addAll(_lstBoardInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainSocialSampleViewHolder {
        return MainSocialSampleViewHolder(ListItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: MainSocialSampleViewHolder, position: Int) {
        holder.bind(datas!![position])
    }

    override fun getItemCount(): Int {
        Log.d("rv data size: ", datas?.size.toString())
        return datas?.size ?: 0
    }

}