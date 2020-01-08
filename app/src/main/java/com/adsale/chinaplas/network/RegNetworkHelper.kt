package com.adsale.chinaplas.network

import com.adsale.chinaplas.utils.*
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Carrie on 2019/11/20.
 */

val REGISTER_INVOICE_URL: String? =
    "https://www.chinaplasonline.com/CPS19/PreRegInvoice/%s?device=APP&guid=%s"
const val REGISTER_CHARGE =
    "https://e.adsale.com.hk/vreg/PreregSubmitCloud/PayAPPjump" /* 获取charge数据 */
const val REGISTER_BASE_URL =
    "https://epayment.adsale-marketing.com.cn/vreg/PreregSubmitCloud/"
const val REGISTER_CONFIRM_PAY =
    "https://epayment.adsale-marketing.com.cn/vreg/PreregSubmitCloud/APPSelectPay" /* 向服务器确认是否支付成功 */
const val CONFIRM_URL =
    "https://www.chinaplasonline.com/CPS20/PreregSuccess/%s/?guid=%s&device=mobileapp"

/*  ~~~~~~~~MyChinaplas 登录~~~~~~~~~~*/

const val LOGIN_URL = "https://www.chinaplasonline.com/CPS20/CPSAPI/MembershipGenecral"    // 密码/SMS登录。 成功返回对应id, 失败返回2
const val LOGIN_SEND_SMS_CODE_URL =
    "https://eform.adsale.com.hk/vreg/Content/handler/MobileCode.ashx"  // 发送验证码。 成功返回guid
const val LOGIN_CHECK_SMS_CODE_URL =
    "https://eform.adsale.com.hk/vreg/Content/handler/MobileCode.ashx"   // 校验验证码. 成功返回True
const val CHINAPLAS_CLIENT_URL = "https://www.chinaplasonline.com/CPS20/mychinaplas/%s"   // eng trad simp
const val MY_CHINAPLAS_HOME_URL =
    "https://www.chinaplasonline.com/CPS20/mychinaplas/%s/?AppToken=%s&device=mobileapp"   // MyChinaplas 首页.   [lang]  [Token]
const val MY_CHINAPLAS_MY_URL =
    "https://prereg.adsaleonline.com/Prereg/PreregPDF/SubmitAction/Preregland?encryptKey=%s"  // MyChinaplas 我的登记信息
const val MY_CHINAPLAS_INVOICE_URL = "https://www.chinaplasonline.com/CPS20/prereginvoice/%s/?guid=%s"
const val GENIUS_HELPER_ENCRYPT =
    "https://eform.adsale.com.hk/GeniusHelper/TestRoute/Encrypt"    // 获取 [token], 或者 [KEY]  , API相同，传递的参数格式不同。

fun getTokenJson(memberId: String, email: String): String {
    return " {\"Context\": \"$memberId#2020-07-04T13:55:07#$email\"}"
}

fun getKeyJson(guid: String): String {
    return " {\"Context\": \"$guid#2020-07-04T13:55:07#${getLangStr()}\"}"
}


/*  ----------------- 确认信链接 -------  */
/**
 *  ① POST:  获取 EncryptKey
 *  [url]       https://eform.adsale.com.hk/GeniusHelper/TestRoute/Encrypt
 *  [body]   {"Context":"[guid]#2020-07-04T13:55:07#[simp]"}      <<< guid#無效日期#語言
 *
 *  ② GET: PDF 下载url
 * [Url] https://prereg.adsaleonline.com/Prereg/PreregPDF/SubmitAction/Preregland?encryptKey=[Key]
 *
 */
const val REG_CONFIRM_KEY_API = "https://eform.adsale.com.hk/GeniusHelper/TestRoute/Encrypt"
const val REG_CONFIRM_LATTER_URL =
    "https://prereg.adsaleonline.com/Prereg/PreregPDF/SubmitAction/Preregland?encryptKey="


