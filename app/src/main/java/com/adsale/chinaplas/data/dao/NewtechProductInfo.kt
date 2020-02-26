package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.getName
import java.util.*

@Entity
class NewtechProductInfo constructor() {
    @PrimaryKey
    var RID: String = ""
    var CompanyID: String = ""
    var BoothNo: String = ""
    var CompanyNameEn: String? = ""
    var CompanyNameSc: String? = ""
    var CompanyNameTc: String? = ""
    var Product_Name_SC: String? = ""
    var Rroduct_Description_SC: String? = ""
    var Product_Name_TC: String? = ""
    var Rroduct_Description_TC: String? = ""
    var Product_Name_EN: String? = ""
    var Rroduct_Description_EN: String? = ""

    var imageName: String? = ""

    @Ignore
    var isAder = false

    constructor(RID: String,
                CompanyID: String,
                BoothNo: String,
                CompanyNameEn: String?,
                CompanyNameSc: String?,
                CompanyNameTc: String?,
                Product_Name_SC: String?,
                Rroduct_Description_SC: String?,
                Product_Name_TC: String?,
                Rroduct_Description_TC: String?,
                Product_Name_EN: String?,
                Rroduct_Description_EN: String?,
                imageName: String,
                isAder: Boolean) : this() {
        this.RID = RID
        this.CompanyID = CompanyID
        this.BoothNo = BoothNo
        this.CompanyNameEn = CompanyNameEn
        this.CompanyNameSc = CompanyNameSc
        this.CompanyNameTc = CompanyNameTc
        this.Product_Name_SC = Product_Name_SC
        this.Rroduct_Description_SC = Rroduct_Description_SC
        this.Product_Name_TC = Product_Name_TC
        this.Rroduct_Description_TC = Rroduct_Description_TC
        this.Product_Name_EN = Product_Name_EN
        this.Rroduct_Description_EN = Rroduct_Description_EN
        this.imageName = imageName
        this.isAder = isAder
    }

    fun getCompanyName(): String {
        return getName(CompanyNameTc!!, CompanyNameEn!!, CompanyNameSc!!)
    }

    fun getProductName(): String {
        return getName(Product_Name_TC!!, Product_Name_EN!!, Product_Name_SC!!)
    }

    fun getProductDesc(): String {
        return getName(Rroduct_Description_TC!!, Rroduct_Description_EN!!, Rroduct_Description_SC!!)
    }

    fun isContainsCompany(str: String): Boolean {
        val text = str.toLowerCase(Locale.ENGLISH)
        return CompanyNameEn?.toLowerCase(Locale.ENGLISH)?.contains(text) ?: false ||
                CompanyNameSc?.toLowerCase(Locale.ENGLISH)?.contains(text) ?: false ||
                CompanyNameTc?.toLowerCase(Locale.ENGLISH)?.contains(text) ?: false
    }

    fun isContainsProductName(text: String): Boolean {
        return Product_Name_EN?.toLowerCase(Locale.ENGLISH)?.contains(text) ?: false ||
                Product_Name_SC?.toLowerCase(Locale.ENGLISH)?.contains(text) ?: false ||
                Product_Name_TC?.toLowerCase(Locale.ENGLISH)?.contains(text) ?: false
    }

    fun parser(strings: Array<String>) {
        this.RID = strings[0]
        this.CompanyID = strings[1]
        this.BoothNo = strings[2]
        this.CompanyNameEn = strings[3]
        this.CompanyNameSc = strings[4]
        this.CompanyNameTc = strings[5]
        this.Product_Name_SC = strings[6]
        this.Rroduct_Description_SC = strings[7]
        this.Product_Name_TC = strings[8]
        this.Rroduct_Description_TC = strings[9]
        this.Product_Name_EN = strings[10]
        this.Rroduct_Description_EN = strings[11]
    }

    override fun toString(): String {
        return "NewtechProductInfo(RID='$RID', CompanyID='$CompanyID', isAder=$isAder, CompanyNameSc=$CompanyNameSc, imageName=$imageName)"
    }

}