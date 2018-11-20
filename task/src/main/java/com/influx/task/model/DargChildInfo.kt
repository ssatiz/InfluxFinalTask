package com.influx.task.model


/**
 *
 * 类: DargChildInfo
 *
 *
 * 描述: 子item显示
 *
 *
 * 作者: wedcel wedcel@gmail.com
 *
 *
 * 时间: 2015年8月25日 下午5:24:04
 *
 *
 */
class DargChildInfo {

    var id: Int = 0
    var name: String? = null


    constructor() {
        // TODO Auto-generated constructor stub
    }


    constructor(id: Int, name: String) : super() {
        this.id = id
        this.name = name
    }


}
