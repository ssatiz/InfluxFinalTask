package com.influx.task.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.view.*
import android.view.ViewTreeObserver.OnPreDrawListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import com.influx.task.R
import com.influx.task.model.DragIconInfo
import com.influx.task.other.DragGridAdapter


import java.util.ArrayList
import java.util.LinkedList

/**
 *
 * 类: CustomBehindView
 *
 *
 * 描述: TODO
 *
 *
 * 作者: wedcel wedcel@gmail.com
 *
 *
 * 时间: 2015年8月25日 下午4:08:40
 *
 *
 */
class CustomBehindView(private val mContext: Context, private val mCustomGroup: CustomGroup) : GridView(mContext) {
    /*** DragGridView的item长按响应的时间， 默认是1000毫秒，也可以自行设置  */
    private var dragResponseMS: Long = 100
    /** 是否可以拖拽，默认不可以  */
    private var isDrag = false

    private var mDownX: Int = 0
    private var mDownY: Int = 0
    private var moveX: Int = 0
    private var moveY: Int = 0
    /** 正在拖拽的position  */
    private var mDragPosition: Int = 0
    /*** 刚开始拖拽的item对应的View  */
    private var mStartDragItemView: View? = null
    /** 用于拖拽的镜像，这里直接用一个ImageView  */
    private var mDragImageView: ImageView? = null
    private val mWindowManager: WindowManager
    /** item镜像的布局参数  */
    private var mWindowLayoutParams: WindowManager.LayoutParams? = null
    /** 我们拖拽的item对应的Bitmap  */
    private var mDragBitmap: Bitmap? = null
    /** 按下的点到所在item的上边缘的距离  */
    private var mPoint2ItemTop: Int = 0
    /** 按下的点到所在item的左边缘的距离  */
    private var mPoint2ItemLeft: Int = 0
    /** DragGridView距离屏幕顶部的偏移量  */
    private var mOffset2Top: Int = 0
    /** DragGridView距离屏幕左边的偏移量  */
    private var mOffset2Left: Int = 0
    /** 状态栏的高度  */
    private val mStatusHeight: Int
    /** DragGridView自动向下滚动的边界值  */
    private var mDownScrollBorder: Int = 0
    /** DragGridView自动向上滚动的边界值  */
    private var mUpScrollBorder: Int = 0

    private var mAnimationEnd = true

    private var mNumColumns = GridView.AUTO_FIT
    private var mDragAdapter: DragGridAdapter? = null
    /**
     *
     * 方法: getEditList
     *
     *
     * 描述: TODO
     *
     *
     * 参数: @return
     *
     *
     * 返回: ArrayList<DragIconInfo> </DragIconInfo>
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午7:00:24
     */
    val editList = ArrayList<DragIconInfo>()

    private val mHandler = Handler()

    private val mLongClickRunnable = Runnable {
        isDrag = true
        mStartDragItemView!!.visibility = View.INVISIBLE
        createDragImage(mDragBitmap, mDownX, mDownY)
    }

    private var isFirstLongDrag: Boolean = false
    private var hasFirstCalculate = false

    /**
     * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动 当moveY的值小于向下滚动的边界值，触发GridView自动向下滚动
     * 否则不进行滚动
     */
    @SuppressLint("NewApi")
    private val mScrollRunnable = object : Runnable {

        override fun run() {
            val scrollY: Int
            if (firstVisiblePosition == 0 || lastVisiblePosition == count - 1) {
                mHandler.removeCallbacks(this)
            }

            if (moveY > mUpScrollBorder) {
                scrollY = speed
                mHandler.postDelayed(this, 25)
            } else if (moveY < mDownScrollBorder) {
                scrollY = -speed
                mHandler.postDelayed(this, 25)
            } else {
                scrollY = 0
                mHandler.removeCallbacks(this)
            }
            smoothScrollBy(scrollY, 10)
        }
    }
    private var parentView: CustomBehindParent? = null

    /**
     *
     * 方法: isModifyedOrder
     *
     *
     * 描述: 是否修改
     *
     *
     * 参数: @return
     *
     *
     * 返回: boolean
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:35:20
     */
    val isModifyedOrder: Boolean
        get() = mDragAdapter!!.isHasModifyedOrder

    init {
        this.numColumns = CustomGroup.COLUMNUM
        mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mStatusHeight = getStatusHeight(mContext) // 获取状态栏的高度
    }

    override fun setAdapter(adapter: ListAdapter) {
        super.setAdapter(adapter)

        if (adapter is DragGridAdapter) {
            mDragAdapter = adapter
        } else {
            throw IllegalStateException("the adapter must be implements DragGridAdapter")
        }
    }

