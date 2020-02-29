package com.example.looplayoutmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val list = mutableListOf<Int>().apply {
        for (i in 0 until 7) {
            this.add(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        with(findViewById<RecyclerView>(R.id.recycler_view)) {
            layoutManager = LoopLayoutManager(3)
            adapter = LoopAdapter().apply { itemList = list }
        }
    }
}