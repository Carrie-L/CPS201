package com.adsale.chinaplas.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.os.LocaleList
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.adsale.chinaplas.R
import com.adsale.chinaplas.mResources
import com.adsale.chinaplas.mSPConfig
import com.adsale.chinaplas.mSPReg
import com.adsale.chinaplas.utils.LogUtil.i
import com.github.promeg.pinyinhelper.Pinyin
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Carrie on 2019/10/12.
 */


fun getScreenWidth(): Int {
    return mSPConfig.getInt("ScreenWidth", 0)
}

fun getScreenHeight(): Int {
    return mSPConfig.getInt("ScreenHeight", 0)
}

fun getDisplayHeight(): Int {
    return mSPConfig.getInt(DISPLAY_HEIGHT, 0)
}

fun setScreenSize(sp: SharedPreferences, width: Int, height: Int) {
    sp.edit()
        .putInt("ScreenWidth", width)
        .putInt("ScreenHeight", height).apply()
}

fun getBottomNavHeight(): Int {
    return mSPConfig.getInt("NAV_BAR_HEIGHT", 0)
}

fun getPadHeightRate(): Float {
    return mSPConfig.getFloat("PadHeightRate", 0f)
}

fun getPadWidthRate(): Float {
    return mSPConfig.getFloat("PadWidthRate", 0f)
}

fun getMainHeaderHeight(): Int {
    return mSPConfig.getInt("main_header_height", 0)
}

fun setMainMenuHeight(height: Int) {
    mSPConfig.edit().putInt("main_menu_height", height).apply()
}

fun getMainMenuHeight(): Int {
    return mSPConfig.getInt("main_menu_height", 0)
}

fun isFirstRunning(): Boolean {
    return mSPConfig.getBoolean("isFirstRunning", true)
}

fun setNotFirstRunning() {
    mSPConfig.edit().putBoolean("isFirstRunning", false).apply()
}

fun setCurrLanguage(lang: Int) {
    mSPConfig.edit().putInt("current_language", lang).apply()
}

fun getCurrLanguage(): Int {
    return mSPConfig.getInt("current_language", -1)
}

fun getLocale(language: Int): Locale? {
    return when (language) {
        1 -> Locale.US
        2 -> Locale.SIMPLIFIED_CHINESE
        else -> Locale.TRADITIONAL_CHINESE
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun updateResources(context: Context): Context? {
    val resources = context.resources
    val locale: Locale =
        getLocale(
            context.getSharedPreferences(SP_CONFIG, Context.MODE_PRIVATE).getInt(
                "current_language",
                LANG_TC
            )
        )!!
    val configuration = resources.configuration
    configuration.setLocale(locale)
    val localeList = LocaleList(locale)
    LocaleList.setDefault(localeList)
    configuration.setLocales(localeList)
    return context.createConfigurationContext(configuration)
}

/**
 * 获取系统的locale
 *
 * @return Locale对象
 */
fun getSystemLocale(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        LocaleList.getDefault().get(0)
    } else {
        Locale.getDefault()
    }
}

///**
// * @param mContext
// * @param language 0:ZhTw; 1:en;2:ZhCn;
// */
//private fun switchLanguage(mContext: Context, language: Int) {
//    LogUtil.i("switchLanguage=$language")
//    setCurrLanguage(language)
//    val resources = mContext.resources
//    val config = resources.configuration
//    val dm = resources.displayMetrics
//    val locale: Locale = getLocale(language)!!
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//        val localeList = LocaleList(locale)
//        LocaleList.setDefault(localeList)
//        config.setLocales(localeList)
//        mContext.createConfigurationContext(config)
//    } else {
//        config.setLocale(locale)
//    }
//    Locale.setDefault(locale)
//    resources.updateConfiguration(config, dm)
//}

fun switchLanguage(mContext: Context, language: Int) {
    setCurrLanguage(language)
    val resources = mContext.resources
    val config = resources.configuration
    val dm = resources.displayMetrics
    val locale: Locale = getLocale(language)!!
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        config.setLocales(localeList)
        mContext.createConfigurationContext(config)
    } else {
        config.setLocale(locale)
    }
    Locale.setDefault(locale)
    resources.updateConfiguration(config, dm)
}

