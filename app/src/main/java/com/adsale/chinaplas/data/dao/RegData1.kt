package com.adsale.chinaplas.data.dao

import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.SELECT_TITLE_EN
import com.adsale.chinaplas.utils.SELECT_TITLE_SC
import com.adsale.chinaplas.utils.SELECT_TITLE_TC
import com.adsale.chinaplas.viewmodels.RegisterViewModel

/**
 * Created by Carrie on 2019/11/1.
 */
@Entity(tableName = "RegOptionData")
class RegOptionData {
    @PrimaryKey
    var objectId: String = ""
    var PartName: String? = ""
    var NameSC: String? = ""
    var NameEN: String? = ""
    var NameTC: String? = ""
    var DetailCode: String = ""
    var OrderSC: String? = ""
    var OrderTC: String? = ""
    var OrderEN: String? = ""
    var updatedAt: String? = ""
    var GroupCode: String? = ""

    fun getName(): String {
        if (!TextUtils.isEmpty(NameSC)) {
            return NameSC!!
        }
        if (!TextUtils.isEmpty(NameTC)) {
            return NameTC!!
        }
        if (!TextUtils.isEmpty(NameEN)) {
            return NameEN!!
        }
        return ""
    }

    fun isOther(): Boolean {
        return getName().contains("其他") || getName().contains("Others")
    }

    fun setSelectTitle() {
        NameSC = SELECT_TITLE_SC
        NameEN = SELECT_TITLE_EN
        NameTC = SELECT_TITLE_TC
    }

    @Transient
    @Ignore
    var isChecked = ObservableField<Boolean>(false)

    fun isProductParent(): Boolean {
        return DetailCode.contains("ProductParent")
    }

    fun setNameAndOrder(lang: Int, Name: String, Order: String) {
        if (lang == 0) {
            NameTC = Name
            OrderTC = Order
        } else if (lang == 1) {
            NameEN = Name
            OrderEN = Order
        } else if (lang == 2) {
            NameSC = Name
            OrderSC = Order
        }
    }

    override fun toString(): String {
        return getName()
        //        return "RegOptionData(objectId='$objectId', PartName='$PartName', NameSC='$NameSC', NameEN='$NameEN', NameTC='$NameTC', DetailCode='$DetailCode', OrderSC='$OrderSC', OrderTC='$OrderTC', OrderEN='$OrderEN', updatedAt='$updatedAt', GroupCode='$GroupCode')"
    }


    /**
     * JobTitleList
     * JobFuctionList
     * ProductList
     */
    //    override fun toString(): String {
    ////        return "{\"PartName\":\"ProductList\",\"Property\":\"{\"\"NameSC\"\":\"\"$NameSC\"\", \"\"NameEN\"\":\"\"$NameEN\"\", \"\"NameTC\"\":\"\"$NameTC\"\", \"\"DetailCode\"\":\"\"$DetailCode\"\", \"\"OrderSC\"\":\"\"$OrderSC\"\", \"\"OrderTC\"\":\"\"$OrderTC\"\", \"\"OrderEN\"\":\"\"$OrderEN\"\", \"\"GroupCode\"\":\"\"$GroupCode\"\"}\"}"
    ////                return "{\"PartName\":\"ProductList\",\"Property\":\"{NameSC:\"\"$NameSC\", NameEN:\"\"$NameEN\", NameTC:\"\"$NameTC\", DetailCode:\"\"$DetailCode\", OrderSC:\"\"$OrderSC\", OrderTC:\"\"$OrderTC\", OrderEN:\"\"$OrderEN\", GroupCode:\"\"$GroupCode\"}\"}"
    //    }


}