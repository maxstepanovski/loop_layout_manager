package com.example.looplayoutmanager

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LoopViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.title_id)

    fun bind(index: Int) {
        title.text = index.toString()
    }
}