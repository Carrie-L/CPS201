package com.adsale.chinaplas.helper

import com.adsale.chinaplas.data.dao.NewtechProductInfo
import com.adsale.chinaplas.data.entity.*
import com.adsale.chinaplas.utils.*

const val D5_VISIT = 0
const val D5_GENERATION = 1
const val D5_MYCHINAPLAS = 2
const val D5_EVENT = 3

class ADHelper {
    val epo: EPO
    val baseUrl: String


    init {
        epo = parseFileJson(EPO::class.java, "advertisement2020.txt")!!
        LogUtil.i("--------ADHelper init--------------")
        baseUrl = epo.base.baseUrl
    }

    // ※※※※※※※※※※※※※※※ D1   ※※※※※※※※※※※※※※※※※※※※※※
    fun d1ImageUrl(): String {
        return if (getCurrLanguage() == LANG_EN)
            "$baseUrl${epo.D1.image_en}"
        else
            "$baseUrl${epo.D1.image_cn}"
    }

    fun d1CompanyID(): String {
        return if (getCurrLanguage() == LANG_EN)
            epo.D1.company_id_en
        else
            epo.D1.company_id_cn
    }

    // ※※※※※※※※※※※※※※※ D2   ※※※※※※※※※※※※※※※※※※※※※※
    fun d2Property(): List<Property> {
        return if (getCurrLanguage() == LANG_EN)
            epo.D2.en
        else
            epo.D2.cn
    }


    // ※※※※※※※※※※※※※※※ D3  ※※※※※※※※※※※※※※※※※※※※※※
    fun d3Property(): List<Property> {
        return if (getCurrLanguage() == LANG_EN)
            epo.D3.en
        else
            epo.D3.cn
    }

    fun getCurrentD3(): Property {
        var index = getD3CurrentIndex()
        val list = d3Property()
        if (index == list.size - 1) {
            index = 0
        } else {
            index++
        }
        setD3CurrentIndex(index)
        LogUtil.i("getCurrentD3ImageUrl: index=$index")
        return list[index]
    }

    // ※※※※※※※※※※※※※※※ D4 平面图 ※※※※※※※※※※※※※※※※※※※※※※


    // ※※※※※※※※※※※※※※※ D5 自选页底横幅 ※※※※※※※※※※※※※※※※※※※※※※
    // 判断该页面有无D5广告
    fun d5HasAD(index: Int) {

    }

    fun d5Property(index: Int): Property {
        return if (getCurrLanguage() == LANG_EN)
            epo.D5.en[index]
        else
            epo.D5.cn[index]
    }

    fun d5ImageUrl(index: Int): String {
        return if (getCurrLanguage() == LANG_EN)
            "$baseUrl${epo.D5.en[index].imageName()}"
        else
            "$baseUrl${epo.D5.cn[index].imageName()}"
    }

    fun d5PageID(index: Int): String {
        return if (getCurrLanguage() == LANG_EN)
            epo.D5.en[index].pageID
        else
            epo.D5.cn[index].pageID
    }


    // ※※※※※※※※※※※※※※※ D6 item logo & 展商详情  ※※※※※※※※※※※※※※※※※※※※※※
    fun getD6(companyID: String): D6? {
        val list = epo.D6
        for (item in list) {
            if (item.companyID == companyID) {
                return item
            }
        }
        return null
    }


    // ※※※※※※※※※※※※※※※ D7 新技术产品 ※※※※※※※※※※※※※※※※※※※※※※
    fun d7List(): List<D7> {
        return epo.D7
    }

    fun getADProductEntity(d7Item: D7): NewtechProductInfo {
        return NewtechProductInfo(d7Item.RID,
            d7Item.CompanyID,
            d7Item.BoothNo,
            d7Item.CompanyName_EN,
            d7Item.CompanyName_SC,
            d7Item.CompanyName_TC,
            d7Item.ProductName_SC,
            d7Item.Description_SC,
            d7Item.ProductName_TC,
            d7Item.Description_TC,
            d7Item.ProductName_EN,
            d7Item.Description_EN,
            d7Item.FirstPageImage,
            true)
    }

    fun getD7(rid: String): D7? {
        val list = d7List()
        for (item in list) {
            if (item.RID == rid) {
                return item
            }
        }
        return null
    }


    // ※※※※※※※※※※※※※※※ D8 技术交流会  ※※※※※※※※※※※※※※※※※※※※※※
    fun d8List(): List<D8> {
        return epo.D8
    }

    fun getADHeight(): Int {
        return getScreenWidth() * IMG_HEIGHT / IMG_WIDTH
    }

    fun isD1Open(): Boolean {
        return if (getCurrLanguage() == LANG_EN)
            epo.base.isOpenEN[0] == 1
        else
            epo.base.isOpenCN[0] == 1
    }

    fun isD2Open(): Boolean {
        return if (getCurrLanguage() == LANG_EN)
            epo.base.isOpenEN[1] == 1
        else
            epo.base.isOpenCN[1] == 1
    }

    fun isD3Open(): Boolean {
        return if (getCurrLanguage() == LANG_EN)
            epo.base.isOpenEN[2] == 1
        else
            epo.base.isOpenCN[2] == 1
    }

    fun isD4Open(): Boolean {
        return if (getCurrLanguage() == LANG_EN)
            epo.base.isOpenEN[3] == 1
        else
            epo.base.isOpenCN[3] == 1
    }

    fun isD5Open(): Boolean {
        return if (getCurrLanguage() == LANG_EN)
            epo.base.isOpenEN[4] == 1
        else
            epo.base.isOpenCN[4] == 1
    }

    fun isD6Open(): Boolean {
        return if (getCurrLanguage() == LANG_EN)
            epo.base.isOpenEN[5] == 1
        else
            epo.base.isOpenCN[5] == 1
    }

    fun isD7Open(): Boolean {
        return if (getCurrLanguage() == LANG_EN)
            epo.base.isOpenEN[6] == 1
        else
            epo.base.isOpenCN[6] == 1
    }

    fun isD8Open(): Boolean {
        return if (getCurrLanguage() == LANG_EN)
            epo.base.isOpenEN[7] == 1
        else
            epo.base.isOpenCN[7] == 1
    }


    companion object {
        @Volatile
        private var instance: ADHelper? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: ADHelper().also { instance = it }
            }
    }

}