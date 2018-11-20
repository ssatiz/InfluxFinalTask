package com.influx.task.other

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import com.influx.task.R
import com.influx.task.model.DragIconInfo
import com.influx.task.view.CustomBehindView


import java.util.ArrayList
import java.util.Collections

/**
 *
 * 类: DragGridAdapter
 *
 *
 * 描述: TODO
 *
 *
 * 作者: wedcel wedcel@gmail.com
 *
 *
 * 时间: 2015年8月25日 下午5:02:40
 *
 *
 */
class DragGridAdapter(
    private val mContext: Context,
    private val mIconInfoList: ArrayList<DragIconInfo>,
    private val mCustomBehindView: CustomBehindView
) : BaseAdapter() {
    private val INVALID_POSIT = -100
    private var mHidePosition = INVALID_POSIT
    private var modifyPosition = INVALID_POSIT
    private val mHandler = Handler()
    var isHasModifyedOrder = false

    fun setModifyPosition(position: Int) {
        modifyPosition = position
    }


    override fun getCount(): Int {
        return mIconInfoList.size
    }

    override fun getItem(position: Int): Any {
        return mIconInfoList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var viewHold: ViewHold? = null
        if (convertView == null) {
            viewHold = ViewHold()
            convertView = View.inflate(mContext, R.layout.gridview_behind_itemview, null)
            viewHold.llContainer = convertView!!.findViewById<View>(R.id.edit_ll) as LinearLayout
            viewHold.ivIcon = convertView.findViewById<View>(R.id.icon_iv) as ImageView
            viewHold.tvName = convertView.findViewById<View>(R.id.name_tv) as TextView
            viewHold.ivDelet = convertView.findViewById<View>(R.id.delet_iv) as ImageButton
            convertView.tag = viewHold
        } else {
            viewHold = convertView.tag as ViewHold
        }
        val iconInfo = mIconInfoList[position]
        viewHold.ivIcon!!.setImageResource(iconInfo.resIconId)
        viewHold.tvName!!.text = iconInfo.name
        viewHold.ivDelet!!.setOnClickListener {
            modifyPosition = INVALID_POSIT
            mCustomBehindView.deletInfo(position, iconInfo)
        }
        if (modifyPosition == position) {
            viewHold.llContainer!!.setBackgroundColor(mContext.resources.getColor(R.color.item_bg))
            viewHold.ivDelet!!.visibility = View.VISIBLE
        } else {
            viewHold.llContainer!!.setBackgroundColor(mContext.resources.getColor(R.color.white))
            viewHold.ivDelet!!.visibility = View.GONE
        }
        convertView.setOnClickListener {
            if (position != modifyPosition) {
                modifyPosition = INVALID_POSIT
                mCustomBehindView.cancleEditModel()
            }
        }
        if (position == mHidePosition) {
            convertView.visibility = View.INVISIBLE
        } else {
            convertView.visibility = View.VISIBLE
        }
        return convertView
    }

    /**
     *
     * 方法: reorderItems
     *
     *
     * 描述: TODO
     *
     *
     * 参数: @param oldPosition
     * 参数: @param newPosition
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
     * 时间: 2015年8月25日 下午5:02:53
     */
    fun reorderItems(oldPosition: Int, newPosition: Int) {
        val temp = mIconInfoList[oldPosition]
        if (oldPosition < newPosition) {
            for (i in oldPosition until newPosition) {
                Collections.swap(mIconInfoList, i, i + 1)
            }
        } else if (oldPosition > newPosition) {
            for (i in oldPosition downTo newPosition + 1) {
                Collections.swap(mIconInfoList, i, i - 1)
            }
        }
        mIconInfoList[newPosition] = temp
        modifyPosition = newPosition
        isHasModifyedOrder = true
    }

    /**
     *
     * 方法: setHideItem
     *
     *
     * 描述:  拖动的时候会隐藏某个
     *
     *
     * 参数: @param hidePosition
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
     * 时间: 2015年8月25日 下午5:03:05
     */
    fun setHideItem(hidePosition: Int) {
        this.mHidePosition = hidePosition
        notifyDataSetChanged()
    }

    fun deleteItem(deletPosit: Int) {
        mIconInfoList.removeAt(deletPosit)
        notifyDataSetChanged()
        mHandler.postDelayed({
            isHasModifyedOrder = true
            mCustomBehindView.cancleEditModel()
        }, 500)
    }

    internal inner class ViewHold {
        var llContainer: LinearLayout? = null
        var ivIcon: ImageView? = null
        var ivDelet: ImageView? = null
        var tvName: TextView? = null
    }

    /**
     *
     * 方法: resetModifyPosition
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
     * 时间: 2015年8月25日 下午5:03:16
     */
    fun resetModifyPosition() {
        modifyPosition = INVALID_POSIT
    }
}
