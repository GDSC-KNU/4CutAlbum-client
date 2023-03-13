package com.gdsc.fourcutalbum.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gdsc.fourcutalbum.R

class HashtagAdapter(val itemList: Array<String>, layoutId: Int): RecyclerView.Adapter<HashtagAdapter.ViewHolder>() {

    private var layout : Int = layoutId

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var hashtag: TextView = itemView.findViewById(R.id.itemHashtag)
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): HashtagAdapter.ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.hashtag.text = holder.hashtag.text.toString() + itemList[position]
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}