fun setIsTablet(bool: Boolean) {
    mSPConfig.edit().putBoolean("isTablet", bool).apply()
}

fun isTablet(): Boolean {
    return mSPConfig.getBoolean("isTablet", false)
}

fun setCountDownFinish() {
    mSPConfig.edit().putBoolean("countDownFinish", true).apply()
}

fun getCountDownFinish(): Boolean {
    return mSPConfig.getBoolean("countDownFinish", false)
}

fun setIntentImmediately() {
    mSPConfig.edit().putBoolean("IntentImmediately", true).apply()
}

fun getIntentImmediately(): Boolean {
    return mSPConfig.getBoolean("IntentImmediately", false)
}

/**
 * 开展倒计时天数
 */
fun getShowCountDown(): Long {
    var diff: Long = 0
    val today = getTodayDate()
    val showStartDate = SHOW_DATE_1ST //yyyy-MM-dd
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    try {
        val date0 = sdf.parse(today)
        val date1 = sdf.parse(showStartDate)
        diff = (date1!!.time - date0!!.time) / (1000 * 60 * 60 * 24) - 1
        LogUtil.i("diff=$diff")
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return diff
}

/**
 * 获取今天的日期
 * @return String yyyy-MM-dd
 */
fun getTodayDate(): String {
    val sformat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sformat.format(Calendar.getInstance().time)
}

/**
 * 给时间增加1s
 */
fun timeAddOneSecond(time: String): String {
    LogUtil.i("before: $time")
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = format.parse(time)
    date.seconds++
    LogUtil.i("after: ${format.format(date)}")
    return format.format(date)
}

fun getCurrentTime(): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = Calendar.getInstance().time
    return format.format(date)
}

/**
 * 获取昨天的日期
 *
 * @return String yyyy-MM-dd
 */
fun getYesterdayDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, -1)
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        .format(calendar.time)
}

fun dateToString(date: Date?): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    return sdf.format(date)
}

