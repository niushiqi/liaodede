package com.dyyj.idd.chatmore.utils;

/**
 * 子线程请求
 *
 * @author yangjiaming
 * @version 1.0.0
 * @date 2018/11/18
 */

public abstract class ThreadRequest<T> {
    /**
     * 子线程执行的操作
     *
     * @return
     */
    public abstract T run();
}
