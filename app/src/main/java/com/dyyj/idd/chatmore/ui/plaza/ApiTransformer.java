package com.dyyj.idd.chatmore.ui.plaza;

import com.dyyj.idd.chatmore.model.network.result.PlazaFlowCardResult;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

/**
 * $desc$
 *
 * @author DengCiPing
 * @date 2017/8/4 下午9:14
 */
public class ApiTransformer<T extends PlazaFlowCardResult> implements ObservableTransformer<T, T>, FlowableTransformer<T, T> {
    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream.flatMap(new Function<T, Publisher<T>>() {
            @Override
            public Publisher<T> apply(T model) throws Exception {

                if (model == null || model.getErrorCode() != 200) {
                    return Flowable.error(new NetError(model.getErrorMsg()));
                } else {
                    return Flowable.just(model);
                }
            }
        });
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.flatMap(new Function<T, ObservableSource<T>>() {
            @Override
            public ObservableSource<T> apply(T model) throws Exception {

                if (model == null || model.getErrorCode() != 200) {
                    return Observable.error(new NetError(model.getErrorMsg()));
                } else {
                    return Observable.just(model);
                }
            }
        });
    }

    public static class NetError extends Exception {
        public NetError(String message) {
            super(message);
        }
    }
}
