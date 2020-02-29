package com.example.looplayoutmanager

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LoopLayoutManager(
    private val spanCount: Int
) : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        var accumulatedWidth = 0
        var accumulatedHeight = 0
        var counter = 0
        while (accumulatedWidth < width) {
            val view = recycler.getViewForPosition(counter % state.itemCount)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            layoutDecoratedWithMargins(
                view,
                accumulatedWidth,
                accumulatedHeight,
                accumulatedWidth + view.width,
                accumulatedHeight + view.height
            )
            if (counter % spanCount < spanCount - 1) {
                accumulatedHeight += view.height
            } else {
                accumulatedHeight = 0
                accumulatedWidth += view.width
            }
            counter++
        }
    }

    override fun canScrollHorizontally(): Boolean = true

    override fun canScrollVertically(): Boolean = false
}