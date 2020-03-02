package com.example.looplayoutmanager

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val horizontalMargin = 40
    private val verticalMargin = 40

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val lm = parent.layoutManager as GridLayoutManager
        if (lm.spanCount == 1) {
            listOffsets(outRect, position, parent)
        } else {
            gridOffsets(position, outRect)
        }
    }

    private fun listOffsets(
        outRect: Rect,
        position: Int,
        parent: RecyclerView
    ) {
        outRect.top = verticalMargin
        outRect.bottom = verticalMargin
        when (position) {
            0 -> {
                outRect.left = horizontalMargin
                outRect.right = horizontalMargin / 2
            }
            parent.adapter!!.itemCount - 1 -> {
                outRect.left = horizontalMargin / 2
                outRect.right = horizontalMargin
            }
            else -> {
                outRect.left = horizontalMargin / 2
                outRect.right = horizontalMargin / 2
            }
        }
    }

    private fun gridOffsets(position: Int, outRect: Rect) {
        if (position % 2 == 0) {
            outRect.right = horizontalMargin
        } else {
            outRect.left = horizontalMargin
        }
        outRect.top = verticalMargin
        outRect.bottom = verticalMargin
    }
}