fun stringToDate(time: String?): Date? {
    val sdf = SimpleDateFormat("HH:mm", Locale.CHINA)
    try {
        return sdf.parse(time)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return Date()
}

fun timeToString(date: Date?): String? {
    val sdf = SimpleDateFormat("HH:mm", Locale.CHINA)
    return sdf.format(date)
}


/**
 * 比较三个date的值  ,是否 date1 < date < date2
 * ad.txt
 *
 * @param date  current date
 * @param date1 open date
 * @param date2 close date
 * @return
 */
fun compareDate(
    date: String,
    date1: String,
    date2: String
): Boolean {
    val sformat =
        SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
    try {
        val dt = sformat.parse(date)
        val dt1 = sformat.parse(date1)
        val dt2 = sformat.parse(date2)
        val l0 = dt!!.time
        val l1 = dt1!!.time
        val l2 = dt2!!.time
        i("dt=$dt, dt1=$dt1,dt2=$dt2")
        i("lo=$l0, l1=$l1,l2=$l2")
        if (dt.before(dt2) && dt.after(dt1)) {
            i("compareDate：  在中间")
        }
        if (l0 in l1..l2) {
            i("compareDate：  在中间 2 ")
            return true
        }
        var max = l2
        var min = l1
        if (l1 > l2) {
            max = l1
            min = l2
        }
        if (l0 in (min + 1) until max) {
            return true
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
    return false
}

@SuppressLint("DefaultLocale")
fun getFirstChar(str: String): String {
    val c = Pinyin.toPinyin(str.substring(0), "")[0]
    return (c + "").toUpperCase()
}

fun replaceBmobResult(content: String): String {
    return content.replace("{\"results\":", "").substringBeforeLast("}", "")
}


/*  ------------------------------ SP  Reg  预登记资料信息 ------------------------------*/
/*  预登记注册信息 */
fun setGuid(guid: String) {
    mSPReg.edit().putString("guid", guid).apply()
}

fun getGuid(): String {
    return mSPReg.getString("guid", "")!!
}

/*  确认信pdf key */
fun saveConfirmKey(key: String) {
    mSPReg.edit().putString("confirm_key", key).apply()
}

fun getConfirmKey(): String {
    return mSPReg.getString("confirm_key", "")!!
}

/*  确认信pdf 下载链接 */
fun saveConfirmPdfUrl(url: String) {
    mSPReg.edit().putString("confirm_pdf_url", url).apply()
}

fun getConfirmPdfUrl(): String {
    return mSPReg.getString("confirm_pdf_url", "")!!
}

/**
 * 支付成功
 */
fun paySuccess(bool: Boolean) {
    mSPReg.edit().putBoolean("pay_success", bool).apply()
}

fun isPaySuccess(): Boolean {
    return mSPReg.getBoolean("pay_success", false)
}

fun putLogin(bool: Boolean) {
    mSPReg.edit().putBoolean("isLogin", bool).apply()
}

fun isLogin(): Boolean {
    return mSPReg.getBoolean("isLogin", false)
}

fun setName(name: String) {
    mSPReg.edit().putString(REG_FORM_NAME, name).apply()
}

fun getName(): String {
    return mSPReg.getString(REG_FORM_NAME, "")!!
}


fun setGender(gender: String) {
    mSPReg.edit().putString(REG_FORM_GENDER, gender).apply()
}

fun getGender(): String {
    return mSPReg.getString(REG_FORM_GENDER, "") ?: "MR"
}

fun setCompany(company: String) {
    mSPReg.edit().putString(REG_FORM_COMPANY, company).apply()
}

fun getCompany(): String {
    return mSPReg.getString(REG_FORM_COMPANY, "")!!
}

/**
 * @param str : DetailCode
 */
fun setTitleCode(str: String) {
    mSPReg.edit().putString(REG_FORM_TITLE_CODE, str).apply()
}

fun getTitleCode(): String {
    return mSPReg.getString(REG_FORM_TITLE_CODE, "")!!
}

fun setTitleText(str: String) {
    mSPReg.edit().putString(REG_FORM_TITLE, str).apply()
}

fun getTitleText(): String {
    return mSPReg.getString(REG_FORM_TITLE, "")!!
}

fun setTitleOther(str: String) {
    mSPReg.edit().putString(REG_FORM_TITLE_OTHER, str).apply()
}

fun getTitleOther(): String {
    return mSPReg.getString(REG_FORM_TITLE_OTHER, "")!!
}

/**
 * @param str : DetailCode
 */
fun setFunctionCode(str: String) {
    mSPReg.edit().putString(REG_FORM_FUNCTION_CODE, str).apply()
}

fun getFunctionCode(): String {
    return mSPReg.getString(REG_FORM_FUNCTION_CODE, "")!!
}

fun setFunctionOther(str: String) {
    mSPReg.edit().putString(REG_FORM_FUNCTION_OTHER, str).apply()
}

fun getFunctionOther(): String {
    return mSPReg.getString(REG_FORM_FUNCTION_OTHER, "")!!
}

fun setFunctionText(str: String) {
    mSPReg.edit().putString(REG_FORM_FUNCTION, str).apply()
}

fun getFunctionText(): String {
    return mSPReg.getString(REG_FORM_FUNCTION, "")!!
}

/**
 * text 主营产品
 */
fun setRegProduct(str: String) {
    mSPReg.edit().putString(REG_FORM_PRODUCT, str).apply()
}

fun getRegProduct(): String {
    return mSPReg.getString(REG_FORM_PRODUCT, "")!!
}

fun setRegServiceCode(str: String) {
    mSPReg.edit().putString(REG_FORM_SERVICE, str).apply()
}

fun getRegServiceCode(): String {
    return mSPReg.getString(REG_FORM_SERVICE, "")!!
}

fun setServiceOther(code: String, text: String) {
    when (code) {
        "2008" -> mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_CAR, text).apply()
        "2016" -> mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_PACKAGE, text).apply()
        "2022" -> mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_COSME, text).apply()
        "2032" -> mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_EE, text).apply()
        "2038" -> mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_MEDICAL, text).apply()
        "2045" -> mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_BUILD, text).apply()
        "2049" -> mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_LED, text).apply()
        "2066" -> mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_TEXT, text).apply()
    }
}

