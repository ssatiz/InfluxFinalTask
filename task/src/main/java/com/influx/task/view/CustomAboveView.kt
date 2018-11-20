package com.influx.task.view


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.influx.task.R
import com.influx.task.model.DragIconInfo
import java.util.*

/**
 *
 * 类: CustomAboveView
 *
 *
 * 描述: TODO
 *
 *
 * 作者: wedcel wedcel@gmail.com
 *
 *
 * 时间: 2015年8月25日 下午7:01:18
 *
 *
 */
@SuppressLint("ViewConstructor")
class CustomAboveView(private val mContext: Context, private val mCustomGroup: CustomGroup) :
    LinearLayout(mContext, null) {

    var iconInfoList = ArrayList<DragIconInfo>()
    private var mItemViewClickListener: ItemViewClickListener? = null
    private val verticalViewWidth = 1
    var gridViewClickListener: CustomAboveViewClickListener? = null
    var firstEvent: MotionEvent? = null
   // private lateinit var gridViewNoScroll: CustomGridView1

    private var mChildClickListener: CustomGridView1.CustomChildClickListener? = null

    interface CustomAboveViewClickListener {
        /**
         *
         * 方法: onSingleClicked
         *
         *
         * 描述: TODO
         *
         *
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
         * 时间: 2015年8月25日 下午5:30:13
         */
        fun onSingleClicked(iconInfo: DragIconInfo)

        /**
         *
         * 方法: onChildClicked
         *
         *
         * 描述: TODO
         *
         *
         * 参数: @param childInfo
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
         * 时间: 2015年8月25日 下午5:30:10
         */
        fun onChildClicked()
    }


    init {
        orientation = LinearLayout.VERTICAL
        initData()
    }

    /**
     *
     * 方法: initData
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
     * 时间: 2015年8月25日 下午7:02:12
     */
    private fun initData() {
        mChildClickListener = object : CustomGridView1.CustomChildClickListener {
            override fun onPreviewClicked() {

                return if (gridViewClickListener == null) {
                } else {
                    gridViewClickListener!!.onChildClicked()
                }

            }

            override fun onBuyTicketClicked() {
                return if (gridViewClickListener == null) {
                } else {
                    gridViewClickListener!!.onChildClicked()
                }
            }


//            override fun onChildClicked(chilidInfo: DargChildInfo) {
//                return if (gridViewClickListener == null) {
//                } else {
//                    gridViewClickListener!!.onChildClicked(chilidInfo)
//                }
//            }
        }
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
     * 时间: 2015年8月25日 下午6:46:22
     */
    fun refreshIconInfoList(iconInfoList: ArrayList<DragIconInfo>) {
        this.iconInfoList.clear()
        this.iconInfoList.addAll(iconInfoList)
        refreshViewUI()
    }

    /**
     *
     * 方法: refreshViewUI
     *
     *
     * 描述:  刷新UI
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
     * 时间: 2015年8月25日 下午7:02:17
     */



    private fun refreshViewUI() {
        removeAllViews()
        val rowNum =
            iconInfoList.size / CustomGroup.COLUMNUM + if (iconInfoList.size % CustomGroup.COLUMNUM > 0) 1 else 0
        val rowParam =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val verticalParams = LinearLayout.LayoutParams(verticalViewWidth, LinearLayout.LayoutParams.FILL_PARENT)
        val horizontalParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, verticalViewWidth)
        for (rowIndex in 0 until rowNum) {
            val rowView = View.inflate(mContext, R.layout.gridview_above_rowview, null)

            val llRowContainer = rowView.findViewById<View>(R.id.gridview_rowcontainer_ll) as LinearLayout
            val ivOpenFlag = rowView.findViewById<View>(R.id.gridview_rowopenflag_iv) as ImageView
            val llBtm = rowView.findViewById<View>(R.id.gridview_rowbtm_ll) as LinearLayout
            val gridViewNoScroll = rowView.findViewById<View>(R.id.gridview_child_gridview) as CustomGridView1
            if (mChildClickListener != null) {
                gridViewNoScroll.childClickListener = mChildClickListener
            }
            gridViewNoScroll.setParentView(llBtm)
            val itemParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            itemParam.weight = 1.0f
            val itemClickLitener = ItemViewClickListener(llBtm, ivOpenFlag, object : ItemViewClickInterface {

                override fun shoudInteruptViewAnimtion(listener: ItemViewClickListener, position: Int): Boolean {
                    var isInterupt = false
                    mCustomGroup.clearEditDragView()
                    if (mItemViewClickListener != null && mItemViewClickListener != listener) {
                        mItemViewClickListener!!.closeExpandView()
                    }
                    mItemViewClickListener = listener
                    val iconInfo = iconInfoList[position]
                    val childList = iconInfo.childList
                    if (childList.size > 0) {
                        gridViewNoScroll.refreshDataSet(childList)
                    } else {
                        setViewCollaps()
                        isInterupt = true
                        if (gridViewClickListener != null) {
                            gridViewClickListener!!.onSingleClicked(iconInfo)
                        }
                    }
                    return isInterupt
                }

                override fun viewUpdateData(position: Int) {
                    gridViewNoScroll.notifyDataSetChange(true)
                }
            })
            for (columnIndex in 0 until CustomGroup.COLUMNUM) {
                val itemView = View.inflate(mContext, R.layout.gridview_above_itemview, null)
                val ivIcon = itemView.findViewById<View>(R.id.icon_iv) as ImageView
                val tvName = itemView.findViewById<View>(R.id.name_tv) as TextView
                val itemInfoIndex = rowIndex * CustomGroup.COLUMNUM + columnIndex
                if (itemInfoIndex > iconInfoList.size - 1) {
                    itemView.visibility = View.INVISIBLE
                } else {
                    val iconInfo = iconInfoList[itemInfoIndex]
                    ivIcon.setImageResource(iconInfo.resIconId)
                    tvName.text = iconInfo.name
                    itemView.id = itemInfoIndex
                    itemView.tag = itemInfoIndex

                    itemView.setOnClickListener(itemClickLitener)
                    itemView.setOnLongClickListener { v ->
                        if (iconInfo.id != MORE) {
                            val position = v.tag as Int
                            mCustomGroup.setEditModel(true, position)
                        }
                        true
                    }
                }

                llRowContainer.addView(itemView, itemParam)
                val view = View(mContext)
                view.setBackgroundResource(R.color.gap_line)
                llRowContainer.addView(view, verticalParams)
            }
            val view = View(mContext)
            view.setBackgroundResource(R.color.gap_line)
            addView(view, horizontalParams)
            addView(rowView, rowParam)
            if (rowIndex == rowNum - 1) {
                val btmView = View(mContext)
                btmView.setBackgroundResource(R.color.gap_line)
                addView(btmView, horizontalParams)
            }

        }
    }

    fun showPreviewView() {
        // gridViewNoScroll.showPreviewView()
    }

    /**
     *
     * 方法: setViewCollaps
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
     * 时间: 2015年8月25日 下午7:03:23
     */
    fun setViewCollaps() {
        if (mItemViewClickListener != null) {
            mItemViewClickListener!!.closeExpandView()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        this.firstEvent = ev
        if (mCustomGroup.isEditModel) {
            mCustomGroup.sendEventBehind(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    interface ItemViewClickInterface {
        fun shoudInteruptViewAnimtion(animUtil: ItemViewClickListener, position: Int): Boolean

        fun viewUpdateData(position: Int)
    }

    inner class ItemViewClickListener(
        val contentView: View,
        private val mViewFlag: View,
        private val animationListener: ItemViewClickInterface?
    ) : View.OnClickListener {
        private val INVALID_ID = -1000
        private var mLastViewID = INVALID_ID

        private var startX: Int = 0
        private var viewFlagWidth: Int = 0
        private var itemViewWidth: Int = 0

        override fun onClick(view: View) {
            val viewId = view.id
            var isTheSameView = false
            if (animationListener != null) {
                if (animationListener.shoudInteruptViewAnimtion(this, viewId)) {
                    return
                }
            }
            if (mLastViewID == viewId) {
                isTheSameView = true
            } else {
                mViewFlag.visibility = View.VISIBLE
                viewFlagWidth = getViewFlagWidth()
                itemViewWidth = view.width
                val endX = view.left + itemViewWidth / 2 - viewFlagWidth / 2
                if (mLastViewID == INVALID_ID) {
                    startX = endX
                    xAxismoveAnim(mViewFlag, startX, endX)
                } else {
                    xAxismoveAnim(mViewFlag, startX, endX)
                }
                startX = endX
            }
            val isVisible = contentView.visibility == View.VISIBLE
            if (isVisible) {
                if (isTheSameView) {
                    animateCollapsing(contentView)
                } else {
                    animationListener?.viewUpdateData(viewId)
                }
            } else {
                if (isTheSameView) {
                    mViewFlag.visibility = View.VISIBLE
                    xAxismoveAnim(mViewFlag, startX, startX)
                }
                animateExpanding(contentView)
            }
            mLastViewID = viewId
        }

        private fun getViewFlagWidth(): Int {
            var viewWidth = mViewFlag.width
            if (viewWidth == 0) {
                val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                mViewFlag.measure(widthSpec, heightSpec)
                viewWidth = mViewFlag.measuredWidth
            }
            return viewWidth
        }


        /**
         *
         * 方法: xAxismoveAnim
         *
         *
         * 描述: x轴移动
         *
         *
         * 参数: @param v
         * 参数: @param startX
         * 参数: @param toX
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
         * 时间: 2015年8月25日 下午7:03:35
         */
        fun xAxismoveAnim(v: View, startX: Int, toX: Int) {
            moveAnim(v, startX, toX, 0, 0, 200)
        }

        /**
         *
         * 方法: moveAnim
         *
         *
         * 描述: 移动动画
         *
         *
         * 参数: @param v
         * 参数: @param startX
         * 参数: @param toX
         * 参数: @param startY
         * 参数: @param toY
         * 参数: @param during
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
         * 时间: 2015年8月25日 下午7:03:40
         */
        private fun moveAnim(v: View, startX: Int, toX: Int, startY: Int, toY: Int, during: Long) {
            val anim = TranslateAnimation(startX.toFloat(), toX.toFloat(), startY.toFloat(), toY.toFloat())
            anim.duration = during
            anim.fillAfter = true
            v.startAnimation(anim)
        }

        /**
         *
         * 方法: closeExpandView
         *
         *
         * 描述: 收缩
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
         * 时间: 2015年8月25日 下午7:03:49
         */
        fun closeExpandView() {
            val isVisible = contentView.visibility == View.VISIBLE
            if (isVisible) {
                animateCollapsing(contentView)
            }
        }

        /**
         *
         * 方法: animateCollapsing
         *
         *
         * 描述: 收缩动画
         *
         *
         * 参数: @param view
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
         * 时间: 2015年8月25日 下午7:04:01
         */
        fun animateCollapsing(view: View) {
            val origHeight = view.height

            val animator = createHeightAnimator(view, origHeight, 0)
            animator.addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animator: Animator) {
                    view.visibility = View.GONE
                    mViewFlag.clearAnimation()
                    mViewFlag.visibility = View.GONE
                }
            })
            animator.start()
        }

        /**
         *
         * 方法: animateExpanding
         *
         *
         * 描述: 动画展开
         *
         *
         * 参数: @param view
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
         * 时间: 2015年8月25日 下午7:04:22
         */
        fun animateExpanding(view: View) {
            view.visibility = View.VISIBLE
            val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(widthSpec, heightSpec)
            val animator = createHeightAnimator(view, 0, view.measuredHeight)
            animator.start()
        }

        /**
         *
         * 方法: createHeightAnimator
         *
         *
         * 描述: TODO
         *
         *
         * 参数: @param view
         * 参数: @param start
         * 参数: @param end
         * 参数: @return
         *
         *
         * 返回: ValueAnimator
         *
         *
         * 异常
         *
         *
         * 作者: wedcel wedcel@gmail.com
         *
         *
         * 时间: 2015年8月25日 下午7:04:29
         */
        fun createHeightAnimator(view: View, start: Int, end: Int): ValueAnimator {
            val animator = ValueAnimator.ofInt(start, end)
            animator.addUpdateListener { valueAnimator ->
                val value = valueAnimator.getAnimatedValue() as Int

                val layoutParams = view.layoutParams
                layoutParams.height = value
                view.layoutParams = layoutParams
            }
            return animator
        }

    }

    companion object {
        val MORE = 99999
    }
}
