package com.adsale.chinaplas.network

import com.adsale.chinaplas.data.entity.RegKey
import com.adsale.chinaplas.data.entity.RegPayStatus
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * MyChinaplas
 *
 */

interface MyCpsService {

    @POST(LOGIN_URL)
    fun postLogin(@Body body: RequestBody): Deferred<String>

    @POST(LOGIN_SEND_SMS_CODE_URL)
    fun sendSmsCodeAsync(@Body body: RequestBody): Deferred<ResponseBody>

    @POST(LOGIN_CHECK_SMS_CODE_URL)
    fun checkSmsCodeAsync(@Body body: RequestBody): Deferred<Boolean>

    @Headers("Content-Type:application/json")
    @POST(GENIUS_HELPER_ENCRYPT)
    fun apiGeniusEncrypt(@Body body: RequestBody): Deferred<RegKey>

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST  //VISITOR_LIST_URL
    fun getVisitorListAsync(@Url url: String): Deferred<ResponseBody>

    @POST
    fun getInvoiceUrlAsync():Deferred<ResponseBody>

}
