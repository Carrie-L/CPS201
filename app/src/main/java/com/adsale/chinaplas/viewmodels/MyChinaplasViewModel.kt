package com.adsale.chinaplas.viewmodels

import android.os.CountDownTimer
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.network.CHINAPLAS_CLIENT_URL
import com.adsale.chinaplas.network.CpsApi
import com.adsale.chinaplas.network.confirmKeyJson
import com.adsale.chinaplas.network.getTokenJson
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import kotlinx.coroutines.*
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.RequestBody
import java.lang.Exception

/**
 * Created by Carrie on 2019/12/31.
 */
class MyChinaplasViewModel : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main) + job

    var email = MutableLiveData<String>()
    var phoneNo = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var smsCode = MutableLiveData<String>()

    private var _submitStatus = MutableLiveData(0)
    val submitStatus = _submitStatus

    var barClick = MutableLiveData(1)  // 左边1，右边2
    var progressBarVisible = MutableLiveData(false)
    var toDestination = MutableLiveData(false)

    /*  -----------------  MyChinaplas 主页  ------------------------  */
    fun getTokenAsync() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val requestBody = RequestBody.create(
                    MediaType.parse("application/json;charset=UTF-8"),
                    getTokenJson(getMemberId(), getMyChinaplasEmail()))
                val response = CpsApi.regService.apiGeniusEncrypt(requestBody).await().Context
                LogUtil.i("getTokenAsync=$response")
                setToken(response!!)
            }
        }
    }

    /**
     * 未支付没有Guid
     */
    private suspend fun getKeyAsync(guid: String): String {
        return withContext(Dispatchers.IO) {
            val requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"), getKeyAsync(guid))
            val response = CpsApi.regService.apiGeniusEncrypt(requestBody).await().Context
            LogUtil.i("getTokenAsync=$response")
            response!!
        }
    }


    /*  -----------------  MyChinaplas 登录  ------------------------  */

    fun onLeftClick() {
        barClick.value = 1
    }

    fun onRightClick() {
        barClick.value = 2
    }

    fun resetSubmitStatus() {
        _submitStatus.value = 0
        toDestination.value = false
        progressBarVisible.value = false
        barClick.value = 1
    }

    fun onPwdSubmit() {
        val isEmpty = checkEmpty(false)
        i("check=$isEmpty, status = ${submitStatus.value!!}")
        if (!isEmpty) {
            progressBarVisible.value = true
            uiScope.launch {
                val result = postLogin()
                if (result == "2") {
                    submitStatus.value = LOGIN_FAILED
                } else {
                    submitStatus.value = LOGIN_SUCCESS
                    setLoginSuccess(result)
                }
                progressBarVisible.value = false
            }
        }
    }

    private suspend fun postLogin(): String {
        return withContext(Dispatchers.IO) {
            val body = FormBody.Builder()
                .add("Handler", "GeValidateMembership")
                .add("ShowId", "523")
                .add("Exh_Id", "389")
                .add("userEmail", email.value!!)
                .add("userCell", phoneNo.value!!)
                .add("password", password.value!!)
                .add("lang", getLangStr())
                .build()
            val result = CpsApi.regService.postLogin(body).await()
            i("postLogin = ${result}")
            result
        }
    }

    private var guid: String = ""
    fun onSendCode() {
        if (TextUtils.isEmpty(phoneNo.value)) {
            submitStatus.value = PHONE_EMPTY
            return
        }
        uiScope.launch {
            codeCountDown()
            // ① 发送验证码
            val result1 = sendCodeSync()
            i("result1=$result1")
            when (result1) {
                "0" -> submitStatus.value = SMS_CODE_SEND_FAIL_0
                "1" -> submitStatus.value = SMS_CODE_SEND_FAIL
                "2" -> submitStatus.value = SMS_CODE_PHONE_INVALID
                else -> {
                    submitStatus.value = SMS_CODE_SEND_SUCCESS
                    guid = result1
//                    setMyChinaplasGuid(guid)
                }
            }
        }
    }

    fun onSmsSubmit() {
        if (checkEmpty(true)) {
            return
        }
        uiScope.launch {
            // ① 发送验证码
            if (TextUtils.isEmpty(guid)) {
                submitStatus.value = SMS_CODE_INCORRECT
                return@launch
            }
            progressBarVisible.value = true
            // ② 检查验证码
            i("checkCode guid=$guid")
            val result2 = checkCodeSync(guid)
            i("result2=$result2")
            if (!result2) {
                submitStatus.value = SMS_CODE_INCORRECT
                progressBarVisible.value = false
                return@launch
            }
            // ③ 提交
            when (val result3 = postSMSLogin(guid)) {
                "2" -> {
                    submitStatus.value = LOGIN_FAILED
                    progressBarVisible.value = false
                }
                else -> {
                    submitStatus.value = LOGIN_SUCCESS
                    setLoginSuccess(result3)
                }
            }
        }
    }

    private fun setLoginSuccess(memberId: String) {
        setMemberId(memberId)
        progressBarVisible.value = false
        toDestination.value = true
    }

    private suspend fun sendCodeSync(): String {
        return withContext(Dispatchers.IO) {
            val body = FormBody.Builder()
                .add("Handler", "GetCode")
                .add("showid", "523")
                .add("CellNum", phoneNo.value!!)
                .add("RLang", getLangStr())
                .build()
            val result = CpsApi.regService.sendSmsCodeAsync(body).await().string()
            i("sendCode = $result")
            result
        }
    }

    private suspend fun checkCodeSync(guid: String): Boolean {
        i("smsCode=${smsCode.value}")
        return withContext(Dispatchers.IO) {
            val body = FormBody.Builder()
                .add("Handler", "SelectCode")
                .add("Guid", guid)
                .add("Code", smsCode.value!!)
                .add("Url", "Mobile")
                .build()
            try {
                val result = CpsApi.regService.checkSmsCodeAsync(body).await()
                i("sendCode = $result")
                result
            } catch (e: Exception) {
                LogUtil.e(e)
                false
            }
        }
    }

    private suspend fun postSMSLogin(guid: String): String {
        return withContext(Dispatchers.IO) {
            val body = FormBody.Builder()
                .add("Handler", "GeValidateMembershipBySMS")
                .add("ShowId", "523")
                .add("Exh_Id", "389")
                .add("userEmail", email.value!!)
                .add("userCell", phoneNo.value!!)
                .add("Code", smsCode.value!!)
                .add("Guid", guid)
                .add("clienurl", String.format(CHINAPLAS_CLIENT_URL, getLangStr()))
                .build()
            val result = CpsApi.regService.postLogin(body).await()
            i("postLogin = ${result}")
            result
        }
    }

    /**
     * 检查空值和格式。
     * 密码登录： 检查密码，不检查验证码
     * SMS登录：  检查验证码，不检查密码
     */
    private fun checkEmpty(isSmsCheck: Boolean): Boolean {
        if (TextUtils.isEmpty(email.value)) {
            submitStatus.value = EMAIL_EMPTY
            return true
        }
        if (!checkEmail(email.value!!)) {
            submitStatus.value = EMAIL_INVALID
            return true
        }
        if (TextUtils.isEmpty(phoneNo.value)) {
            submitStatus.value = PHONE_EMPTY
            return true
        }
        if (!isSmsCheck && TextUtils.isEmpty(password.value)) {
            submitStatus.value = PWD_EMPTY
            return true
        }
        if (isSmsCheck && TextUtils.isEmpty(smsCode.value)) {
            submitStatus.value = SMS_CODE_EMPTY
            return true
        }
        return false
    }

    private lateinit var timer: CountDownTimer
    var smsCountDownTime = MutableLiveData(0)
    var btnSMSCodeClickable = Transformations.map(smsCountDownTime) {
        it == 0
    }

    /**
     * 验证码倒计时
     */
    private fun codeCountDown() {
        timer = object : CountDownTimer(60000L, 1000L) {  // 60s
            override fun onTick(millisUntilFinished: Long) {   //  millisUntilFinished / ONE_SECOND = 4,3,2,1,0
                smsCountDownTime.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {}
        }
        timer.start()
    }

}