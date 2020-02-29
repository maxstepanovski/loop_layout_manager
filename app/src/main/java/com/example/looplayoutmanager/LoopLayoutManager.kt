package com.example.looplayoutmanager

import android.util.Log
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
        detachAndScrapAttachedViews(recycler)
        fillToRight(0, 0, recycler, state)
    }

    override fun canScrollHorizontally(): Boolean = true

    override fun canScrollVertically(): Boolean = false

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        Log.d("tag", "$dx")
        offsetChildrenHorizontal(-dx)
        if (dx > 0) {
            // Значит элементы уходят налево
            getChildAt(childCount - 1)
                ?.takeIf {
                    it.right < width
                }
                ?.let { view ->
                    val fromPosition = getPosition(view).let { position ->
                        (position + 1) % itemCount
                    }
                    fillToRight(view.right, fromPosition, recycler, state)
                    recycleToLeft(recycler)
                }
        } else if (dx < 0) {
            // Значит элементы уходят направо

        }
        return dx
    }

    private fun fillToRight(
        currentWidth: Int,
        startFromPosition: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) {
        var accumulatedWidth = currentWidth
        var accumulatedHeight = 0
        var counter = startFromPosition
        while (accumulatedWidth < width) {
            val view = recycler.getViewForPosition(counter % state.itemCount)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val width = getDecoratedMeasuredWidth(view)
            val height = getDecoratedMeasuredHeight(view)
            layoutDecoratedWithMargins(
                view,
                accumulatedWidth,
                accumulatedHeight,
                accumulatedWidth + width,
                accumulatedHeight + height
            )
            if ((counter - startFromPosition) % spanCount == spanCount - 1) {
                accumulatedHeight = 0
                accumulatedWidth += view.width
            } else {
                accumulatedHeight += view.height
            }
            counter++
        }
    }

    private fun fillToLeft(
        endWithPosition: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) {
        getChildAt(0)?.let {

        }
    }

    private fun recycleToLeft(recycler: RecyclerView.Recycler) {
        for (i in 0 until childCount) {
            getChildAt(i)?.let { view ->
                if (view.right < 0) {
                    removeAndRecycleView(view, recycler)
                }
            }
        }
    }

    private fun recycleToRight(recycler: RecyclerView.Recycler) {
        for (i in 0 until childCount) {
            getChildAt(i)?.let { view ->
                if (view.left > width) {
                    removeAndRecycleView(view, recycler)
                }
            }
        }
    }
}