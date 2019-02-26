package com.dyyj.idd.chatmore.utils;

import io.reactivex.disposables.Disposable;

/**
 * 数据库查询结果接口
 *
 * @author yangjiaming
 * @version 1.0.0
 * @date 2018/11/18
 */

public abstract class ResultCallBack<T> {

    public void onSubscribe(Disposable d) {

    }

    /**
     * 返回结果
     *
     * @param t 结果
     */
    public abstract void result(T t);

    /**
     * 错误
     */
    public void error(Throwable e) {

    }

    public void complete() {

    }
}