fun getServiceOther(code: String): String {
    when (code) {
        "2008" -> return mSPReg.getString(REG_FORM_SERVICE_OTHER_CAR, "")!!
        "2016" -> return mSPReg.getString(REG_FORM_SERVICE_OTHER_PACKAGE, "")!!
        "2022" -> return mSPReg.getString(REG_FORM_SERVICE_OTHER_COSME, "")!!
        "2032" -> return mSPReg.getString(REG_FORM_SERVICE_OTHER_EE, "")!!
        "2038" -> return mSPReg.getString(REG_FORM_SERVICE_OTHER_MEDICAL, "")!!
        "2045" -> return mSPReg.getString(REG_FORM_SERVICE_OTHER_BUILD, "")!!
        "2049" -> return mSPReg.getString(REG_FORM_SERVICE_OTHER_LED, "")!!
        "2066" -> return mSPReg.getString(REG_FORM_SERVICE_OTHER_TEXT, "")!!
    }
    return ""
}

/**
 * 字段Code Country.  CN  UK  US
 */
fun setRegion(region: String) {
    mSPReg.edit().putString(REG_FORM_REGION, region).apply()
}

fun getRegion(): String {
    return mSPReg.getString(REG_FORM_REGION, "")!!
}

/**
 * 字段Code Province  AH  BJ
 */
fun setProvince(province: String) {
    mSPReg.edit().putString(REG_FORM_PROVINCE, province).apply()
}

fun getProvince(): String {
    return mSPReg.getString(REG_FORM_PROVINCE, "")!!
}

/**
 * 字段 Code
 */
fun setCity(city: String) {
    mSPReg.edit().putString(REG_FORM_CITY, city).apply()
}

fun getCity(): String {
    return mSPReg.getString(REG_FORM_CITY, "")!!
}

fun setRegionCombineText(text: String) {
    mSPReg.edit().putString("region_combine_text", text).apply()
}

fun getRegionCombineText(): String {
    return mSPReg.getString("region_combine_text", "")!!
}


fun setTellData(index: Int, text: String) {
    when (index) {
        1 -> mSPReg.edit().putString(REG_FORM_REGION_CODE, text).apply()     // 国家地区号
        2 -> mSPReg.edit().putString(REG_FORM_AREA_CODE, text).apply()         // 电话号码区号
        3 -> mSPReg.edit().putString(REG_FORM_TELL, text).apply()                   // 电话号码
        4 -> mSPReg.edit().putString(REG_FORM_EXT, text).apply()                    // 分机
        5 -> mSPReg.edit().putString(REG_FORM_AREA_CODE2, text).apply()       // 手机区号
        6 -> mSPReg.edit().putString(REG_FORM_PHONE_NUMBER, text).apply() // 手机号码
    }
}

fun getTellData(index: Int): String {
    when (index) {
        1 -> return mSPReg.getString(REG_FORM_REGION_CODE, "")!!
        2 -> return mSPReg.getString(REG_FORM_AREA_CODE, "")!!
        3 -> return mSPReg.getString(REG_FORM_TELL, "")!!
        4 -> return mSPReg.getString(REG_FORM_EXT, "")!!
        5 -> return mSPReg.getString(REG_FORM_AREA_CODE2, "")!!
        6 -> return mSPReg.getString(REG_FORM_PHONE_NUMBER, "")!!
    }
    return ""
}

fun setEmail(index: Int, text: String) {
    when (index) {
        1 -> mSPReg.edit().putString(REG_FORM_EMAIL1, text).apply()
        2 -> mSPReg.edit().putString(REG_FORM_EMAIL2, text).apply()
    }
}

