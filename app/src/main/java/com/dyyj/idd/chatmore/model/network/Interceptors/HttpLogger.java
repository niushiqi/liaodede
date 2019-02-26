package com.dyyj.idd.chatmore.model.network.Interceptors;

import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

/**
 * Created by wangbin on 2018/11/21.
 */

public class HttpLogger  implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
        Timber.tag("okhttplog").i(message);
    }

}
