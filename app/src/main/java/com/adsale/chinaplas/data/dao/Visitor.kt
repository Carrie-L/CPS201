package com.adsale.chinaplas.data.dao

import androidx.room.Entity

/**
 * Created by Carrie on 2020/1/16.
 */
@Entity(primaryKeys = arrayOf("Email", "CellNo"))
class Visitor {
    val Email: String = ""
    val CellNo: String? = ""  // 手机号
    val Company: String? = ""
    val ContactPerson: String? = ""  // 联系人
    val TelCountry: String? = ""   // 国家地区号   86
    val TelArea: String? = ""   // 电话区号
    val TelNo: String = ""  // 电话号码
    val TelExt: String? = ""   // 电话分析
    val CellCountry: String? = ""  // 国家地区号   86
    val CellArea: String? = ""  // 手机区号
    val SalCode: String? = ""   // 性别Code   MS (女士)   MSR (先生)
    val Title: String? = ""
    val TitleCode: String? = ""
    val TitleOther: String? = ""
    val Function: String? = ""
    val FunctionCode: String? = ""
    val FunctionOther: String? = ""
    val MajorProduct: String? = ""   // 主营产品
    val ServiceCode: String? = ""   // 公司业务
    val ServiceOtherCar: String? = ""
    val ServiceOtherPackage: String? = ""
    val ServiceOtherCosme: String? = ""
    val ServiceOtherEE: String? = ""
    val ServiceOtherMedical: String? = ""
    val ServiceOtherBuild: String? = ""
    val ServiceOtherLed: String? = ""
    val ServiceOtherText: String? = ""
    val Region: String? = ""
    val Province: String? = ""
    val City: String? = ""
    val Email2: String? = ""
    val IsPostChecked: Boolean? = false
    val PostCity: String? = ""
    val PostAddress1: String? = ""
    val PostAddress2: String? = ""
    val PostAddress3: String? = ""
    val VisitorId: String? = ""
    val Guid: String? = ""
    val PayDate: String? = ""
    val Paid: Boolean? = false
    val PayType: String? = ""
    val PdfUrl: String? = ""
    val Ref: String? = ""
    val ConfirmPdfUr: String? = ""
}