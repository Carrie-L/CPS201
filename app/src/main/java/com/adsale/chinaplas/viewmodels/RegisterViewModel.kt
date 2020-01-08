package com.adsale.chinaplas.viewmodels

import android.os.CountDownTimer
import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.RegOptionData
import com.adsale.chinaplas.data.dao.RegisterRepository
import com.adsale.chinaplas.data.entity.RegSubmitResponse
import com.adsale.chinaplas.mSPReg
import com.adsale.chinaplas.network.CpsApi
import com.adsale.chinaplas.network.checkInsertJson
import com.adsale.chinaplas.network.submitJson
import com.adsale.chinaplas.utils.*
import kotlinx.coroutines.*
import okhttp3.RequestBody

/**
 * Created by Carrie on 2019/11/4.
 */
class RegisterViewModel(val regRepo: RegisterRepository) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private var titles = MutableLiveData<MutableList<RegOptionData>>()
    private var titleEntity = MutableLiveData<RegOptionData>()
    private var functionEntity = MutableLiveData<RegOptionData>()

    var companyProduct = MutableLiveData<String>("")  // 主营产品
    val serviceOtherCar = MutableLiveData<String>()
    val serviceOtherPackage = MutableLiveData<String>()
    val serviceOtherCosme = MutableLiveData<String>()
    val serviceOtherBuild = MutableLiveData<String>()
    val serviceOtherEE = MutableLiveData<String>()
    val serviceOtherMedical = MutableLiveData<String>()
    val serviceOtherLED = MutableLiveData<String>()
    val serviceOtherText = MutableLiveData<String>()
    val service = MutableLiveData<String>()   // groupCode|DetailCode

    var name = MutableLiveData<String>()
    var company = MutableLiveData<String>()
    var gender = MutableLiveData(REG_FEMALE)
    private var titleCode = MutableLiveData<String>("")
    var titleText = MutableLiveData<String>("")
    private var functionCode = MutableLiveData<String>("")
    var functionText = MutableLiveData<String>("")
    var titleOther = MutableLiveData<String>("")
    var functionOther = MutableLiveData<String>("")
    var email = MutableLiveData<String>("")
    var email2 = MutableLiveData<String>("")
    var pwd = MutableLiveData<String>("")
    var pwd2 = MutableLiveData<String>("")
    //    var countryCode = MutableLiveData<String>("")  // 国家/地区
