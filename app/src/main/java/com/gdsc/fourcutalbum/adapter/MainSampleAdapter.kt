package com.gdsc.fourcutalbum.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gdsc.fourcutalbum.data.model.FourCuts
import com.gdsc.fourcutalbum.databinding.ListItemMainBinding


class MainSampleAdapter : RecyclerView.Adapter<MainSampleViewHolder>() {
    private var datas: ArrayList<FourCuts>? = null

    init {
        datas = ArrayList()
    }

    fun setListInit(_lstBoardInfo : List<FourCuts>) {
        if (datas?.isNotEmpty() == true)
            datas?.clear()
        datas?.addAll(_lstBoardInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainSampleViewHolder {
        return MainSampleViewHolder(ListItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainSampleViewHolder, position: Int) {
        holder.bind(datas!![position])
    }

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }
}