package com.dyyj.idd.chatmore.eventtracking

import android.os.Message

/**
 * Created by Corey_Jia on 2018/7/5.
 */
class EventMessage<T> {

    var obj: T? = null//消息内容
    var tag: String? = null //消息类型
    var what: Int = 0
    var msg: Message? = null

    constructor(tag: String, msg: Message) : super() {
        this.tag = tag
        this.msg = msg
    }

    constructor(tag: String, what: Int, obj: T) : super() {
        this.tag = tag
        this.obj = obj
        this.what = what
    }

    constructor(tag: String, obj: T) : super() {
        this.tag = tag
        this.obj = obj
    }

    constructor(tag: String, what: Int) : super() {
        this.tag = tag
        this.what = what
    }
}
