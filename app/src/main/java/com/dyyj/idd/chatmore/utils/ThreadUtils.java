package com.dyyj.idd.chatmore.utils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Description
 *
 * @author yangjiaming
 * @version 1.0.0
 * @date 2018/11/18
 */
public class ThreadUtils {

    /**
     * RxJava开启子线程
     * io线程执行耗时操作，UI线程回调
     *
     * @param request
     */
    public static void operate(ThreadRequest request) {
        operate(request, null);
    }

    /**
     * RxJava开启子线程
     * io线程执行耗时操作，UI线程回调
     *
     * @param request
     * @param callBack 返回接口
     * @param <T>      返回类型
     */
    public static <T> void operate(ThreadRequest<T> request, ResultCallBack<T> callBack) {
        Observable.create(getObservable(request))
                //用线程做库的操作
                .subscribeOn(Schedulers.io())
                //主线程返回结果
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver(callBack));
    }


    public static <T> ObservableOnSubscribe getObservable(final ThreadRequest<T> request) {
        return new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                e.onNext(request.run());
            }
        };
    }

    /**
     * 获得Observer对象，实现一些返回方法
     *
     * @param callBack
     * @param <T>
     * @return
     */
    public static <T> Observer getObserver(final ResultCallBack<T> callBack) {
        return new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                if (callBack != null) {
                    callBack.onSubscribe(d);
                }
            }

            @Override
            public void onNext(Object o) {
                if (callBack != null) {
                    callBack.result((T) o);
                    onComplete();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (callBack != null) {
                    callBack.error(e);
                }
            }

            @Override
            public void onComplete() {
                if (callBack != null) {
                    callBack.complete();
                }
            }
        };
    }
}
