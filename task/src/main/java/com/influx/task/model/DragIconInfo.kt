package com.influx.task.model

import java.util.ArrayList

/**
 * 类: DragIconInfo
 *
 *
 * 描述: 拖动显示的view和icon
 *
 *
 * 作者: wedcel wedcel@gmail.com
 *
 *
 * 时间: 2015年8月25日 下午5:08:23
 *
 *
 */
class   DragIconInfo {

    var id: Int = 0
    var name: String? = null
    var resIconId: Int = 0
    /**
     * 类型
     */
    var category: Int = 0

    /**
     * 展开的child
     */
    var childList = ArrayList<DargChildInfo>()


    constructor() {
        // TODO Auto-generated constructor stub
    }


    constructor(
        id: Int, name: String, resIconId: Int, category: Int,
        childList: ArrayList<DargChildInfo>
    ) : super() {
        this.id = id
        this.name = name
        this.resIconId = resIconId
        this.category = category
        this.childList = childList
    }

    companion object {

        /**
         * 可展开的
         */
        val CATEGORY_EXPAND = 100

        /**
         * 不可展开的
         */
        val CATEGORY_ONLY = 300
    }


}
