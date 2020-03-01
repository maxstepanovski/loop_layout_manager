package com.example.looplayoutmanager

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val list = mutableListOf<Int>().apply {
        for (i in 0 until 9) {
            this.add(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        with(recyclerView) {
            layoutManager = LoopLayoutManager(3)
            adapter = LoopAdapter().apply { itemList = list }
        }

        with(findViewById<Button>(R.id.right_button)) {
            setOnClickListener {
                (recyclerView.layoutManager as LoopLayoutManager).publicFillToRight()
            }
        }

        with(findViewById<Button>(R.id.left_button)) {
            setOnClickListener {
                (recyclerView.layoutManager as LoopLayoutManager).publicFillToLeft()
            }
        }

        with(findViewById<Button>(R.id.recycle_button)) {
            setOnClickListener {
                (recyclerView.layoutManager as LoopLayoutManager).recycleViews()
            }
        }

        with(findViewById<Button>(R.id.reset_button)) {
            setOnClickListener {
                recyclerView.invalidate()
            }
        }
    }
}