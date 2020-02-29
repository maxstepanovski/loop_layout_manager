package com.example.looplayoutmanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LoopAdapter : RecyclerView.Adapter<LoopViewHolder>() {
    var itemList = mutableListOf<Int>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoopViewHolder =
        LayoutInflater.from(parent.context)
            .let {
                it.inflate(R.layout.view_holder, parent, false)
            }.let {
                LoopViewHolder(it)
            }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: LoopViewHolder, position: Int) {
        holder.bind(itemList[position])
    }
}