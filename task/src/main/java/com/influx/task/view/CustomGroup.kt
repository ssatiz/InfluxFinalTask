package com.influx.task.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.influx.task.R
import com.influx.task.model.DargChildInfo
import com.influx.task.model.DragIconInfo
import java.util.*

/**
 *
 * 类: CustomGroup
 *
 *
 * 描述: TODO
 *
 *
 * 作者: wedcel wedcel@gmail.com
 *
 *
 * 时间: 2015年8月25日 下午6:54:26
 *
 *
 */
class CustomGroup @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null) :
    ViewGroup(mContext, attrs) {

    private val mCustomAboveView: CustomAboveView
    private val mCustomBehindParent: CustomBehindParent
    var isEditModel = false
        private set
    //所有以的list
    private val allInfoList = ArrayList<DragIconInfo>()
    /**显示的带more的list */
    private val homePageInfoList = ArrayList<DragIconInfo>()
    /**可展开的list */
    private var expandInfoList = ArrayList<DragIconInfo>()

    /**不可展开的list */
    private var onlyInfoList = ArrayList<DragIconInfo>()

    var editModelListener: InfoEditModelListener? = null


    interface InfoEditModelListener {
        fun onModleChanged(isEditModel: Boolean)
    }

    init {
        val upParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mCustomAboveView = CustomAboveView(mContext, this)
        addView(mCustomAboveView, upParams)
        val downParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mCustomBehindParent = CustomBehindParent(mContext, this)
        addView(mCustomBehindParent, downParams)
        initData()
    }

    /**
     *
     * 方法: initData
     *
     *
     * 描述: 初始化监听和数据
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
     * 时间: 2015年8月25日 下午5:29:40
     */
    private fun initData() {

        setCustomViewClickListener(object : CustomAboveView.CustomAboveViewClickListener {

            override fun onSingleClicked(iconInfo: DragIconInfo) {
                dispatchSingle(iconInfo)
            }

            override fun onChildClicked() {
                dispatchChild()
            }
        })

        initIconInfo()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasure = 0
        var heightMeasure = 0
        if (isEditModel) {
            mCustomBehindParent.measure(widthMeasureSpec, heightMeasureSpec)
            widthMeasure = mCustomBehindParent.measuredWidth
            heightMeasure = mCustomBehindParent.measuredHeight
        } else {
            mCustomAboveView.measure(widthMeasureSpec, heightMeasureSpec)
            widthMeasure = mCustomAboveView.measuredWidth
            heightMeasure = mCustomAboveView.measuredHeight
        }
        setMeasuredDimension(widthMeasure, heightMeasure)

    }

    /**
     * 方法: onLayout
     *
     *
     * 描述: TODO
     *
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     *
     *
     * @see ViewGroup.onLayout
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (isEditModel) {
            val behindHeight = mCustomBehindParent.measuredHeight
            mCustomBehindParent.layout(l, 0, r, behindHeight + t)
        } else {
            val aboveHeight = mCustomAboveView.measuredHeight
            mCustomAboveView.layout(l, 0, r, aboveHeight + t)
        }
    }

    /**
     *
     * 方法: initIconInfo
     *
     *
     * 描述: 初始化数据
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
     * 时间: 2015年8月25日 下午5:33:14
     */
    private fun initIconInfo() {


        allInfoList.clear()
        allInfoList.addAll(initAllOriginalInfo())
        getPageInfoList()

        refreshIconInfo()
    }

    /**
     *
     * 方法: initAllOriginalInfo
     *
     *
     * 描述: 初始化Icon info
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
     * 时间: 2015年8月25日 下午5:33:48
     */
    private fun initAllOriginalInfo(): ArrayList<DragIconInfo> {
        val iconInfoList = ArrayList<DragIconInfo>()
        val childList = initChildList()
        iconInfoList.add(DragIconInfo(1, "Expand 1", R.mipmap.ic_launcher, DragIconInfo.CATEGORY_EXPAND, childList))
        iconInfoList.add(DragIconInfo(2, "Expand 2", R.mipmap.ic_launcher, DragIconInfo.CATEGORY_EXPAND, childList))
        iconInfoList.add(DragIconInfo(3, "Expand 3", R.mipmap.ic_launcher, DragIconInfo.CATEGORY_EXPAND, childList))
        iconInfoList.add(DragIconInfo(4, "Expand 4", R.mipmap.ic_launcher, DragIconInfo.CATEGORY_EXPAND, childList))
        iconInfoList.add(DragIconInfo(5, "Expand 5", R.mipmap.ic_launcher, DragIconInfo.CATEGORY_EXPAND, childList))
        iconInfoList.add(DragIconInfo(6, "Expand 6", R.mipmap.ic_launcher, DragIconInfo.CATEGORY_EXPAND, childList))

        return iconInfoList
    }


    /**
     *
     * 方法: initChildList
     *
     *
     * 描述: 初始化child list
     *
     *
     * 参数: @return
     *
     *
     * 返回: ArrayList<DargChildInfo> </DargChildInfo>
     *
     *
     * 异常
     *
     *
     * 作者: wedcel wedcel@gmail.com
     *
     *
     * 时间: 2015年8月25日 下午5:36:12
     */
    private fun initChildList(): ArrayList<DargChildInfo> {
        val childList = ArrayList<DargChildInfo>()
        childList.add(DargChildInfo(1, "Item1"))
        return childList
    }

    /**
     *
     * 方法: getPageInfoList
     *
     *
     * 描述: 初始化显示
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
     * 时间: 2015年8月25日 下午5:37:33
     */
    private fun getPageInfoList() {
        homePageInfoList.clear()
        var count = 0
        for (info in allInfoList) {
            if (count < 9) {
                homePageInfoList.add(info)
                count++
            } else {
                break
            }
        }
    }

    /**
     *
     * 方法: refreshIconInfo
     *
     *
     * 描述: 刷新信息
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
     * 时间: 2015年8月25日 下午5:38:11
     */
    private fun refreshIconInfo() {
        judeHomeInfoValid()

        val moreInfo = getMoreInfoList(allInfoList, homePageInfoList)
        expandInfoList = getInfoByType(moreInfo, DragIconInfo.CATEGORY_EXPAND)
        onlyInfoList = getInfoByType(moreInfo, DragIconInfo.CATEGORY_ONLY)
        setIconInfoList(homePageInfoList)
    }


    /**
     *
     * 方法: judeHomeInfoValid
     *
     *
     * 描述: 判断下显示里面是否包含更多 或者看下是否是最后一个 固定更多的位置
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
     * 时间: 2015年8月25日 下午5:38:37
     */
    private fun judeHomeInfoValid() {
        var hasMoreInfo = false
        var posit = 0
        for (index in homePageInfoList.indices) {
            val tempInfo = homePageInfoList[index]
            if (tempInfo.id == CustomAboveView.MORE) {
                hasMoreInfo = true
                posit = index
                break
            }
        }
        if (!hasMoreInfo) {
            //没有更多 增加
            homePageInfoList.add(DragIconInfo(CustomAboveView.MORE, "更多", R.mipmap.ic_launcher, 0, ArrayList()))
        } else {
            if (posit != homePageInfoList.size - 1) {
                //不是最后一个
                val moreInfo = homePageInfoList.removeAt(posit)
                homePageInfoList.add(moreInfo)
            }
        }
    }


    /**
     *
     * 方法: getInfoByType
     *
     *
     * 描述: TODO
     *
     *
     * 参数: @param moreInfo
     * 参数: @param categorySpt
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
     * 时间: 2015年8月25日 下午6:50:25
     */
    private fun getInfoByType(moreInfo: ArrayList<DragIconInfo>, categorySpt: Int): ArrayList<DragIconInfo> {
        val typeList = ArrayList<DragIconInfo>()
        for (info in moreInfo) {
            if (info.category == categorySpt) {
                typeList.add(info)
            }
        }
        return typeList
    }


    fun setCustomViewClickListener(gridViewClickListener: CustomAboveView.CustomAboveViewClickListener) {
        mCustomAboveView.gridViewClickListener = gridViewClickListener
    }

    /**
     *
     * 方法: setIconInfoList
     *
     *
     * 描述: 设置信息
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
     * 时间: 2015年8月25日 下午6:45:55
     */
    fun setIconInfoList(iconInfoList: ArrayList<DragIconInfo>) {
        mCustomAboveView.refreshIconInfoList(iconInfoList)
        mCustomBehindParent.refreshIconInfoList(iconInfoList)
    }

    fun cancleEidtModel() {
        setEditModel(false, 0)
    }


    fun setEditModel(isEditModel: Boolean, position: Int) {
        this.isEditModel = isEditModel
        if (isEditModel) {
            mCustomAboveView.setViewCollaps()
            mCustomAboveView.visibility = View.GONE
            mCustomBehindParent.notifyDataSetChange(mCustomAboveView.iconInfoList)
            mCustomBehindParent.visibility = View.VISIBLE
            mCustomBehindParent.drawWindowView(position, mCustomAboveView.firstEvent!!)
        } else {
            homePageInfoList.clear()
            homePageInfoList.addAll(mCustomBehindParent.editList)
            mCustomAboveView.refreshIconInfoList(homePageInfoList)
            mCustomAboveView.visibility = View.VISIBLE
            mCustomBehindParent.visibility = View.GONE
            if (mCustomBehindParent.isModifyedOrder) {
                mCustomBehindParent.cancleModifyOrderState()
            }
            mCustomBehindParent.resetHidePosition()
        }
        if (editModelListener != null) {
            editModelListener!!.onModleChanged(isEditModel)
        }
    }


    fun sendEventBehind(ev: MotionEvent) {
        mCustomBehindParent.childDispatchTouchEvent(ev)
    }

    /**
     *
     * 方法: getMoreInfoList
     *
     *
     * 描述: TODO
     *
     *
     * 参数: @param allInfoList
     * 参数: @param homePageInfoList
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
     * 时间: 2015年8月25日 下午6:57:06
     */
    private fun getMoreInfoList(
        allInfoList: ArrayList<DragIconInfo>,
        homePageInfoList: ArrayList<DragIconInfo>
    ): ArrayList<DragIconInfo> {
        val moreInfoList = ArrayList<DragIconInfo>()
        moreInfoList.addAll(allInfoList)
        moreInfoList.removeAll(homePageInfoList)
        return moreInfoList
    }


    /**
     *
     * 方法: deletHomePageInfo
     *
     *
     * 描述: TODO
     *
     *
     * 参数: @param dragIconInfo
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
     * 时间: 2015年8月25日 下午6:56:19
     */
    fun deletHomePageInfo(dragIconInfo: DragIconInfo) {
        homePageInfoList.remove(dragIconInfo)
        mCustomAboveView.refreshIconInfoList(homePageInfoList)
        val category = dragIconInfo.category
        when (category) {
            DragIconInfo.CATEGORY_ONLY -> onlyInfoList.add(dragIconInfo)
            DragIconInfo.CATEGORY_EXPAND -> expandInfoList.add(dragIconInfo)
            else -> {
            }
        }
        allInfoList.remove(dragIconInfo)
        allInfoList.add(dragIconInfo)
    }


    /**
     *
     * 方法: dispatchChild
     *
     *
     * 描述: 点击child
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
     * 时间: 2015年8月25日 下午5:30:58
     */
    protected fun dispatchChild() {

        Toast.makeText(mContext, "Test", Toast.LENGTH_SHORT).show()
       // mCustomAboveView.showPreviewView()
    }


    /**
     *
     * 方法: dispatchSingle
     *
     *
     * 描述: 没child的点击
     *
     *
     * 参数: @param dragInfo
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
     * 时间: 2015年8月25日 下午5:30:40
     */
    fun dispatchSingle(dragInfo: DragIconInfo?) {
        if (dragInfo == null) {
            return
        }
        Toast.makeText(mContext, "点击了icon" + dragInfo.name!!, Toast.LENGTH_SHORT).show()
    }


    fun isValideEvent(ev: MotionEvent, scrolly: Int): Boolean {
        return mCustomBehindParent.isValideEvent(ev, scrolly)
    }


    fun clearEditDragView() {
        mCustomBehindParent.clearDragView()
    }

    companion object {
        val COLUMNUM = 3
        val CHILD_COLUMNUM = 1
    }


}
