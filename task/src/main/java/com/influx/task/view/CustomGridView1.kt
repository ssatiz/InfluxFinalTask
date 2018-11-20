package com.influx.task.view


import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.influx.task.R
import com.influx.task.model.DargChildInfo
import com.influx.task.other.CommUtil
import java.util.*

/**
 * 类: CustomGridView
 *
 *
 * 描述: TODO
 *
 *
 * 作者: wedcel wedcel@gmail.com
 *
 *
 * 时间: 2015年8月25日 下午7:07:44
 *
 *
 */
class CustomGridView1 : LinearLayout {

    private var mContext: Context? = null
    private val mPlayList = ArrayList<DargChildInfo>()
    private var viewHeight: Int = 0
    private var viewWidth: Int = 0
    private var mParentView: LinearLayout? = null
    private var rowNum: Int = 0
    private var verticalViewWidth: Int = 0
    var childClickListener: CustomChildClickListener? = null

    interface CustomChildClickListener {
        fun onPreviewClicked()
        fun onBuyTicketClicked()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    private fun initView(context: Context) {
        this.mContext = context
        verticalViewWidth = CommUtil.dip2px(mContext!!, 1f)
        val root = View.inflate(mContext, R.layout.preview_layout, null)

        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        root.measure(widthSpec, heightSpec)
        viewHeight = root.measuredHeight
        viewWidth = (mContext!!.resources.displayMetrics.widthPixels - CommUtil.dip2px(mContext!!, 2f)) /
                CustomGroup.CHILD_COLUMNUM
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    fun refreshDataSet(playList: ArrayList<DargChildInfo>) {
        mPlayList.clear()
        mPlayList.addAll(playList)
        notifyDataSetChange(false)
    }

    fun notifyDataSetChange(needAnim: Boolean) {
        removeAllViews()
        rowNum = mPlayList.size / CustomGroup.COLUMNUM + if (mPlayList.size % CustomGroup.COLUMNUM > 0) 1 else 0
        val rowParam =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val verticalParams = LinearLayout.LayoutParams(verticalViewWidth, LinearLayout.LayoutParams.FILL_PARENT)
        val horizontalParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, verticalViewWidth)
        for (rowIndex in 0 until rowNum) {
            val llContainer = LinearLayout(mContext)
            llContainer.orientation = LinearLayout.HORIZONTAL
            val itemParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            itemParam.width = viewWidth
            for (columnIndex in 0 until CustomGroup.COLUMNUM) {
                val itemInfoIndex = rowIndex * CustomGroup.COLUMNUM + columnIndex
                var isValidateView = true
                if (itemInfoIndex >= mPlayList.size) {
                    isValidateView = false
                }
                val root = View.inflate(mContext, R.layout.preview_layout, null)
                val previewButton = root.findViewById<View>(R.id.btnOpenPanel) as RelativeLayout
                val buyTicketsButton = root.findViewById<View>(R.id.btnBuyTickets) as Button
                val superChild = root.findViewById<View>(R.id.super_child) as LinearLayout

                if (isValidateView) {

                    previewButton.setOnClickListener {
                        if (childClickListener != null) {
//                            childClickListener!!.onPreviewClicked()
                            showPreviewView(root, superChild)
                        }
                    }
                    buyTicketsButton.setOnClickListener {
                        if (childClickListener != null) {
                            childClickListener!!.onBuyTicketClicked()
                        }
                    }

                }
                llContainer.addView(root, itemParam)
                if (columnIndex != CustomGroup.COLUMNUM - 1) {
                    val view = View(mContext)
                    view.setBackgroundResource(R.drawable.ver_line)
//                    llContainer.addView(view, verticalParams)
                }
            }
            addView(llContainer, rowParam)
            val view = View(mContext)
            view.setBackgroundResource(R.drawable.hor_line)
//            addView(view, horizontalParams)
            Log.e("animator", "" + height + "--" + rowNum * viewHeight)
            if (needAnim) {
                createHeightAnimator(mParentView, height, rowNum * viewHeight)
            }
        }
    }


    fun createHeightAnimator(view: View?, start: Int, end: Int) {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int

            val layoutParams = view!!.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        animator.duration = 200
        animator.start()
    }

    fun createChildHeightAnimator(view: View?, start: Int, end: Int) {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int

            val layoutParams = view!!.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        animator.duration = 200
        animator.start()
    }

    /**
     * 方法: setParentView
     *
     *
     * 描述: TODO
     *
     *
     * 参数: @param llBtm
     *
     *
     * 返回: void
     *
     *
     */
    fun setParentView(llBtm: LinearLayout) {
        this.mParentView = llBtm
    }

    fun showPreviewView(root: View, superChild: LinearLayout) {
        superChild.visibility = View.VISIBLE
        Log.e("Child animator", "" + height + "--" + (root.measuredHeight * 2))
        createChildHeightAnimator(mParentView, height, (root.measuredHeight * 2))

    }

}

