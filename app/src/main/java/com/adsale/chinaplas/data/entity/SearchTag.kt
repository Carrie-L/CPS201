package com.adsale.chinaplas.data.entity

import com.adsale.chinaplas.utils.LANG_EN
import com.adsale.chinaplas.utils.LANG_SC
import com.adsale.chinaplas.utils.getCurrLanguage
import com.squareup.moshi.JsonClass

/**
 * Created by Carrie on 2020/3/5.
 */
@JsonClass(generateAdapter = true)
data class SearchTag(
    val function: Int,
    val id: String,
    val name_en: String,
    val name_sc: String,
    val name_tc: String
) {
    override fun toString(): String {
        return "SearchTag(function=$function, id='$id', name_en='$name_en', name_sc='$name_sc', name_tc='$name_tc')"
    }


    fun getName(): String {
        return when (getCurrLanguage()) {
            LANG_SC -> name_sc
            LANG_EN -> name_en
            else -> name_tc
        }
    }

}