//    var provinceCode = MutableLiveData<String>("")  // 国家/地区
//    var cityCode = MutableLiveData<String>("")  // 国家/地区
    var tellAreaCode = MutableLiveData<String>("")   // 区号，没有可为0
    var tellNo = MutableLiveData<String>("")  // 电话号码
    var tellExt = MutableLiveData<String>("")  // 分机
    var mobileAreaCode = MutableLiveData<String>("")  // 手机区号。没有可为0
    var mobileNo = MutableLiveData<String>("")  // 手机号
    var smsCode = MutableLiveData<String>("")  // 手机验证码。当为中国大陆时
    var postChecked = ObservableBoolean(false)
    var postCity = MutableLiveData<String>("")  //  邮寄城市
    var postcode = MutableLiveData<String>("")  // 邮编
    var postAddress1 = MutableLiveData<String>("")  // 地址栏1
    var postAddress2 = MutableLiveData<String>("")  // 地址栏2
    var regionText = MutableLiveData<String>("")  // 国家/地区  combine文字
    var tellRegionCode = MutableLiveData<String>("")  // 国家/地区号
    var cnMobileVisible = Transformations.map(tellRegionCode) {
        it == "86"
    }

    //    var btnSMSCodeClickable = MutableLiveData<Boolean>()  // 获取验证码按钮 是否可点击
    private lateinit var timer: CountDownTimer
    private lateinit var checkTimer: CountDownTimer
    var smsCountDownTime = MutableLiveData(0)
    var smsGuid: String = ""
    var btnSMSCodeClickable = Transformations.map(smsCountDownTime) {
        it == 0
    }
    var progressBarVisible = MutableLiveData(false)
    var isSMSCodeCorrect = MutableLiveData(-1)
    var isSubmitDataSuccess = MutableLiveData(-1)  // 0 失败，1 成功
    var payUrl: String? = ""
    var btnSubmitClicked = MutableLiveData(false)
    var btnConfirmClicked = MutableLiveData(-1)   // 0 back， 1 confirm
    var isPwdVisible1 = MutableLiveData(false)
    var isPwdVisible2 = MutableLiveData(false)

    init {
        name.value = (getName())
        company.value = getCompany()
        gender.value = getGender()
        titleCode.value = getTitleCode()
        titleOther.value = getTitleOther()
        functionCode.value = getFunctionCode()
        functionOther.value = getFunctionOther()
        companyProduct.value = getRegProduct()
        serviceOtherCar.value = getServiceOther(REG_SERVICE_OTHER_CODE_CAR)
        serviceOtherPackage.value = getServiceOther(REG_SERVICE_OTHER_CODE_PACKAGE)
        serviceOtherBuild.value = getServiceOther(REG_SERVICE_OTHER_CODE_BUILD)
        serviceOtherCosme.value = getServiceOther(REG_SERVICE_OTHER_CODE_COSME)
        serviceOtherLED.value = getServiceOther(REG_SERVICE_OTHER_CODE_LED)
        serviceOtherMedical.value = getServiceOther(REG_SERVICE_OTHER_CODE_MEDICAL)
        serviceOtherEE.value = getServiceOther(REG_SERVICE_OTHER_CODE_EE)
        serviceOtherText.value = getServiceOther(REG_SERVICE_OTHER_CODE_TEXT)

        regionText.value = getRegionCombineText()
        tellRegionCode.value = getTellData(1)
        tellAreaCode.value = getTellData(REG_TELL_AREA_CODE_INDEX)
        tellNo.value = getTellData(REG_TELL_NO_INDEX)
        tellExt.value = getTellData(REG_TELL_EXT_INDEX)
        mobileAreaCode.value = getTellData(REG_MOBILE_AREA_CODE_INDEX)
        mobileNo.value = getTellData(REG_MOBILE_NO)
        email2.value = getEmail(2)
        pwd.value = getPassword(1)
        pwd2.value = getPassword(2)
        postChecked.set(getIsPostChecked())
        postcode.value = getPostAddress(0)
        postCity.value = getPostCity()
        postAddress1.value = getPostAddress(1)
        postAddress2.value = getPostAddress(2)

        LogUtil.i("postChecked=${postChecked.get()}")



        uiScope.launch {
            titleText.value?.let {
                getTitleEntity(it)
            }
            functionText.value?.let {
                getFunctionEntity(it)
            }
        }


        LogUtil.i("init RegisterViewModel!!!!!! name = ${getName()}")
        LogUtil.i("init RegisterViewModel!!!!!! getCompany = ${getCompany()}")
        LogUtil.i("init RegisterViewModel!!!!!! getGender = ${getGender()}")


    }

    fun getTitleEntity(text: String) {
        uiScope.launch {
            titleEntity.value = getTitleFromDB(text)
            titleCode.value = titleEntity.value?.DetailCode
        }
    }

    fun getFunctionEntity(text: String) {
        uiScope.launch {
            functionEntity.value = getFunctionFromDB(text)
            functionCode.value = functionEntity.value?.DetailCode
        }
    }

    val titleItemClickListener = OnItemClickListener { item, position ->
        item as RegOptionData
        item.isChecked.set(true)
        titles.value!![position] = item
    }

    private suspend fun getTitleFromDB(text: String): RegOptionData {
        return withContext(Dispatchers.IO) {
            regRepo.getTitle(text)
        }
    }

    private suspend fun getFunctionFromDB(text: String): RegOptionData {
        return withContext(Dispatchers.IO) {
            regRepo.getFunction(text)
        }
    }

    fun onSendSmsCode() {
        if (!TextUtils.isEmpty(mobileNo.value)) {
            codeCountDown()
            uiScope.launch {
                val getDeferred = CpsApi.regService.PreregSMSGuid(getLangCode(), mobileNo.value!!)
                val responseBody = getDeferred.await()
                smsGuid = responseBody.string()
                responseBody.close()
                LogUtil.i("guid = $smsGuid")
            }
        }
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

    fun stopCountDown() {
        timer.cancel()
    }

    fun setServiceOtherValue(code: String, text: String) {
        when (code) {
            "2008" -> serviceOtherCar.value = text
            "2016" -> serviceOtherPackage.value = text
            "2022" -> serviceOtherCosme.value = text
            "2032" -> serviceOtherEE.value = text
            "2038" -> serviceOtherMedical.value = text
            "2045" -> serviceOtherBuild.value = text
            "2049" -> serviceOtherLED.value = text
            "2066" -> serviceOtherText.value = text
        }
        setServiceOther(code, text)
        LogUtil.i("setServiceOtherValue:code=$code,text=$text")
    }

    /**
     *  没有返回guid?
     *  没有返回支付链接？
     *  插入不成功？
     */
    fun apiSubmit() {
        progressBarVisible.value = true
        uiScope.launch {
            if (checkSMS()) {
                LogUtil.i("验证码正确~~~")
                isSMSCodeCorrect.value = 1

                val rid = apiGetRid()
                if (rid != 0) {
                    netCheckInsertPost(rid)   // 再次请求验证是否插入成功
                } else {
                    progressBarVisible.value = false
                    isSubmitDataSuccess.value = 0
                }
            } else {
                LogUtil.e("验证码不正确")
                progressBarVisible.value = false
                isSMSCodeCorrect.value = 0
            }
        }
    }

    fun onSubmit() {
        saveFiledToSP()
        btnSubmitClicked.value = true
    }

    /**
     * 检查验证码是否正确
     */
    private suspend fun checkSMS(): Boolean {
        if (!cnMobileVisible.value!!) {
            return true
        }
        val getDeferred = CpsApi.regService.PreregSMSCheckCode(smsGuid, smsCode.value ?: "")
        try {
            val result = getDeferred.await().string()
            return result == "True"
        } catch (e: java.lang.Exception) {
            LogUtil.e("checkSMS::Failure: ${e.message} ")
        }
        return false
    }

    private suspend fun apiGetRid(): Int {
        mSPReg.edit().putString("json", getSumitJson()).apply()
        val requestBody = RequestBody.create(
            okhttp3.MediaType.parse("application/json;charset=UTF-8"), getSumitJson()
        )
        val responseBody = CpsApi.regService.PreregOnCloudSubmit(requestBody).await()
        val response = responseBody.string()
        LogUtil.i("netGetRid:response=$response")

        val resultEntity: RegSubmitResponse = parseJson(RegSubmitResponse::class.java, response)!!
        val rid = resultEntity.PrereSubmitModel.Rid
        val isSuccessful = resultEntity.Validation.IsSuccessful
        responseBody.close()
        LogUtil.i("netGetRid:resultEntity=$resultEntity")

        if (isSuccessful && !TextUtils.isEmpty("$rid")) {
            return rid
        }
        return 0
    }

    /**
     * 检查提交的数据是否插入服务器数据库了
     */
    var checkCount = 0

    private suspend fun netCheckInsertPost(rid: Int): Boolean {
        val checkJson = checkInsertJson(rid, getLangCode())
        val requestBody =
            RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), checkJson)
        val checkDeferred = CpsApi.regService.PreregCheckInsert(requestBody)
        val result = checkDeferred.await().string()

        val resultEntity: RegSubmitResponse = parseJson(RegSubmitResponse::class.java, result)!!
        LogUtil.i("resultEntity2=$resultEntity")
        val isSuccess = resultEntity.Validation.IsSuccessful
        val guid = resultEntity.PrereSubmitModel.Guid
        if (isSuccess && !TextUtils.isEmpty(guid)) {
            LogUtil.i("  chechInsertPost true ")
            payUrl = resultEntity.Validation.Url
            isNetSubmitSuccess(true)
            return true
        }
        LogUtil.i("  chechInsertPost false  again--- ")
        // 3s后重复请求
        if (checkCount < 7) {   // 超过7次后不再请求
            checkCountDown(rid)
        } else {
            LogUtil.e("+++++++++++请求插入失败！！！")
            isNetSubmitSuccess(false)
        }
        return false
    }

    private fun isNetSubmitSuccess(bool: Boolean) {
        progressBarVisible.value = false
        isSubmitDataSuccess.value = if (bool) 1 else 0
    }

    /**
     * 重复请求倒计时
     */
    private fun checkCountDown(rid: Int) {
        checkTimer = object : CountDownTimer(3000L, 1000L) {  // 60s
            override fun onTick(millisUntilFinished: Long) {   //  millisUntilFinished / ONE_SECOND = 4,3,2,1,0
                LogUtil.i("checkCountDown: ${millisUntilFinished / 1000}")
            }

            override fun onFinish() {
                uiScope.launch {
                    checkCount++
                    LogUtil.i(" checkInsertPost do  again/// ")
                    netCheckInsertPost(rid)
                }
            }
        }
        checkTimer.start()
    }

    fun onDialogBack() {
        btnConfirmClicked.value = 0
    }

    fun onDialogConfirm() {
        btnConfirmClicked.value = 1
    }

    fun onPwdSee(index: Int) {
        when (index) {
            1 -> isPwdVisible1.value = !isPwdVisible1.value!!
            2 -> isPwdVisible2.value = !isPwdVisible2.value!!
        }
    }


    private fun getSumitJson(): String {
        LogUtil.i("service=$service")
        LogUtil.i("tellRegionCode=${tellRegionCode.value}")
        LogUtil.i("countryCode=${getRegion()}")
        LogUtil.i("provinceCode=${getProvince()}")
        LogUtil.i("cityCode=${getCity()}")
        LogUtil.i("tellAreaCode=${tellAreaCode.value}")
        LogUtil.i("mobileAreaCode=${mobileAreaCode.value}")
        LogUtil.i("tellNo=${tellNo.value}")
        LogUtil.i("mobileNo=${mobileNo.value}")


        return submitJson(
            smsGuid,
            mobileNo.value ?: "",
            email2.value ?: "",
            email2.value ?: "",
            gender.value ?: "",
            name.value ?: "",
            company.value ?: "",
            titleCode.value ?: "",
            titleOther.value ?: "",
            functionCode.value ?: "",
            functionOther.value ?: "",
            companyProduct.value ?: "",
            service.value ?: "",
            serviceOtherCar.value ?: "",
            serviceOtherPackage.value ?: "",
            serviceOtherCosme.value ?: "",
            serviceOtherBuild.value ?: "",
            serviceOtherEE.value ?: "",
            serviceOtherMedical.value ?: "",
            serviceOtherLED.value ?: "",
            serviceOtherText.value ?: "",
            getRegion(),
            getProvince(),
            getCity(),
            tellRegionCode.value ?: "",
            tellAreaCode.value ?: "",
            tellNo.value ?: "",
            tellExt.value ?: "",
            mobileAreaCode.value ?: "",
            pwd.value ?: "",
            pwd2.value ?: "",
            postcode.value ?: "0",
            getPostCity(),
            postAddress1.value ?: "",
            postAddress2.value ?: "",
            smsCode.value ?: ""
        )
    }

    /**
     * 把值存储下来
     */
    fun saveFiledToSP() {
        setName(name.value ?: "")  // 保存名字
        // 保存公司
        setCompany(company.value ?: "")
        setGender(gender.value ?: "")
        setTitleText(titleText.value ?: "")
        setTitleCode(titleCode.value ?: "")
        setTitleOther(titleOther.value ?: "")
        setFunctionText(functionText.value ?: "")
        setFunctionCode(functionCode.value ?: "")
        setFunctionOther(functionOther.value ?: "")
        setRegProduct(companyProduct.value ?: "")
        setTellData(REG_TELL_AREA_CODE_INDEX, tellAreaCode.value ?: "")
        setTellData(REG_TELL_NO_INDEX, tellNo.value ?: "")
        setTellData(REG_TELL_EXT_INDEX, tellExt.value ?: "")
        setTellData(REG_MOBILE_AREA_CODE_INDEX, mobileAreaCode.value ?: "")
        setTellData(REG_MOBILE_NO, mobileNo.value ?: "")
        setEmail(1, email.value ?: "")
        setEmail(2, email2.value ?: "")
        setPassword(1, pwd.value ?: "")
        setPassword(2, pwd2.value ?: "")
        setIsPostChecked(postChecked.get() ?: false)
        setPostAddress(0, postcode.value ?: "")
        setPostAddress(1, postAddress1.value ?: "")
        setPostAddress(2, postAddress2.value ?: "")
        setPostCity(postCity.value ?: "")


        LogUtil.i("saveFiledToSP: name=${name.value ?: ""}")
        LogUtil.i("saveFiledToSP: company=${company.value ?: ""}")
        LogUtil.i("saveFiledToSP: companyProduct=${companyProduct.value ?: ""}")
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()

        LogUtil.i("---onCleared--- RegisterViewModel")

    }

}