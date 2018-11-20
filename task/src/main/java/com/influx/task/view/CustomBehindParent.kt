package com.influx.task.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.influx.task.R
import com.influx.task.model.DragIconInfo


import java.util.ArrayList


/**
 *
 * 类: CustomBehindParent
 *
 *
 * 描述: TODO
 *
 *
 * 作者: wedcel wedcel@gmail.com
 *
 *
 * 时间: 2015年8月25日 下午6:51:29
 *
 *
 */
class CustomBehindParent(private val mContext: Context, customGroup: CustomGroup) : RelativeLayout(mContext) {
    private val mCustomBehindEditView: CustomBehindView

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
     * 时间: 2015年8月25日 下午6:51:39
     */
    val editList: ArrayList<DragIconInfo>
        get() = mCustomBehindEditView.editList

    val isModifyedOrder: Boolean
        get() = mCustomBehindEditView.isModifyedOrder


    init {
        mCustomBehindEditView = CustomBehindView(mContext, customGroup)
        mCustomBehindEditView.horizontalSpacing = 1
        mCustomBehindEditView.verticalSpacing = 1
        mCustomBehindEditView.selector = ColorDrawable(Color.TRANSPARENT)
        mCustomBehindEditView.setBackgroundColor(mContext.resources.getColor(R.color.gap_line))
        val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.FILL_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        addView(mCustomBehindEditView, lp)
        mCustomBehindEditView.setDeletAnimView(this)


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
     * 时间: 2015年8月25日 下午6:46:38
     */
    fun refreshIconInfoList(iconInfoList: ArrayList<DragIconInfo>) {
        mCustomBehindEditView.refreshIconInfoList(iconInfoList)
    }

    /**
     *
     * 方法: notifyDataSetChange
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
     * 时间: 2015年8月25日 下午6:51:15
     */
    fun notifyDataSetChange(iconInfoList: ArrayList<DragIconInfo>) {
        mCustomBehindEditView.notifyDataSetChange(iconInfoList)
    }

    /**
     *
     * 方法: drawWindowView
     *
     *
     * 描述: TODO
     *
     *
     * 参数: @param position
     * 参数: @param event
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
     * 时间: 2015年8月25日 下午6:51:42
     */
    fun drawWindowView(position: Int, event: MotionEvent) {
        mCustomBehindEditView.drawWindowView(position, event)
    }

    /**
     *
     * 方法: childDispatchTouchEvent
     *
     *
     * 描述: TODO
     *
     *
     * 参数: @param ev
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
     * 时间: 2015年8月25日 下午6:51:48
     */
    fun childDispatchTouchEvent(ev: MotionEvent) {
        mCustomBehindEditView.dispatchTouchEvent(ev)
    }

    fun cancleModifyOrderState() {
        mCustomBehindEditView.cancleModifyedOrderState()
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
     * 时间: 2015年8月25日 下午6:51:51
     */
    fun resetHidePosition() {
        mCustomBehindEditView.resetHidePosition()
    }

    /**
     *
     * 方法: isValideEvent
     *
     *
     * 描述: TODO
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
     * 时间: 2015年8月25日 下午4:50:55
     */
    fun isValideEvent(ev: MotionEvent, scrolly: Int): Boolean {
        return mCustomBehindEditView.isValideEvent(ev, scrolly)
    }

    /**
     *
     * 方法: clearDragView
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
     * 时间: 2015年8月25日 下午6:51:55
     */
    fun clearDragView() {
        mCustomBehindEditView.clearDragView()
    }

}