fun getEmail(index: Int): String {
    when (index) {
        1 -> return mSPReg.getString(REG_FORM_EMAIL1, "")!!
        2 -> return mSPReg.getString(REG_FORM_EMAIL2, "")!!
    }
    return ""
}

fun setPassword(index: Int, text: String) {
    when (index) {
        1 -> mSPReg.edit().putString(REG_FORM_PWD1, text).apply()
        2 -> mSPReg.edit().putString(REG_FORM_PWD2, text).apply()
    }
}

fun getPassword(index: Int): String {
    when (index) {
        1 -> return mSPReg.getString(REG_FORM_PWD1, "")!!
        2 -> return mSPReg.getString(REG_FORM_PWD2, "")!!
    }
    return ""
}

fun setIsPostChecked(boolean: Boolean) {
    mSPReg.edit().putBoolean(REG_FORM_IS_POST, boolean).apply()
}

fun getIsPostChecked(): Boolean {
    return mSPReg.getBoolean(REG_FORM_IS_POST, false)
}

fun setPostCity(text: String) {
    mSPReg.edit().putString(REG_FORM_POST_CITY, text).apply()
}

fun getPostCity(): String {
    return mSPReg.getString(REG_FORM_POST_CITY, "")!!
}

fun setPostAddress(index: Int, text: String) {
    when (index) {
        0 -> mSPReg.edit().putString(REG_FORM_POSTCODE, text).apply()
        1 -> mSPReg.edit().putString(REG_FORM_POST_ADDRESS1, text).apply()
        2 -> mSPReg.edit().putString(REG_FORM_POST_ADDRESS2, text).apply()
    }
}

fun getPostAddress(index: Int): String {
    when (index) {
        0 -> return mSPReg.getString(REG_FORM_POSTCODE, "")!!
        1 -> return mSPReg.getString(REG_FORM_POST_ADDRESS1, "")!!
        2 -> return mSPReg.getString(REG_FORM_POST_ADDRESS2, "")!!
    }
    return ""
}

fun resetRegisterFormData() {
    setGuid("")
    paySuccess(false)
    saveConfirmPdfUrl("")
    putLogin(false)
    setName("")
    setCompany("")
    setTitleCode("")
    setTitleText("")
    setTitleOther("")
    setFunctionCode("")
    setFunctionOther("")
    setFunctionText("")
    setRegProduct("")
    setRegServiceCode("")
    mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_CAR, "").apply()
    mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_PACKAGE, "").apply()
    mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_COSME, "").apply()
    mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_EE, "").apply()
    mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_MEDICAL, "").apply()
    mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_BUILD, "").apply()
    mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_LED, "").apply()
    mSPReg.edit().putString(REG_FORM_SERVICE_OTHER_TEXT, "").apply()
    setRegion("")
    setProvince("")
    setCity("")
    setRegionCombineText("")
    setRegProduct("")
    setRegProduct("")
    setTellData(1, "")
    setTellData(2, "")
    setTellData(3, "")
    setTellData(4, "")
    setTellData(5, "")
    setTellData(6, "")
    setEmail(1, "")
    setEmail(2, "")
    setPassword(1, "")
    setPassword(2, "")
    setIsPostChecked(false)
    setPostCity("")
    setPostAddress(1, "")
    setPostAddress(2, "")
    setPostAddress(3, "")
}

/*  正则验证 */
fun checkYou(str: String): Boolean {
    val re = Regex("^[a-zA-Z\u4e00-\u9fa5\\s]+\$")
    return re.matches(str)
}

fun checkPwd(pwd: String): Boolean {
    val re = Regex("(?=^.{8,8}\$)(?=.*[0-9])(?=.*[A-Za-z])(^[^ ]+\$)")
    val result = re.matches(pwd)
    LogUtil.i("checkPwd: $result")
    return result
}

fun checkEmail(email: String): Boolean {
    val re = Regex("^\\w+(\\.\\w+)*@\\w+(\\.\\w+)+\$")
    val result = re.matches(email)
    LogUtil.i("checkEmail: $result")
    return result
}

