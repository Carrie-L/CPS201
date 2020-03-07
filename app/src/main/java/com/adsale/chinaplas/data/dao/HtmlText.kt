package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.LANG_EN
import com.adsale.chinaplas.utils.LANG_SC
import com.adsale.chinaplas.utils.getCurrLanguage
import com.adsale.chinaplas.utils.getName

/**
 * Created by Carrie on 2019/12/25.
 */
@Entity
data class HtmlText(
    @PrimaryKey
    val id: String,
    val groupID: String,
    val title_sc: String,
    val title_tc: String,
    val title_en: String,
    val group_title: String,
    val text: String) {

    @Transient
    @Ignore
    var isTypeLabel = false

    fun getTitle(): String {
        return when (getCurrLanguage()) {
            LANG_EN -> title_en
            LANG_SC -> title_sc
            else -> title_tc
        }
    }

    fun getGroupTitle(): String {
        if (group_title.contains(","))
            return getName(group_title.split(",")[0], group_title.split(",")[1], group_title.split(",")[2])
        else
            return group_title
    }

    override fun toString(): String {
        return "HtmlText(id='$id', groupID='$groupID', title_sc='$title_sc', title_tc='$title_tc', title_en='$title_en', text='$text')"
    }


}