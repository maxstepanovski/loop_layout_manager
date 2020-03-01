package com.example.looplayoutmanager

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LoopLayoutManager(
    private val spanCount: Int
) : RecyclerView.LayoutManager() {
    private val viewsToRecycle = mutableListOf<View>()
    private lateinit var recycler: RecyclerView.Recycler
    private lateinit var state: RecyclerView.State
    private var firstStart = true

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        this.recycler = recycler
        this.state = state
        if (firstStart) {
            detachAndScrapAttachedViews(recycler)
            fillToRight(0, 0, recycler)
            firstStart = false
        }
        for (i in 0 until childCount) {
            getChildAt(i)?.let {
                it.findViewById<TextView>(R.id.child_id).text = "$i"
            }
        }
    }

    override fun canScrollHorizontally(): Boolean = true

    override fun canScrollVertically(): Boolean = false

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        offsetChildrenHorizontal(-dx)
        for (i in 0 until childCount) {
            getChildAt(i)?.let {
                it.findViewById<TextView>(R.id.child_id).text = "$i"
            }
        }
        recycleViews()
        if (dx > 0) {
            // Значит элементы уходят налево
            publicFillToRight()
        } else if (dx < 0) {
            // Значит элементы уходят направо
            publicFillToLeft()
        }
        return dx
    }

    fun publicFillToRight() {
        findLastChild()
            ?.takeIf {
                getDecoratedRight(it) < width
            }
            ?.let { view ->
                val fromPosition = getPosition(view).let { position ->
                    (position + 1) % itemCount
                }
                fillToRight(getDecoratedRight(view), fromPosition, recycler)
            }
    }

    fun publicFillToLeft() {
        findFirstChild()
            ?.takeIf {
                getDecoratedLeft(it) > 0
            }
            ?.let { view ->
                val fromPosition = getPosition(view).let { position ->
                    if (position - spanCount >= 0) {
                        position - spanCount
                    } else {
                        position - spanCount + state.itemCount
                    }
                }
                fillToLeft(getDecoratedLeft(view), fromPosition, recycler)
            }
    }

    fun recycleViews() {
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

    private fun fillToRight(currentX: Int, fromPosition: Int, recycler: RecyclerView.Recycler) {
        var accumulatedWidth = currentX
        var accumulatedHeight = 0
        var counter = fromPosition
        while (accumulatedWidth < width) {
            val view = recycler.getViewForPosition(counter % itemCount)
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
            if ((counter - fromPosition) % spanCount == spanCount - 1) {
                accumulatedHeight = 0
                accumulatedWidth += viewWidth
            } else {
                accumulatedHeight += viewHeight
            }
            counter++
        }
    }

    private fun fillToLeft(fromX: Int, fromPosition: Int, recycler: RecyclerView.Recycler) {
        var currentX = fromX
        var accumulatedHeight = 0
        var counter = fromPosition
        while (currentX > 0) {
            val view = recycler.getViewForPosition(counter)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val viewWidth = getDecoratedMeasuredWidth(view)
            val viewHeight = getDecoratedMeasuredHeight(view)
            layoutDecoratedWithMargins(
                view,
                currentX - viewWidth,
                accumulatedHeight,
                currentX,
                accumulatedHeight + viewHeight
            )
            if ((counter - fromPosition) % spanCount == spanCount - 1) {
                accumulatedHeight = 0
                currentX -= viewWidth
                counter = counter - 2 * spanCount + 1
                if (counter < 0) {
                    counter += itemCount
                }
            } else {
                accumulatedHeight += viewHeight
                counter++
            }
        }
    }

    private fun findFirstChild(): View? {
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

    private fun findLastChild(): View? {
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