/**
 *  @param str g=08656176ACC54019899D1D6656667673&l=936&p=ALIPAY
 *  @param startStr g=
 *  @param endStr &
 *  @return   08656176ACC54019899D1D6656667673
 */
fun subMiddle(str: String, startStr: String, endStr: String): String {
    return str.substringAfter(startStr, "").substringBefore(endStr, "")
}


fun clearRegFormInfo() {
    setName("")  // 保存名字
    setCompany("")
    setGender("")
    setTitleText("")
    setTitleCode("")
    setTitleOther("")
    setFunctionText("")
    setFunctionCode("")
    setFunctionOther("")
    setRegProduct("")
    setTellData(REG_TELL_AREA_CODE_INDEX, "")
    setTellData(REG_TELL_NO_INDEX, "")
    setTellData(REG_TELL_EXT_INDEX, "")
    setTellData(REG_MOBILE_AREA_CODE_INDEX, "")
    setTellData(REG_MOBILE_NO, "")
    setEmail(1, "")
    setEmail(2, "")
    setPassword(1, "")
    setPassword(2, "")
    setIsPostChecked(false)
    setPostAddress(0, "")
    setPostAddress(1, "")
    setPostAddress(2, "")
    setPostCity("")
}

/*  ------------------------------ SP  Reg  预登记资料信息 END------------------------------*/
/*  ------------------------------ MyChinaplas Start------------------------------*/
fun setMyChinaplasGuid(guid: String) {
    mSPReg.edit().putString("my_chinaplas_guid", guid).apply()
}

fun getMyChinaplasGuid(): String {
    return mSPReg.getString("my_chinaplas_guid", "")!!
}

fun setMemberId(memberId: String) {
    mSPReg.edit().putString("member_id", memberId).apply()
}

fun getMemberId(): String {
    return mSPReg.getString("member_id", "")!!
}

fun putMyChinaplasLogin(bool: Boolean) {
    mSPReg.edit().putBoolean("my_chinaplas_login", bool).apply()
}

fun isMyChinaplasLogin(): Boolean {
    return mSPReg.getBoolean("my_chinaplas_login", false)
}

fun setMyChinaplasEmail(text: String) {
    mSPReg.edit().putString("my_chinaplas_email", text).apply()
}

fun getMyChinaplasEmail(): String {
    return mSPReg.getString("my_chinaplas_email", "")!!
}

fun setMyChinaplasPhone(text: String) {
    mSPReg.edit().putString("my_chinaplas_phone", text).apply()
}

fun getMyChinaplasPhone(): String {
    return mSPReg.getString("my_chinaplas_phone", "")!!
}

fun setToken(text: String) {
    mSPReg.edit().putString("my_chinaplas_token", text).apply()
}

fun getToken(): String {
    return mSPReg.getString("my_chinaplas_token", "")!!
}

fun setKey(text: String) {
    mSPReg.edit().putString("my_chinaplas_key", text).apply()
}

fun getKey(): String {
    return mSPReg.getString("my_chinaplas_key", "")!!
}

fun setMyChinaplasIsPay(text: Boolean) {
    mSPReg.edit().putBoolean("my_chinaplas_is_pay", text).apply()
}

fun getMyChinaplasIsPay(): Boolean {
    return mSPReg.getBoolean("my_chinaplas_is_pay", false)
}

/**
 * 发票PDF链接，如果有，下载PDF
 */
fun setInvoicePdfUrl(text: String) {
    mSPReg.edit().putString("my_chinaplas_invoice_pdf", text).apply()
}

fun getInvoicePdfUrl(): String {
    return mSPReg.getString("my_chinaplas_invoice_pdf", "")!!
}

fun saveCPSConfirmPdfUrl(url: String) {
    mSPReg.edit().putString("my_chinaplas_confirm_pdf_url", url).apply()
}

fun getCPSConfirmPdfUrl(): String {
    return mSPReg.getString("my_chinaplas_confirm_pdf_url", "")!!
}

/**
 * 发票网页链接
 */