    override fun setNumColumns(numColumns: Int) {
        super.setNumColumns(numColumns)
        this.mNumColumns = numColumns
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }

    /**
     * 设置响应拖拽的毫秒数，默认是1000毫秒
     *
     * @param dragResponseMS
     */
    fun setDragResponseMS(dragResponseMS: Long) {
        this.dragResponseMS = dragResponseMS
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = ev.x.toInt()
                mDownY = ev.y.toInt()
                val tempPosit = pointToPosition(mDownX, mDownY)

                if (tempPosit == AdapterView.INVALID_POSITION) {
                    return true
                }
                if (mCustomGroup.isEditModel && tempPosit != mDragPosition) {
                    mCustomGroup.setEditModel(false, 0)
                    return true
                }
                mHandler.postDelayed(mLongClickRunnable, dragResponseMS)
                mStartDragItemView = getChildAt(mDragPosition - firstVisiblePosition)
                mPoint2ItemTop = mDownY - mStartDragItemView!!.top
                mPoint2ItemLeft = mDownX - mStartDragItemView!!.left
                mOffset2Top = (ev.rawY - mDownY).toInt()
                mOffset2Left = (ev.rawX - mDownX).toInt()


                mDownScrollBorder = height / 5
                mUpScrollBorder = height * 4 / 5

                mStartDragItemView!!.isDrawingCacheEnabled = true
                mDragBitmap = Bitmap.createBitmap(mStartDragItemView!!.drawingCache)
                mStartDragItemView!!.destroyDrawingCache()
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = ev.x.toInt()
                val moveY = ev.y.toInt()
                if (isFirstLongDrag && !hasFirstCalculate) {
                    mStartDragItemView = getChildAt(mDragPosition - firstVisiblePosition)

                    mPoint2ItemTop = moveY - mStartDragItemView!!.top
                    mPoint2ItemLeft = moveX - mStartDragItemView!!.left

                    mOffset2Top = (ev.rawY - moveY).toInt()
                    mOffset2Left = (ev.rawX - moveX).toInt()
                    hasFirstCalculate = true
                }

                if (!isTouchInItem(mStartDragItemView, moveX, moveY)) {
                    mHandler.removeCallbacks(mLongClickRunnable)
                }
            }
            MotionEvent.ACTION_UP -> {
                mHandler.removeCallbacks(mLongClickRunnable)
                mHandler.removeCallbacks(mScrollRunnable)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun drawWindowView(position: Int, event: MotionEvent) {
        mHandler.postDelayed({
            mDragPosition = position
            if (mDragPosition != AdapterView.INVALID_POSITION) {
                isFirstLongDrag = true
                mDragAdapter!!.setModifyPosition(mDragPosition)
                mDownX = event.x.toInt()
                mDownY = event.y.toInt()
                mStartDragItemView = getChildAt(mDragPosition - firstVisiblePosition)
                createFirstDragImage()
            }
        }, 100)

    }

    private fun createFirstDragImage() {
        removeDragImage()
        isDrag = true
        val ivDelet = mStartDragItemView!!.findViewById<View>(R.id.delet_iv) as ImageView
        val llContainer = mStartDragItemView!!.findViewById<View>(R.id.edit_ll) as LinearLayout
        if (ivDelet != null) {
            ivDelet.visibility = View.VISIBLE
        }
        llContainer.setBackgroundColor(mContext.resources.getColor(R.color.item_bg))
        mStartDragItemView!!.isDrawingCacheEnabled = true
        mDragBitmap = Bitmap.createBitmap(mStartDragItemView!!.drawingCache)
        mStartDragItemView!!.destroyDrawingCache()
        llContainer.setBackgroundColor(mContext.resources.getColor(R.color.white))
        mWindowLayoutParams = WindowManager.LayoutParams()
        mWindowLayoutParams!!.format = 1

        mWindowLayoutParams!!.gravity = Gravity.TOP or Gravity.LEFT
        val location = IntArray(2)
        mStartDragItemView!!.getLocationOnScreen(location)
        mWindowLayoutParams!!.x = location[0]// (x-mLastX-xtox)+dragItemView.getLeft()+8;
        mWindowLayoutParams!!.y = location[1] - mStatusHeight
        mWindowLayoutParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        mWindowLayoutParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        mWindowLayoutParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        mDragImageView = ImageView(context)
        mDragImageView!!.setBackgroundColor(mContext.resources.getColor(R.color.item_bg))
        mDragImageView!!.setImageBitmap(mDragBitmap)
        mWindowManager.addView(mDragImageView, mWindowLayoutParams)
        mStartDragItemView!!.visibility = View.INVISIBLE// 隐藏该item
    }

    private fun isTouchInItem(dragView: View?, x: Int, y: Int): Boolean {
        if (dragView == null) {
            return false
        }
        val leftOffset = dragView.left
        val topOffset = dragView.top
        if (x < leftOffset || x > leftOffset + dragView.width) {
            return false
        }

        return !(y < topOffset || y > topOffset + dragView.height)

    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (isDrag && mDragImageView != null) {
            when (ev.action) {
                MotionEvent.ACTION_MOVE -> {
                    //				LogUtil.d("CustomBehindView onTouchEvent", "ACTION_MOVE");
                    moveX = ev.x.toInt()
                    moveY = ev.y.toInt()

                    // 拖动item
                    onDragItem(moveX, moveY)
                }
                MotionEvent.ACTION_UP -> {
                    val dropx = ev.x.toInt()
                    val dropy = ev.y.toInt()
                    onStopDrag(dropx, dropy)
                    isDrag = false
                    isFirstLongDrag = false
                    hasFirstCalculate = false
                }
            }
            return true
        }
        return super.onTouchEvent(ev)
    }

    /**
     *
     * 方法: cancleEditModel
     *
     *
     * 描述: 是否修改了
     *
     *
     * 参数:
     *
     *
     * 返回: void
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:19:25
     */
    fun cancleEditModel() {
        removeDragImage()
        mCustomGroup.setEditModel(false, 0)
    }

    /**、
     *
     * 方法: createDragImage
     *
     *
     * 描述: TODO
     *
     *
     * 参数: @param bitmap
     * 参数: @param downX  按下的点相对父控件的X坐标
     * 参数: @param downY  按下的点相对父控件的Y坐标
     *
     *
     * 返回: void
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:19:39
     */
    private fun createDragImage(bitmap: Bitmap?, downX: Int, downY: Int) {
        mWindowLayoutParams = WindowManager.LayoutParams()
        mWindowLayoutParams!!.format = 1
        mWindowLayoutParams!!.gravity = Gravity.TOP or Gravity.LEFT
        mWindowLayoutParams!!.x = downX - mPoint2ItemLeft + mOffset2Left
        mWindowLayoutParams!!.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight
        mWindowLayoutParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        mWindowLayoutParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        mWindowLayoutParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

        mDragImageView = ImageView(context)
        mDragImageView!!.setBackgroundColor(mContext.resources.getColor(R.color.item_bg))
        mDragImageView!!.setImageBitmap(bitmap)
        mWindowManager.addView(mDragImageView, mWindowLayoutParams)
    }

    /**
     *
     * 方法: removeDragImage
     *
     *
     * 描述:  从界面上面移除拖动镜像
     *
     *
     * 参数:
     *
     *
     * 返回: void
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:19:52
     */
    private fun removeDragImage() {
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView)
            mDragImageView = null
        }
    }

    /**
     *
     * 方法: onDragItem
     *
     *
     * 描述:  拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
     *
     *
     * 参数: @param moveX
     * 参数: @param moveY
     *
     *
     * 返回: void
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:20:08
     */
    private fun onDragItem(moveX: Int, moveY: Int) {
        mWindowLayoutParams!!.x = moveX - mPoint2ItemLeft + mOffset2Left
        mWindowLayoutParams!!.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight
        mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams) // 更新镜像的位置
        onSwapItem(moveX, moveY)

        // GridView自动滚动
        mHandler.post(mScrollRunnable)
    }

    /**
     * 交换item,并且控制item之间的显示与隐藏效果
     *
     * @param moveX
     * @param moveY
     */
    private fun onSwapItem(moveX: Int, moveY: Int) {
        // 获取我们手指移动到的那个item的position
        val tempPosition = pointToPosition(moveX, moveY)

        // 假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
        if (tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION && mAnimationEnd) {
            if (tempPosition != editList.size - 1) {
                mDragAdapter!!.reorderItems(mDragPosition, tempPosition)
                mDragAdapter!!.setHideItem(tempPosition)

                val observer = viewTreeObserver
                observer.addOnPreDrawListener(object : OnPreDrawListener {

                    override fun onPreDraw(): Boolean {
                        observer.removeOnPreDrawListener(this)
                        animateReorder(mDragPosition, tempPosition)
                        mDragPosition = tempPosition
                        return true
                    }
                })
            }
        }
    }


    private fun createTranslationAnimations(
        view: View,
        startX: Float,
        endX: Float,
        startY: Float,
        endY: Float
    ): AnimatorSet {
        val animX = ObjectAnimator.ofFloat(view, "translationX", startX, endX)
        val animY = ObjectAnimator.ofFloat(view, "translationY", startY, endY)
        val animSetXY = AnimatorSet()
        animSetXY.playTogether(animX, animY)
        return animSetXY
    }


    private fun animateReorder(oldPosition: Int, newPosition: Int) {
        val isForward = newPosition > oldPosition
        val resultList = LinkedList<Animator>()
        if (isForward) {
            for (pos in oldPosition until newPosition) {
                val view = getChildAt(pos - firstVisiblePosition)
                println(pos)

                if ((pos + 1) % mNumColumns == 0) {
                    resultList.add(
                        createTranslationAnimations(
                            view,
                            (-view.width * (mNumColumns - 1)).toFloat(),
                            0f,
                            view.height.toFloat(),
                            0f
                        )
                    )
                } else {
                    resultList.add(createTranslationAnimations(view, view.width.toFloat(), 0f, 0f, 0f))
                }
            }
        } else {
            for (pos in oldPosition downTo newPosition + 1) {
                val view = getChildAt(pos - firstVisiblePosition)
                if ((pos + mNumColumns) % mNumColumns == 0) {
                    resultList.add(
                        createTranslationAnimations(
                            view,
                            (view.width * (mNumColumns - 1)).toFloat(),
                            0f,
                            (-view.height).toFloat(),
                            0f
                        )
                    )
                } else {
                    resultList.add(createTranslationAnimations(view, (-view.width).toFloat(), 0f, 0f, 0f))
                }
            }
        }

        val resultSet = AnimatorSet()
        resultSet.playTogether(resultList)
        resultSet.duration = 300
        resultSet.interpolator = AccelerateDecelerateInterpolator()
        resultSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                mAnimationEnd = false
            }

            override fun onAnimationEnd(animation: Animator) {
                mAnimationEnd = true
            }
        })
        resultSet.start()
    }

    /**
     *
     * 方法: onStopDrag
     *
     *
     * 描述: 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
     *
     *
     * 参数: @param dropx
     * 参数: @param dropy
     *
     *
     * 返回: void
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:20:35
     */
    private fun onStopDrag(dropx: Int, dropy: Int) {

        val view = getChildAt(mDragPosition - firstVisiblePosition)

        if (view != null) {
            view.visibility = View.VISIBLE
        }
        mDragAdapter!!.setHideItem(-1)
        removeDragImage()
    }

    /**
     *
     * 方法: getStatusHeight
     *
     *
     * 描述: 得到标题栏高度
     *
     *
     * 参数: @param context
     * 参数: @return
     *
     *
     * 返回: int
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:20:46
     */
    private fun getStatusHeight(context: Context): Int {
        var statusHeight = 0
        val localRect = Rect()
        (context as Activity).window.decorView.getWindowVisibleDisplayFrame(localRect)
        statusHeight = localRect.top
        if (0 == statusHeight) {
            val localClass: Class<*>
            try {
                localClass = Class.forName("com.android.internal.R\$dimen")
                val localObject = localClass.newInstance()
                val i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString())
                statusHeight = context.getResources().getDimensionPixelSize(i5)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return statusHeight
    }

    /**
     *
     * 方法: refreshIconInfoList
     *
     *
     * 描述: TODO
     *
     *
     * 参数: @param iconInfoList
     *
     *
     * 返回: void
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午7:00:14
     */
    fun refreshIconInfoList(iconInfoList: ArrayList<DragIconInfo>) {
        editList.clear()
        editList.addAll(iconInfoList)
        mDragAdapter = DragGridAdapter(mContext, editList, this)
        this.adapter = mDragAdapter!!
        mDragAdapter!!.notifyDataSetChanged()
    }

    /**
     *
     * 方法: notifyDataSetChange
     *
     *
     * 描述: 刷新数据
     *
     *
     * 参数: @param iconInfoList
     *
     *
     * 返回: void
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午7:00:42
     */
    fun notifyDataSetChange(iconInfoList: ArrayList<DragIconInfo>) {
        editList.clear()
        editList.addAll(iconInfoList)
        mDragAdapter!!.resetModifyPosition()
        mDragAdapter!!.notifyDataSetChanged()
    }

    /**
     *
     * 方法: deletInfo
     *
     *
     * 描述: 删除
     *
     *
     * 参数: @param position
     * 参数: @param iconInfo
     *
     *
     * 返回: void
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午6:56:47
     */
    fun deletInfo(position: Int, iconInfo: DragIconInfo) {
        deletAnimation(position)
        mCustomGroup.deletHomePageInfo(iconInfo)
    }

    /**
     *
     * 方法: deletAnimation
     *
     *
     * 描述: 删除动画
     *
     *
     * 参数: @param position
     *
     *
     * 返回: void
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:21:37
     */
    private fun deletAnimation(position: Int) {
        val view = getChildAt(position)
        view.isDrawingCacheEnabled = true
        val mDragBitmap = Bitmap.createBitmap(view.drawingCache)
        view.destroyDrawingCache()
        val animView = ImageView(mContext)
        animView.setImageBitmap(mDragBitmap)
        val ivlp =
            AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT)
        parentView!!.addView(animView, ivlp)
        val aimPosit = editList.size - 1

        val animatorSet = createTranslationAnim(position, aimPosit, view, animView)
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.duration = 500
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                view.visibility = View.INVISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
                animView.visibility = View.GONE
                animView.clearAnimation()
                parentView!!.removeView(animView)
                mDragAdapter!!.reorderItems(position, aimPosit)
                mDragAdapter!!.deleteItem(aimPosit)
                //mDragAdapter.setHideItem(aimPosit);

                val observer = viewTreeObserver
                observer.addOnPreDrawListener(object : OnPreDrawListener {

                    override fun onPreDraw(): Boolean {
                        observer.removeOnPreDrawListener(this)
                        animateReorder(position, aimPosit)
                        return true
                    }
                })
            }
        })
        animatorSet.start()
    }


    /**
     *
     * 方法: createTranslationAnim
     *
     *
     * 描述: TODO
     *
     *
     * 参数: @param position
     * 参数: @param aimPosit
     * 参数: @param view
     * 参数: @param animView
     * 参数: @return
     *
     *
     * 返回: AnimatorSet
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:49:23
     */
    private fun createTranslationAnim(position: Int, aimPosit: Int, view: View, animView: ImageView): AnimatorSet {
        val startx = view.left
        val starty = view.top
        val aimView = getChildAt(aimPosit)
        val endx = aimView.left
        val endy = aimView.top

        val animX = ObjectAnimator.ofFloat(animView, "translationX", startx.toFloat(), endx.toFloat())
        val animY = ObjectAnimator.ofFloat(animView, "translationY", starty.toFloat(), endy.toFloat())
        val scaleX = ObjectAnimator.ofFloat(animView, "scaleX", 1f, 0.5f)
        val scaleY = ObjectAnimator.ofFloat(animView, "scaleY", 1f, 0.5f)
        val alpaAnim = ObjectAnimator.ofFloat(animView, "alpha", 1f, 0.0f)

        val animSetXY = AnimatorSet()
        animSetXY.playTogether(animX, animY, scaleX, scaleY, alpaAnim)
        return animSetXY
    }

    fun setDeletAnimView(customBehindParent: CustomBehindParent) {
        this.parentView = customBehindParent

    }

    /**
     *
     * 方法: cancleModifyedOrderState
     *
     *
     * 描述: TODO
     *
     *
     * 参数:
     *
     *
     * 返回: void
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:35:10
     */
    fun cancleModifyedOrderState() {
        mDragAdapter!!.isHasModifyedOrder = false
    }

    /**
     *
     * 方法: resetHidePosition
     *
     *
     * 描述: TODO
     *
     *
     * 参数:
     *
     *
     * 返回: void
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:35:05
     */
    fun resetHidePosition() {
        mDragAdapter!!.setHideItem(-1)
    }

    /**
     *
     * 方法: isValideEvent
     *
     *
     * 描述: 标记是否是在这个view里面的点击事件 防止事件冲突
     *
     *
     * 参数: @param ev
     * 参数: @param scrolly
     * 参数: @return
     *
     *
     * 返回: boolean
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:34:01
     */
    fun isValideEvent(ev: MotionEvent, scrolly: Int): Boolean {
        val left = (parent.parent as View).left
        val top = (parent.parent as View).top
        val x_ = ev.x.toInt()
        val y_ = ev.y.toInt()
        val tempx = x_ - left
        val tempy = y_ - top + scrolly
        val position = pointToPosition(tempx, tempy)
        val rect = Rect()
        getHitRect(rect)
        return position != AdapterView.INVALID_POSITION
    }

    /**
     *
     * 方法: clearDragView
     *
     *
     * 描述: 清除拖动
     *
     *
     * 参数:
     *
     *
     * 返回: void
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午4:28:13
     */
    fun clearDragView() {
        removeDragImage()
    }

    companion object {
        /** DragGridView自动滚动的速度  */
        private val speed = 20
    }
}
