package com.adsale.chinaplas.network

import com.adsale.chinaplas.data.entity.RegKey
import com.adsale.chinaplas.data.entity.RegPayStatus
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by Carrie on 2019/11/18.
 * 预登记网络请求
 */

interface RegService {

    @POST("https://e.adsale.com.hk/vreg/services/MyChinaplasLoginVerify.ashx?showcode=cps20")
    fun PreregAccountExists(@Query("cell") phoneNo: String, @Query("email") email: String): Deferred<ResponseBody>


    /**
     * 此链接给用户发送验证码，如果发送成功，返回guid
     */
    @POST("https://eform.adsale.com.hk/vreg/Content/handler/MobileCode.ashx?Handler=GetCode&showid=523")
    fun PreregSMSGuid(@Query("RLang") lang: String, @Query("CellNum") phoneNo: String): Deferred<ResponseBody>

    /**
     * 检查验证码是否正确
     */
    @POST("https://eform.adsale.com.hk/vreg/Content/handler/MobileCode.ashx?Handler=SelectCode&Url=")
    fun PreregSMSCheckCode(@Query("Guid") guid: String, @Query("Code") code: String): Deferred<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("https://prereg.adsaleonline.com/Prereg/PreregSubmitSMS/PreregOnCloudSubmit")
    fun PreregOnCloudSubmit(@Body body: RequestBody): Deferred<ResponseBody>


    @Headers("Content-Type:application/json")
    @POST("https://prereg.adsaleonline.com/prereg/PreregSubmitSMS/CheckInsertSuccess")
    fun PreregCheckInsert(@Body body: RequestBody): Deferred<ResponseBody>


    @Headers("Content-Type:application/json")
    @POST(REG_CONFIRM_KEY_API)
    fun PreregConfirmKey(@Body body: RequestBody): Deferred<RegKey>

//    @GET(REG_CONFIRM_LATTER_URL)
//    fun RegregConfirmPDF(@Query("encryptKey") encryptKey: String): Deferred<ResponseBody>

    @Streaming
    /* 大文件需要加入这个判断，防止下载过程中写入到内存中 */
    @GET
    fun largeDownload(@Url url: String?): Deferred<ResponseBody?>


    @Headers("Content-Type:application/json")
    @POST(REGISTER_CHARGE)
    fun PreregCharge(@Body body: RequestBody): Deferred<ResponseBody>


    @POST(REGISTER_CONFIRM_PAY)
    fun PreregPaySuccess(@Body body: RequestBody?): Deferred<RegPayStatus>


    /*  ===================== MyChinaplas 登录 ============= */
    @POST(LOGIN_URL)
    fun postLogin(@Body body: RequestBody): Deferred<String>

    @POST(LOGIN_SEND_SMS_CODE_URL)
    fun sendSmsCodeAsync(@Body body: RequestBody): Deferred<ResponseBody>

    @POST(LOGIN_CHECK_SMS_CODE_URL)
    fun checkSmsCodeAsync(@Body body: RequestBody): Deferred<Boolean>

    @Headers("Content-Type:application/json")
    @POST(GENIUS_HELPER_ENCRYPT)
    fun apiGeniusEncrypt(@Body body: RequestBody): Deferred<RegKey>


}