fun setInvoiceUrl(text: String) {
    mSPReg.edit().putString("my_chinaplas_invoice_pdf", text).apply()
}

fun getInvoiceUrl(): String {
    return mSPReg.getString("my_chinaplas_invoice_pdf", "")!!
}

fun setVisitorId(text: String) {
    mSPReg.edit().putString("my_chinaplas_invoice_pdf", text).apply()
}

fun getVisitorId(): String {
    return mSPReg.getString("my_chinaplas_invoice_pdf", "")!!
}

fun resetLoginInfo() {
    putMyChinaplasLogin(false)
    setToken("")
    setGuid("")
    setKey("")
    setMemberId("")
    setMyChinaplasGuid("")
    setMyChinaplasEmail("")
    setMyChinaplasPhone("")
    setInvoicePdfUrl("")
    setMyChinaplasIsPay(false)
    setVisitorId("")
    setInvoiceUrl("")
}


/*  ------------------------------ MyChinaplas End------------------------------*/

/**
 * @density = resource.displayMetrics.density
 */
fun px2dip(px: Float): Int {
    return (px / mResources.displayMetrics.density + 0.5f).toInt()
}

fun dp2px(dp: Float): Int {
    return (dp * mResources.displayMetrics.density + 0.5f).toInt()
}

/**
 * @return 950 1252 936
 */
fun getLangCode(): String {
    return if (getCurrLanguage() == LANG_TC) "950" else if (getCurrLanguage() == LANG_EN) "1252" else "936"
}

/**
 * @return eng  simp  trad
 */
fun getLangStr(): String {
    return if (getCurrLanguage() == LANG_TC) "trad" else if (getCurrLanguage() == LANG_EN) "eng" else "simp"
}

/**
 * @return eng  simp  trad
 */
fun getLangStr(lang: String): String {
    return if (lang == "950") "trad" else if (lang == "1250") "eng" else "simp"
}

/*  ------------------------------ dialog ------------------------------*/
/**
 * 最简单对话框，文字 + 确认 按钮
 */
fun alertDialogSingleConfirm(context: Context, res: Int) {
    AlertDialog.Builder(context)
        .setMessage(res)
        .setPositiveButton(context.getText(R.string.confirm)) { dialog, _ ->
            dialog.dismiss()
        }.show()
}

/**
 * 确认对话框，两个按钮，确认，取消
 */
