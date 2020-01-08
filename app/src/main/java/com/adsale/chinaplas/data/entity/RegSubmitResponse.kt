package com.adsale.chinaplas.data.entity

import com.squareup.moshi.JsonClass
import retrofit2.http.Url

/**
 * Created by Carrie on 2019/11/26.
 * 预登记提交后，返回
{
"PrereSubmitModel": {
"LangId": 0,
"ShowId": 0,
"ShowCode": null,
"Ticket": null,
"Randstr": null,
"Rid": 0,
"Guid": "C5F64BA85CBE463ABC57B2CCCB66A800",
"Data": null,
"codeMethod": null,
"userAgent": null,
"WeChatOpenId": null,
"device": null
},
Validation": {
"IsSuccessful": true,
"Msg": [],
"Url": "https://www.chinaplasonline.com/CPS20/PreRegSuccessPay/trad/?guid=C5F64BA85CBE463ABC57B2CCCB66A800",
"WeChatUrl": "https://weixin.adsaleonline.com/index.php?s=/Home/User/login/publicid/53/sub_redirect_type/1/sub_redirect_url/https:@_@_www.chinaplasonline.com@_CPS20@_PreRegSuccessPay@_trad@_@$guid=C5F64BA85CBE463ABC57B2CCCB66A800",
"guid": null,
"BrowserAgent": null
}
}
 *
 *
 */
@JsonClass(generateAdapter = true)
data class RegSubmitResponse(
    val PrereSubmitModel: PrereSubmitModel,
    val Validation: Validation
)

@JsonClass(generateAdapter = true)
data class PrereSubmitModel(
    val Rid: Int,
    val Guid: String?
)

@JsonClass(generateAdapter = true)
data class Validation(
    val IsSuccessful: Boolean,
    val Url: String?
)