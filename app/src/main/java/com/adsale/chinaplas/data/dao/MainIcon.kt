package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.LANG_EN
import com.adsale.chinaplas.utils.LANG_TC
import com.adsale.chinaplas.utils.getCurrLanguage

/**
 * Created by Carrie on 2019/10/16.
 */
@Entity(tableName = "MainIcon")
class MainIcon {

    @PrimaryKey
    var IconID: String = ""
    var TitleCN: String = ""
    var TitleTW: String = ""
    var TitleEN: String = ""
    var BaiDu_TJ: String = ""
    var Icon: String = ""
    var MenuSeq: Int = 0
    var DrawerSeq: Int = 0
    var IsDelete: Boolean = false
    var updatedAt: String = ""

    constructor(IconID: String,
                TitleCN: String,
                TitleTW: String,
                TitleEN: String,
                BaiDu_TJ: String,
                Icon: String,
                MenuSeq: Int,
                DrawerSeq: Int,
                IsDelete: Boolean,
                updatedAt: String) {
        this.IconID = IconID
        this.TitleCN = TitleCN
        this.TitleTW = TitleTW
        this.TitleEN = TitleEN
        this.BaiDu_TJ = BaiDu_TJ
        this.Icon = Icon
        this.MenuSeq = MenuSeq
        this.DrawerSeq = DrawerSeq
        this.IsDelete = IsDelete
        this.updatedAt = updatedAt
    }

//    @Ignore
//    var title = Transformations.map(currLanguage) {
//        val title =
//            if (it == 0) containsTitle(TitleTW) else if (it == 1) containsTitle(TitleEN) else containsTitle(TitleCN)
//        LogUtil.i("title:currLanguage=${getCurrLanguage()}, $title")
//        title
//    }

    fun getTitle(): String {
        return if (getCurrLanguage() == LANG_TC) containsTitle(TitleTW) else if (getCurrLanguage() == LANG_EN) containsTitle(
            TitleEN) else containsTitle(TitleCN)
    }

    private fun containsTitle(title: String): String {
        // todo 判断是否登录
        return if (title.contains("|")) title.split("|")[0] else title
    }

    override fun toString(): String {
        return "MainIcon(IconID='$IconID', TitleCN='$TitleCN', TitleTW='$TitleTW', TitleEN='$TitleEN', BaiDu_TJ='$BaiDu_TJ', Icon='$Icon', MenuSeq=$MenuSeq, DrawerSeq=$DrawerSeq, IsDelete=$IsDelete, updatedAt='$updatedAt')"
    }


}