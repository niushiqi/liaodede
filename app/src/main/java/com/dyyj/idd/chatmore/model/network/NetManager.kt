package com.dyyj.idd.chatmore.model.network

import com.dyyj.idd.chatmore.model.network.Interceptors.HttpLogger
import com.dyyj.idd.chatmore.model.network.Interceptors.HttpMyLogInterceptor
import com.google.gson.Gson
import com.gt.common.gtchat.model.network.NetApi
import com.gt.common.gtchat.model.network.NetURL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/03/08
 * desc   : 网络管理
 */
class NetManager {

  val CONNECT_TIMEOUT: Long = 60
  val READ_TIMEOUT: Long = 100
  val WRITE_TIMEOUT: Long = 60

  private val mOkHttpClient: OkHttpClient by lazy { createOkhttp() }

  private val mRetrofit: Retrofit by lazy { createRetrofit2() }

  private val mNetApi: NetApi by lazy { createNetApi() }

  private val mGson: Gson by lazy { Gson() }

  fun getGson() = mGson

  fun getRetrofit() = mRetrofit

  fun getOkHttpClient() = mOkHttpClient

  fun getNetApi() = mNetApi

  private fun createNetApi(): NetApi {
    return mRetrofit.create(NetApi::class.java)
  }


  private fun createRetrofit2(): Retrofit {
    return Retrofit.Builder().baseUrl(NetURL.HOST).addConverterFactory(
        ScalarsConverterFactory.create()).addConverterFactory(
        GsonConverterFactory.create(mGson)).addCallAdapterFactory(
        RxJava2CallAdapterFactory.create()).client(mOkHttpClient).build()
  }

  private fun createOkhttp(): OkHttpClient {
    var logInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor(HttpLogger());
    logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    return OkHttpClient()
            .newBuilder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
            .hostnameVerifier(getHostnameVerifier())
            .sslSocketFactory(createCertificates())
            .addInterceptor(logInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(HttpMyLogInterceptor())
            .build()
  }

  private fun getHostnameVerifier(): HostnameVerifier {
    return HostnameVerifier { _, _ -> true }
  }


  private fun createCertificates(): SSLSocketFactory? {
    var sSLSocketFactory: SSLSocketFactory? = null

    val manager = object : X509TrustManager {
      override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

      }

      override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

      }

      override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
      }
    }

    try {
      var sc = SSLContext.getInstance("TLS")
      val array = arrayOf(manager)
      sc.init(null, array, SecureRandom())
      sSLSocketFactory = sc.socketFactory
    } catch (e: Exception) {
    }

    return sSLSocketFactory

  }
}