fun submitJson(
    smsGuid: String,
    phoneNo: String,
    email: String,
    email2: String,
    gender: String,
    name: String,
    company: String,
    titleCode: String,
    titleOther: String,
    functionCode: String,
    functionOther: String,
    companyProduct: String,
    service: String,
    serviceOtherCar: String,
    serviceOtherPackage: String,
    serviceOtherCosme: String,
    serviceOtherBuild: String,
    serviceOtherEE: String,
    serviceOtherMedical: String,
    serviceOtherLED: String,
    serviceOther: String,
    countryCode: String,
    provinceCode: String,
    cityCode: String,
    tellRegionCode: String,
    tellAreaCode: String,
    tellNo: String,
    extNo: String,
    mobileAreaCode: String,
    pwd: String,
    pwd2: String,
    postcode: String,
    postAddressCity: String,
    postAddress1: String,
    postAddress2: String,
    smsCode: String
): String {
    val json = "{" +
            "\"Data\": [{" +
            "\"Key\": \"SMSGuid\"," +
            "\"Value\": \"$smsGuid\"" +
            "}, {" +
            "\"Key\": \"MemberGuid\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"hdSMSPath\"," +
            "\"Value\": \"APP Page\"" +
            "}, {" +
            "\"Key\": \"WeChatOpenId\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"PayOpenId\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"regsource\"," +
            "\"Value\": \"213WFR\"" +
            "}, {" +
            "\"Key\": \"guidTemp\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"device\"," +
            "\"Value\": \"APP\"" +
            "}, {" +
            "\"Key\": \"RegCat\"," +
            "\"Value\": \"1\"" +
            "}, {" +
            "\"Key\": \"txtPostal\"," +
            "\"Value\": \"$postcode\"" +
            "}, {" +
            "\"Key\": \"txtAddress1\"," +
            "\"Value\": \"$postAddressCity\"" +
            "}, {" +
            "\"Key\": \"txtAddress2\"," +
            "\"Value\": \"$postAddress1\"" +
            "}, {" +
            "\"Key\": \"txtAddress3\"," +
            "\"Value\": \"$postAddress2\"" +
            "}, {" +
            "\"Key\": \"txtLoginCellNo\"," +
            "\"Value\": \"$phoneNo\"" +
            "}, {" +
            "\"Key\": \"txtLoginEmail\"," +
            "\"Value\": \"$email\"" +
            "}, {" +
            "\"Key\": \"ddlSalutation\"," +
            "\"Value\": \"$gender\"" +
            "}, {" +
            "\"Key\": \"txtContactPerson\"," +
            "\"Value\": \"$name\"" +
            "}, {" +
            "\"Key\": \"txtCompany\"," +
            "\"Value\": \"$company\"" +
            "}, {" +
            "\"Key\": \"ddlJobTitle\"," +
            "\"Value\": \"$titleCode\"" +
            "}, {" +
            "\"Key\": \"txtJobTitleOthers\"," +
            "\"Value\": \"$titleOther\"" +
            "}, {" +
            "\"Key\": \"ddlJobFunction\"," +
            "\"Value\": \"$functionCode\"" +
            "}, {" +
            "\"Key\": \"txtJobFunctionOthers\"," +
            "\"Value\": \"$functionOther\"" +
            "}, {" +
            "\"Key\": \"tt_serSource_4\"," +
            "\"Value\": \"$companyProduct\"" +
            "}, {" +
            "\"Key\": \"hd_Service\"," +
            "\"Value\": \"$service\"" +
            "}, {" +
            "\"Key\": \"cb_service\"," +
            "\"Value\": \"$service\"" +
            "}, {" +
            "\"Key\": \"ddlBusNatureCode\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"txtBusNatureOthers\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"ddlOwnerCode\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"txtOwnerOthers\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"hd_Lang\"," +
            "\"Value\": \"${if (getCurrLanguage() == LANG_TC) "t" else if (getCurrLanguage() == LANG_EN) "e" else "s"}\"" +
            "}, {" +
            "\"Key\": \"hd_Format\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"valCellNo\"," +
            "\"Value\": \"$phoneNo\"" +
            "}, {" +
            "\"Key\": \"valEmail\"," +
            "\"Value\": \"$email\"" +
            "}, {" +
            "\"Key\": \"tt_ser_2008\"," +
            "\"Value\": \"$serviceOtherCar\"" +
            "}, {" +
            "\"Key\": \"tt_ser_2016\"," +
            "\"Value\": \"$serviceOtherPackage\"" +
            "}, {" +
            "\"Key\": \"tt_ser_2022\"," +
            "\"Value\": \"$serviceOtherCosme\"" +
            "}, {" +
            "\"Key\": \"tt_ser_2032\"," +
            "\"Value\": \"$serviceOtherEE\"" +
            "}, {" +
            "\"Key\": \"tt_ser_2038\"," +
            "\"Value\": \"$serviceOtherMedical\"" +
            "}, {" +
            "\"Key\": \"tt_ser_2045\"," +
            "\"Value\": \"$serviceOtherBuild\"" +
            "}, {" +
            "\"Key\": \"tt_ser_2049\"," +
            "\"Value\": \"$serviceOtherLED\"" +
            "}, {" +
            "\"Key\": \"tt_ser_2066\"," +
            "\"Value\": \"$serviceOther\"" +
            "}, {" +
            "\"Key\": \"ddlCountryCode\"," +
            "\"Value\": \"$countryCode\"" +
            "}, {" +
            "\"Key\": \"ddlProvCodeChange\"," +
            "\"Value\": \"$provinceCode\"" +
            "}, {" +
            "\"Key\": \"ddlProvCode\"," +
            "\"Value\": \"$provinceCode\"" +
            "}, {" +
            "\"Key\": \"ddlCityId\"," +
            "\"Value\": \"$cityCode\"" +
            "}, {" +
            "\"Key\": \"txtTelCountry\"," +
            "\"Value\": \"$tellRegionCode\"" +
            "}, {" +
            "\"Key\": \"txtTelArea\"," +
            "\"Value\": \"$tellAreaCode\"" +
            "}, {" +
            "\"Key\": \"txtTel\"," +
            "\"Value\": \"$tellNo\"" +
            "}, {" +
            "\"Key\": \"txtTelExt\"," +
            "\"Value\": \"$extNo\"" +
            "}, {" +
            "\"Key\": \"txtCellCountry\"," +
            "\"Value\": \"$tellRegionCode\"" +
            "}, {" +
            "\"Key\": \"txtCellArea\"," +
            "\"Value\": \"$mobileAreaCode\"" +
            "}, {" +
            "\"Key\": \"mCell_Code\"," +
            "\"Value\": \"$smsCode\"" +
            "}, {" +
            "\"Key\": \"txtEmailConfirm\"," +
            "\"Value\": \"$email2\"" +
            "}, {" +
            "\"Key\": \"txtFaxCountry\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"txtFaxArea\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"txtFaxNo\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"txtWebsite\"," +
            "\"Value\": \"\"" +
            "}, {" +
            "\"Key\": \"txtpwd\"," +
            "\"Value\": \"$pwd\"" +
            "}, {" +
            "\"Key\": \"txtpwdConfirm\"," +
            "\"Value\": \"$pwd2\"" +
            "}, {" +
            "\"Key\": \"chkIsOptOut\"," +
            "\"Value\": \"on\"" +
            "}]," +
            "\"LangId\": \"${getLangCode()}\"," +
            "\"Randstr\": \"@z2V\"," +
            "\"Showcode\": \"CPS20\"," +
            "\"Ticket\": \"!@#\$AdsaleTest321\"" +
            "}"


    LogUtil.i("json=$json")



    return json
}

