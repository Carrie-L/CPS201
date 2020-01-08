package com.adsale.chinaplas.data.entity

import com.adsale.chinaplas.utils.LANG_EN
import com.adsale.chinaplas.utils.LANG_TC
import com.adsale.chinaplas.utils.getCurrLanguage
import com.adsale.chinaplas.utils.isTablet
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Carrie on 2019/10/17.
 */
@JsonClass(generateAdapter = true)   //  一定要加这句，不然字段值为null
class MainBanner(
    val function: Int,
    @Json(name = "page_id")
    val pageID: String,
    @Json(name = "image_en")
    val imageEN: String,
    @Json(name = "image_pad_en")
    val imagePadEN: String,
    @Json(name = "image_sc")
    val imageSC: String,
    @Json(name = "image_pad_sc")
    val imagePadSC: String,
    @Json(name = "image_tc")
    val imageTC: String,
    @Json(name = "image_pad_tc")
    val imagePadTC: String
) {

    fun getImage(): String {
        if (isTablet()) {
            return if (getCurrLanguage() == LANG_TC) imagePadTC else if (getCurrLanguage() == LANG_EN) imagePadEN else imagePadSC
        } else {
            return if (getCurrLanguage() == LANG_TC) imageTC else if (getCurrLanguage() == LANG_EN) imageEN else imageSC
        }
    }

    override fun toString(): String {
        return "MainBanner(functionEntity=$function, pageID='$pageID', imageEN='$imageEN', imagePadEN='$imagePadEN', imageSC='$imageSC', imagePadSC='$imagePadSC', imageTC='$imageTC', imagePadTC='$imagePadTC')"
    }
}