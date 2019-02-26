package com.dyyj.idd.chatmore.model.network.Interceptors;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import timber.log.Timber;

public class HttpMyLogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        //TODO
        String url = request.url().url().toString();

        RequestBody requestBody = request.body();
        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(Charset.forName("UTF-8"));
            }
            //TODO
            String params = buffer.readString(charset);

            //TODO
            Long requestTime = System.currentTimeMillis();
        }

        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            throw e;
        }
        //TODO
        Long responseTime = System.currentTimeMillis();

        //TODO
        String result = "";
        if (response == null) {
            result = "-1 网络错误";
        } else {
            result = response.code() + (response.message().isEmpty() ? "" : ' ' + response.message());
        }

        //ChatApp.Companion.getInstance().getDataRepository().insertHttpLog(url, params, result, requestTime, responseTime);

        return response;
    }
}