fun checkInsertJson(rid: Int, langId: String): String {
    return " {\n" +
            "        \"Rid\": $rid,\n" +
            "        \"showcode\": 'CPS20',\n" +
            "        \"ShowID\": '523',\n" +
            "        \"LangId\": $langId,\n" +
            "        \"codeMethod\": 'barcode',\n" +
            "        \"WeChatOpenId\": '',\n" +
            "        \"device\": ''\n" +
            "    }"
}

fun pingRequestJson(payMethod: String, guid: String, lang: String): String {
    return " {\n" +
            "        \"p_payMethod\": $payMethod,\n" +
            "        \"g_guid\": $guid,\n" +
            "        \"p_lang\": $lang\n" +
            "    }"
}

fun confirmKeyJson(guid: String): String {
    return " {\"Context\": \"$guid#2020-07-04T13:55:07#${getLangStr()}\"}"
}

fun chargeRequestJson(
    payMethod: String,
    guid: String,
    lang: String,
    fe: String,
    feCode: String
): String? {
    val jsonObject = JSONObject()
    try {
        jsonObject.put("payMethod", payMethod)
            .put("guid", guid)
            .put("lang", lang)
            .put("FECodeAPP", fe)
            .put("FECodeAPP_CODE", feCode)
            .put("showCode", "CPS20")
        return jsonObject.toString()
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    return ""
}



