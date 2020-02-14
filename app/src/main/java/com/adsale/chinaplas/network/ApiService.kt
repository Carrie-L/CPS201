package com.adsale.chinaplas.network

import com.adsale.chinaplas.moshi
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * Created by Carrie on 2019/10/17.
 */

const val BASE_URL = "https://cdn.adsalecdn.com/App/2020/"
const val TXT_URL = "files/{fileName}"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

private val client1 = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build()

/**
 * 添加超时
 */
private val clientRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(client1)
    .baseUrl(BASE_URL)
    .build()


//OkHttpClient client = new OkHttpClient.Builder()
//.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
//.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
//.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
//.build();

fun <T, E> setupRxtrofitProgress(cls: Class<T>?,
                                 baseUrl: String?,
                                 callback: ProgressCallback?,
                                 entity: E): T {
    val interceptor: DownloadProgressInterceptor<E> = DownloadProgressInterceptor(callback, entity)
    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .retryOnConnectionFailure(true)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()
    val builder = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
    val retrofit = builder.client(client).build()
    return retrofit.create(cls)
}


interface ApiService {

    @GET(TXT_URL)
    fun downTxtAsync(@Path("fileName") fileName: String): Deferred<ResponseBody>

    @GET("https://dp7cpcoii5.execute-api.ap-northeast-1.amazonaws.com/prod/helloworld")
    fun getMainIcons(): Deferred<ResponseBody>

    @Headers(
        "Content-Type:application/json",
        "X-Bmob-Application-Id:1300daedb47114d5ebc6412a1ac1b9c1",
        "X-Bmob-REST-API-Key:c9ecff6e1b4a7cd653a225b27494d467"
    )
    @GET("https://api2.bmob.cn/1/classes/MainIcon")
    fun getMainIconsBmobAsync(@Query("where") where: String): Deferred<ResponseBody>

    @Headers(
        "Content-Type:application/json",
        "X-Bmob-Application-Id:1300daedb47114d5ebc6412a1ac1b9c1",
        "X-Bmob-REST-API-Key:c9ecff6e1b4a7cd653a225b27494d467"
    )
    @GET("https://api2.bmob.cn/1/classes/NoSQL")
    fun getNoSQL(@Query("where") where: String, @Query("limit") limit: Int): Deferred<ResponseBody>

    @Headers(
        "Content-Type:application/json",
        "X-Bmob-Application-Id:1300daedb47114d5ebc6412a1ac1b9c1",
        "X-Bmob-REST-API-Key:c9ecff6e1b4a7cd653a225b27494d467"
    )
    @GET("https://api2.bmob.cn/1/classes/Country")
    fun getCountryAsync(@Query("where") where: String, @Query("limit") limit: Int, @Query("skip") skip: Int): Deferred<ResponseBody>

    /**
     *
     */
    @Headers(
        "Content-Type:application/json",
        "X-Bmob-Application-Id:1300daedb47114d5ebc6412a1ac1b9c1",
        "X-Bmob-REST-API-Key:c9ecff6e1b4a7cd653a225b27494d467"
    )
    @GET("https://api2.bmob.cn/1/classes/Country")
    fun getCountryCountAsync(@Query("where") where: String, @Query("limit") limit: Int, @Query("count") count: Int): Deferred<ResponseBody>


    @FormUrlEncoded
    @Headers("Accept:application/x-www-form-urlencoded")
    @POST("https://eform.adsale.com.hk/GeniusAnalyst/api/appapi/GetShowPreregOptionData_APP")
    fun getRegData(@Field("ShowCode") showCode: String, @Field("LangId") langId: String): Deferred<ResponseBody>
    //    fun getRegData(@Body body: JSONObject): Deferred<ResponseBody>


    @Headers(
        "Content-Type:application/json",
        "X-Bmob-Application-Id:1300daedb47114d5ebc6412a1ac1b9c1",
        "X-Bmob-REST-API-Key:c9ecff6e1b4a7cd653a225b27494d467"
    )
    @GET("https://api2.bmob.cn/1/classes/RegisterOptionData")
    fun getProductsAsync(@Query("where") where: String, @Query("limit") limit: Int): Deferred<ResponseBody>

    @Headers(
        "Content-Type:application/json",
        "X-Bmob-Application-Id:1300daedb47114d5ebc6412a1ac1b9c1",
        "X-Bmob-REST-API-Key:c9ecff6e1b4a7cd653a225b27494d467"
    )
    @GET("https://api2.bmob.cn/1/classes/WebContent")
    fun getWebContentsAsync(@Query("where") where: String, @Query("limit") limit: Int): Deferred<ResponseBody>

    @Headers(
        "Content-Type:application/json",
        "X-Bmob-Application-Id:1300daedb47114d5ebc6412a1ac1b9c1",
        "X-Bmob-REST-API-Key:c9ecff6e1b4a7cd653a225b27494d467"
    )
    @GET("https://api2.bmob.cn/1/classes/FileControl")
    fun getFileControlAsync(@Query("where") where: String, @Query("limit") limit: Int): Deferred<ResponseBody>

    @Headers(
        "Content-Type:application/json",
        "X-Bmob-Application-Id:1300daedb47114d5ebc6412a1ac1b9c1",
        "X-Bmob-REST-API-Key:c9ecff6e1b4a7cd653a225b27494d467"
    )
    @GET("https://api2.bmob.cn/1/classes/ConcurrentEvent")
    fun getEventAsync(@Query("where") where: String, @Query("limit") limit: Int): Deferred<ResponseBody>

    @GET
    fun downloadHtmlZip(@Url url: String): Deferred<ResponseBody>


    @POST(SUBSCRIBE_URL)
    fun subcribeSubmit(@Query("lang") lang: String, @Body body: RequestBody): Deferred<ResponseBody>

}


object CpsApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val regService: RegService by lazy {
        retrofit.create(RegService::class.java)
    }

    val mcService: MyCpsService by lazy {
        clientRetrofit.create(MyCpsService::class.java)
    }

}