fun alertDialogConfirmTwo(
    context: Context,
    resMsg: Int,
    positiveButton: DialogInterface.OnClickListener
) {
    AlertDialog.Builder(context)
        .setMessage(resMsg)
        .setPositiveButton(context.getText(R.string.confirm), positiveButton)
        .setNegativeButton(context.getText(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }.show()
}

/**
 * 最简单对话框，文字 + 单个 按钮
 */
fun alertDialogSingleButton(context: Context, resMsg: Int, resBtn: Int) {
    AlertDialog.Builder(context)
        .setMessage(resMsg)
        .setPositiveButton(context.getText(resBtn)) { dialog, _ ->
            dialog.dismiss()
        }.show()
}

/**
 * 最简单对话框，文字 + 单个 按钮
 */
fun alertDialogSingleButton(context: Context, resMsg: String, resBtn: Int) {
    AlertDialog.Builder(context)
        .setMessage(resMsg)
        .setPositiveButton(context.getText(resBtn)) { dialog, _ ->
            dialog.dismiss()
        }.show()
}

/**
 * 两个选项对话框
 *
 * @param resMsg  对话框内容
 * @param resPositive 确认文字
 * @param resNegative 取消对话框文字
 * @param positiveButton 确认按钮事件
 *
 */
fun alertDialogTwoButton(
    context: Context,
    resMsg: Int,
    resPositive: Int,
    resNegative: Int,
    positiveButton: DialogInterface.OnClickListener
) {
    AlertDialog.Builder(context)
        .setMessage(resMsg)
        .setPositiveButton(context.getText(resPositive), positiveButton)
        .setNegativeButton(context.getText(resNegative)) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

fun alertDialogProgress(
    context: Context,
    msg: String
): Dialog {
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null, false)
    val builder = AlertDialog.Builder(context).setView(view)
    view.findViewById<TextView>(R.id.dialog_msg).text = msg
    builder.setCancelable(false)
    return builder.show()
}

fun getName(tc: String, en: String, sc: String): String {
    return when (getCurrLanguage()) {
        LANG_EN -> en
        LANG_TC -> tc
        else -> sc
    }
}

fun getHtmName(): String? {
    return getName("TC.html", "EN.html", "SC.html")
}

fun getSpanCount(): Int {
    return if (isTablet()) 3 else 2
}

fun getExhibitorTitle(): String {
    return getName("展商列表", "Exhibitor List", "展商列表")
}

fun getNewtechTitle(): String {
    return getName("新品及熱點技術", "New & Hot Tech", "新品及热点技术")
}

fun getSeminarTitle(): String {
    return getName("技術交流會", "Technical Seminar", "技术交流会")
}

/**
 * @param windowToken binding.root.windowToken
 */
fun hideInput(context: Context, windowToken: IBinder) {
    val inputMethodManager: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

/*同期活动，技术交流会 item id*/
fun setItemEventID(pageId: String) {
    mSPConfig.edit().putString("concurrent_event_id", pageId).apply()
}

fun getItemEventID(): String {
    return mSPConfig.getString("concurrent_event_id", "")!!
}

fun setItemSeminarEventID(eventId: Int) {
    mSPConfig.edit().putInt("seminar_event_id", eventId).apply()
}

fun getItemSeminarEventID(): Int {
    return mSPConfig.getInt("seminar_event_id", 0)
}

/* 同期活动 应用行业筛选ids */
fun setSPEventFilter(filter: String) {
    mSPConfig.edit().putString("event_filter", filter).apply()
}

fun getSPEventFilter(): String {
    return mSPConfig.getString("event_filter", "")!!
}

fun setSPSeminarFilterEmpty(filter: String) {
    mSPConfig.edit().putString("seminar_filter_%3%", filter).apply()
    mSPConfig.edit().putString("seminar_filter_%4%", filter).apply()
    mSPConfig.edit().putString("seminar_filter_%5%", filter).apply()
}

fun setSPSeminarFilter(date: String, filter: String) {
    mSPConfig.edit().putString("seminar_filter_$date", filter).apply()
}

fun getSPSeminarFilter(date: String): String {
    return mSPConfig.getString("seminar_filter_$date", "")!!
}


/*同期活動 点击筛选前的bar index*/
fun setEventTabIndex(index: Int) {
    mSPConfig.edit().putInt("concurrent_event_tab_index", index).apply()
}

fun getEventTabIndex(): Int {
    return mSPConfig.getInt("concurrent_event_tab_index", 0)
}

fun setSeminarTabIndex(index: Int) {
    mSPConfig.edit().putInt("seminar_tab_index", index).apply()
}

fun getSeminarTabIndex(): Int {
    return mSPConfig.getInt("seminar_tab_index", 1)
}

// 上午 0 下午 1
fun setSeminarTimeIndex(date: String, index: Int) {
    mSPConfig.edit().putInt("seminar_time_index_$date", index).apply()
}

fun getSeminarTimeIndex(date: String): Int {
    return mSPConfig.getInt("seminar_time_index_$date", 0)
}

fun resetSeminarTimeIndex(index: Int) {
    mSPConfig.edit().putInt("seminar_time_index_%3%", index).apply()
    mSPConfig.edit().putInt("seminar_time_index_%4%", index).apply()
    mSPConfig.edit().putInt("seminar_time_index_%5%", index).apply()
}

/*  ========== 广告次序  ============    */
/**
 * 当前显示的广告index
 */
fun setD3CurrentIndex(currentIndex: Int) {
    mSPConfig.edit().putInt("d3_current_index", currentIndex).apply()
}

fun getD3CurrentIndex(): Int {
    return mSPConfig.getInt("d3_current_index", -1)
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

