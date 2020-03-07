package com.adsale.chinaplas.viewmodels

import android.os.CountDownTimer
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.data.entity.VisitorData
import com.adsale.chinaplas.network.*
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import kotlinx.coroutines.*
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.RequestBody

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

    var invoiceUrl = MutableLiveData<String>()

    /*  -----------------  MyChinaplas 主页  ------------------------  */
    /**
     * 获取token
     */
    fun getTokenAsync() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val requestBody = RequestBody.create(
                    MediaType.parse("application/json;charset=UTF-8"),
                    getTokenJson(getMemberId(), getMyChinaplasEmail()))
                try {
                    val response = CpsApi.mcService.apiGeniusEncrypt(requestBody).await().Context
                    i("getTokenAsync=$response")
                    if (response != null) {
                        setToken(response)
                        getUserInfo()

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    LogUtil.e(e)
                    i(e.stackTrace.toString())
                }
            }
        }
    }

    /**
     * 获取基本资料
     */
    private suspend fun getUserInfo(): Boolean {
        withContext(Dispatchers.IO) {
            try {
                val url = String.format(VISITOR_LIST_URL, getToken(), getLangCode())
                i("url=$url")
                val response = CpsApi.mcService.getVisitorListAsync(url).await()
                val content = response.string()
                i("getUserInfo=$content")
                val visitorData = parseJson(VisitorData::class.java, content)
                i("\nvisitorData=${visitorData.toString()}")
                val entity = visitorData?.Data?.get(0)
                if (entity != null) {
                    setMyChinaplasGuid(entity.Guid!!)
                    setVisitorId(entity.VisitorId!!)
                    setMyChinaplasIsPay(entity.Paid)
                    setInvoicePdfUrl(entity.PdfUrl!!)
                    // 保存预登记数据
                    resetRegisterFormData()
                    setName(entity.ContactPerson!!)
                    setCompany(entity.Company!!)
                    setGender(entity.SalCode!!)
                    setTellData(1, entity.TelCountry!!)
                    setTellData(2, entity.TelArea!!)
                    setTellData(3, entity.TelNo!!)
                    setTellData(4, entity.TelExt!!)
                    setTellData(5, entity.CellArea!!)
                    setTellData(6, entity.CellNo!!)
                    setEmail(1, entity.Email!!)

                    LogUtil.i("value =${entity.toString()} ")
                    uiScope.launch {
                        LogUtil.i("value =LOGIN_SUCCESS ")
                        submitStatus.value = LOGIN_SUCCESS
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.e(e)
                i(e.stackTrace.toString())
            }
        }
        return false
    }

    private suspend fun getInvoiceUrlAsync() {
        withContext(Dispatchers.IO) {
            try {
                val requestBody = RequestBody.create(
                    MediaType.parse("application/json;charset=UTF-8"), confirmKeyJson(getMyChinaplasGuid()))
                val regKey = CpsApi.regService.PreregConfirmKey(requestBody).await()
                i("regKey=$regKey")
                val invoiceUrl = String.format(INVOICE_URL, getLangStr(), regKey.Context)
                i("invoiceUrl=$invoiceUrl")
                setInvoiceUrl(invoiceUrl)
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.e(e)
                i(e.stackTrace.toString())
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
            val response = CpsApi.mcService.apiGeniusEncrypt(requestBody).await().Context
            i("getTokenAsync=$response")
            response!!
        }
    }

    /*  -----------------   ------------------------  */


    /*  -----------------  MyChinaplas 登录  ------------------------  */

    fun onLeftClick() {
        barClick.value = 1
    }

    fun onRightClick() {
        barClick.value = 2
    }

    fun resetSubmitStatus() {
        barClick.value = 0
        _submitStatus.value = 0
        toDestination.value = false
        progressBarVisible.value = false
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
                    progressBarVisible.value = false
                } else {
                    // 登录成功了，获取 VisitorListData
                    setMemberId(result)
                    getTokenAsync()
                }
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
            val result = CpsApi.mcService.postLogin(body).await()
            i("postPwdLogin = ${result}")
            result
        }
    }

    private var smsGuid: String = ""
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
                    smsGuid = result1
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
            if (TextUtils.isEmpty(smsGuid)) {
                submitStatus.value = SMS_CODE_INCORRECT
                return@launch
            }
            progressBarVisible.value = true
            // ② 检查验证码
            i("checkCode guid=$smsGuid")
            val result2 = checkCodeSync(smsGuid)
            i("result2=$result2")
            if (!result2) {
                submitStatus.value = SMS_CODE_INCORRECT
                progressBarVisible.value = false
                return@launch
            }
            // ③ 提交
            when (val result3 = postSMSLogin(smsGuid)) {
                "2" -> {
                    submitStatus.value = LOGIN_FAILED
                    progressBarVisible.value = false
                    i("result3 login fail")
                }
                else -> {
                    setMemberId(result3)
                    getTokenAsync()
                    i("result3 id=$result3")
                }
            }
        }
    }

    private suspend fun sendCodeSync(): String {
        return withContext(Dispatchers.IO) {
            val body = FormBody.Builder()
                .add("Handler", "GetCode")
                .add("showid", "523")
                .add("CellNum", phoneNo.value!!)
                .add("RLang", getLangCode())
                .build()
            val result = CpsApi.mcService.sendSmsCodeAsync(body).await().string()
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
                val result = CpsApi.mcService.checkSmsCodeAsync(body).await()
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
            val result = CpsApi.mcService.postLogin(body).await()
            i("postSMSLogin = ${result}")
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
        if (!isSmsCheck && !TextUtils.isEmpty(password.value) && password.value!!.length < 8) {
            submitStatus.value = PWD_INVALID
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