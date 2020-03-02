package com.example.looplayoutmanager

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LoopLayoutManager(
    private val spanCount: Int,
    private val orientation: Int
) : RecyclerView.LayoutManager() {
    private val viewsToRecycle = mutableListOf<View>()
    private var firstStart = true

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (firstStart) {
            detachAndScrapAttachedViews(recycler)
            fillRight(recycler, state, 0, 0)
            firstStart = false
        }
    }

    override fun canScrollHorizontally(): Boolean = orientation == RecyclerView.HORIZONTAL

    override fun canScrollVertically(): Boolean = orientation == RecyclerView.VERTICAL

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        offsetChildrenHorizontal(-dx)
        if (dx > 0) {
            // Значит элементы уходят налево
            findBottomRightChild()
                ?.takeIf {
                    getDecoratedRight(it) < width
                }
                ?.let {
                    val fromPosition = getPosition(it).let { position ->
                        (position + 1) % itemCount
                    }
                    fillRight(recycler, state, fromPosition, getDecoratedRight(it))
                }
        } else if (dx < 0) {
            // Значит элементы уходят направо
            findBottomLeftChild()
                ?.takeIf {
                    getDecoratedLeft(it) > 0
                }
                ?.let {
                    val fromPosition = (getPosition(it) - 1).let { position ->
                        if (position < 0) {
                            state.itemCount - 1
                        } else {
                            position
                        }
                    }
                    fillLeft(
                        recycler,
                        state,
                        fromPosition,
                        getDecoratedLeft(it),
                        (spanCount - 1) * getDecoratedMeasuredHeight(it)
                    )
                }
        }
        recycleViews(recycler)
        return dx
    }

    private fun fillRight(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        startPosition: Int,
        startX: Int
    ) {
        var accumulatedWidth = startX
        var accumulatedHeight = 0
        var counter = startPosition
        while (accumulatedWidth < width) {
            val view = recycler.getViewForPosition(counter % state.itemCount)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val viewWidth = getDecoratedMeasuredWidth(view)
            val viewHeight = getDecoratedMeasuredHeight(view)
            layoutDecoratedWithMargins(
                view,
                accumulatedWidth,
                accumulatedHeight,
                accumulatedWidth + viewWidth,
                accumulatedHeight + viewHeight
            )
            if (accumulatedHeight == (spanCount - 1) * viewHeight) {
                accumulatedHeight = 0
                accumulatedWidth += viewWidth
            } else {
                accumulatedHeight += viewHeight
            }
            counter++
        }
    }

    private fun fillLeft(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        startPosition: Int,
        startX: Int,
        startY: Int
    ) {
        var currentX = startX
        var currentY = startY
        var counter = startPosition
        while (currentX > 0) {
            val view = recycler.getViewForPosition(counter)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val viewWidth = getDecoratedMeasuredWidth(view)
            val viewHeight = getDecoratedMeasuredHeight(view)
            layoutDecoratedWithMargins(
                view,
                currentX - viewWidth,
                currentY,
                currentX,
                currentY + viewHeight
            )
            if (currentY == 0) {
                currentY = startY
                currentX -= viewWidth
            } else {
                currentY -= viewHeight
            }
            counter--
            if (counter < 0) {
                counter = state.itemCount - 1
            }
        }
    }

    private fun recycleViews(recycler: RecyclerView.Recycler) {
        val screenWidth = width
        for (i in 0 until childCount) {
            getChildAt(i)?.let { view ->
                val right = getDecoratedRight(view)
                val left = getDecoratedLeft(view)
                if (right < 0) {
                    viewsToRecycle.add(view)
                }
                if (left > screenWidth) {
                    viewsToRecycle.add(view)
                }
            }
        }
        for (view in viewsToRecycle) {
            detachAndScrapView(view, recycler)
        }
        viewsToRecycle.clear()
    }

    private fun findBottomLeftChild(): View? {
        var minTopLeftSum = width + height
        var result: View? = null
        for (i in 0 until childCount) {
            getChildAt(i)?.let { view ->
                val topLeft = getDecoratedTop(view) + getDecoratedLeft(view)
                if (topLeft < minTopLeftSum) {
                    minTopLeftSum = topLeft
                    result = view
                }
            }
        }
        return result
    }

    private fun findBottomRightChild(): View? {
        var maxBottomRight = 0
        var result: View? = null
        for (i in 0 until childCount) {
            getChildAt(i)?.let { view ->
                val bottomRight = getDecoratedRight(view) + getDecoratedBottom(view)
                if (bottomRight > maxBottomRight) {
                    maxBottomRight = bottomRight
                    result = view
                }
            }
        }
        return result
    }
}